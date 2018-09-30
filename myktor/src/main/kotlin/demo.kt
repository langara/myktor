import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.OAuthServerSettings
import io.ktor.auth.oauth
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.origin
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

fun createDemoServer(port: Int = 80): NettyApplicationEngine {
    return embeddedServer(Netty, port = port) {
        install(Authentication) {
            oauth("google-oauth") {
                client = HttpClient(Apache)
                providerLookup = { googleOauthProvider }
                urlProvider = { redirectUrl("/login") }
            }
        }


        routing {
            get("/") {
                call.respondText("Hello World!", ContentType.Text.Plain)
            }
            get("/demo") {
                call.respondText("HELLO WORLD!")
            }
        }
    }
}

fun startDemoServer(port: Int = 80) {
    createDemoServer(port).start(wait = true)
}


val googleOauthProvider = OAuthServerSettings.OAuth2ServerSettings(
    name = "google",
    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
    accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
    requestMethod = HttpMethod.Post,

    clientId = System.getenv("MY_KTOR_WEB_CLIENT_ID"),
    clientSecret = System.getenv("MY_KTOR_WEB_CLIENT_SECRET"),
    defaultScopes = listOf("profile", "calendar.events") // TODO check: full path for calendar.events ?
)

private fun ApplicationCall.redirectUrl(path: String): String {
    val defaultPort = if (request.origin.scheme == "http") 80 else 443 // TODO check: maybe 8080?
    val hostPort = request.host()!! + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
    val protocol = request.origin.scheme
    return "$protocol://$hostPort$path"
}
