package kotlin.rev.solution.wasabi

import org.wasabi.http.Request
import org.wasabi.http.Response
import io.netty.handler.codec.http.HttpMethod
import org.wasabi.app.AppServer
import org.wasabi.interceptors.Interceptor
import org.wasabi.routing.InterceptOn


public class CustomFaviconInterceptor(val icon: String): Interceptor() {

    override fun intercept(request: Request, response: Response) {
        if (request.method == HttpMethod.GET && request.uri.contains("/favicon.ico")) {
            val path = sanitizePath(icon)
            response.streamFile(path, "image/x-icon")
        } else {
            next()
        }
    }

}

fun sanitizePath(path: String): String {
    var sanitizedPath = path.trimTrailing("/")
    if (path.startsWith("/")) {
        sanitizedPath = path.dropWhile { it == '/' }
    }
    return sanitizedPath
}

fun AppServer.serveFavIconAt(icon: String) {
    intercept(CustomFaviconInterceptor(icon))
}

