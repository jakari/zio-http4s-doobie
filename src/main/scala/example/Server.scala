package example

import cats.effect.ExitCode
import example.database.Connection
import example.database.Connection.{create => createConnection}
import example.database.Migrations._
import example.repository.ExampleRepository
import example.service.AppConfiguration
import org.http4s.server.blaze._
import zio._
import zio.clock.Clock
import zio.interop.catz._

object Server extends App {
  type AppEnvironment = ExampleRepository with Connection with Clock
  type AppTask[A] = RIO[AppEnvironment, A]

  override def run(args: List[String]) = {
    val transactorR = createConnection(AppConfiguration.appConfig)
    val server = ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
      BlazeServerBuilder[AppTask]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(routes.routes())
        .withNio2(true)
        .serve
        .compile[AppTask, AppTask, ExitCode]
        .drain
    }
    val program = for {
      program <- transactorR.use { transactor =>
        val deps = new ExampleRepository with Clock.Live with Connection {
          override def connection: Connection.Service = new Connection.Service {
            override def xa: doobie.Transactor[Task] = transactor
          }
        }
        Migrations.up().provide(deps) *> server.provide(deps)
      }
    } yield program

    program.foldM(
      err => {
        println(s"Execution failed with: $err")
        IO.succeed(1)
      },
      _ => IO.succeed(0)
    )
  }
}
