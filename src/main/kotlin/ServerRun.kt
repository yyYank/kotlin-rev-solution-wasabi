package kotlin.rev.solution.wasabi

import org.wasabi.app.AppConfiguration
import kotlin.rev.solution.wasabi.CustomFaviconInterceptor
import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.app.AppServer
import org.wasabi.interceptors.*
import org.wasabi.routing.InterceptOn
import org.wasabi.routing.RouteHandler
import org.wasabi.routing.routeHandler
import rx.functions.Function
import kotlin.platform.platformStatic
import java.io.File

public class ServerRun {
    companion object {

        /**
         * main
         */
        platformStatic fun main(args: Array<String>) {
            val port = System.getenv("PORT") ?: "8080"
            val config = AppConfiguration(port.toInt())
            val server = AppServer(config)
            server.enableContentNegotiation()
            registerPage(server)
            defineContentType(server)
            server.start()
        }


        /**
         * htmlの入ったdirのみtrueを返す
         */
        fun isTargetDir(s: String) = !s.contains("css") && !s.contains("js")

        /**
         * lsコマンド的なもの
         */
        fun listSegments(dir : String) : List<out String> {
            return File(dir)
                    .listFiles()
                    .map {"${dir.replace("site", "")}/${it.getName()}"}
                    .toList()
        }

        /**
         * pageの登録
         */
        fun registerPage(server : AppServer){
            server.serveStaticFilesFromFolder("site")
            // top
            server.get("/", {
                this.response.redirect("index.html")
            })
            // favicon
            server.serveFavIconAt("site/img/favicon.ico")
            // 各ページ
            File("site").listFiles()
                    .map{l -> l.getName()}
                    .filter{s -> isTargetDir(s)}
                    .forEach{
                        server.serveStaticFilesFromFolder(it)
                        if(!it.contains("html")) {
                            server.get("/${it}", {
                                response.redirect("/${it}/index.html")
                            })
                        }
                    }
        }

        /**
         * contentTypeの設定
         */
        fun defineContentType(server : AppServer) {
            // getにcontentTypeはめていく関数
            val httpGets = {
                list: List<out String>, type: String ->
                list.forEach{
                    server.get(it, {
                        response.contentType = type
                    })
                }
            }

            val jsList = listSegments("site/js")
            val cssList = listSegments("site/css")
            // js
            httpGets(jsList, "text/javascript")
            // css
            httpGets(cssList, "text/css")
            // img
            server.get("/img/grid.png", {
                response.contentType = "image/png"
            })

            //webfont ttf
            server.get("/fonts/fontawesome-webfont.ttf", {
                response.streamFile("site/fonts/fontawesome-webfont.ttf", "application/font-ttf")
            })

            //webfont woff
            server.get("/fonts/fontawesome-webfont.woff", {
                response.streamFile("site/fonts/fontawesome-webfont.woff", "application/font-woff")
            })
        }


    }
}

