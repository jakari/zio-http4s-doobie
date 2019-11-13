package example.routes

import example.models.CreateTask
import example.repository.ExampleRepository
import example.repository.examples
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio._
import zio.interop.catz._

case class ExampleEndpoint[R <: ExampleRepository]() {
  type RoutesTask[A] = RIO[R, A]

  val dsl: Http4sDsl[RoutesTask] = Http4sDsl[RoutesTask]
  import dsl._
  import example.JsonCodec._

  def routes: HttpRoutes[RoutesTask] = {
    HttpRoutes.of[RoutesTask] {
      case GET -> Root => examples.list.foldM(_ => NotFound(), Ok(_))
      case request @ POST -> Root => for {
        task <- request.as[CreateTask]
        taskId <- examples.create(task).map(_.toString).foldM(_ => NotFound(), Ok(_))
      } yield taskId
    }
  }
}
