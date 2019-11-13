package example

import example.Server.AppEnvironment
import org.http4s.implicits._
import org.http4s.server.Router
import zio.RIO
import zio.interop.catz._

package object routes {
  type RouteRIO[A] = RIO[AppEnvironment, A]

  def routes() =
    Router[RouteRIO](
      "/examples" -> ExampleEndpoint().routes
    ).orNotFound
}
