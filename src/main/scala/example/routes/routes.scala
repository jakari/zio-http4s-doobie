package example

import cats.data.Kleisli
import example.environment.AppEnvironment
import org.http4s.{Request, Response}
import org.http4s.implicits._
import org.http4s.server.Router
import zio.RIO
import zio.interop.catz._

package object routes {
  type RouteRIO[A] = RIO[AppEnvironment, A]

  def routes(): Kleisli[RouteRIO, Request[RouteRIO], Response[RouteRIO]] =
    Router[RouteRIO](
      "/examples" -> ExampleEndpoint().routes
    ).orNotFound
}
