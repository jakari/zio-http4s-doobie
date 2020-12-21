package example

import example.database.Connection.{create => createConnection}
import example.database.Migrations._
import example.environment.EnvBuilder
import example.environment.AppEnvironment
import example.service.AppConfiguration
import org.http4s.server.blaze._
import zio.{App, ExitCode, RIO, URIO, ZIO}
import zio.interop.catz._

object Server extends App {
  type AppTask[A] = RIO[AppEnvironment, A]

  override def run(args: List[String]): URIO[Any, ExitCode] = {
    val server = ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
      BlazeServerBuilder[AppTask](rts.platform.executor.asEC)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(routes.routes())
        .serve
        .compile
        .drain
    }

    createConnection(AppConfiguration.appConfig)
      .use { transactor =>
        val deps = EnvBuilder.buildLiveEnv(transactor)
        Migrations.up().provideLayer(deps) *> server.provideLayer(deps)
      }
      .foldM(
        err => {
          println(s"Execution failed with: $err")
          ZIO.succeed(ExitCode.failure)
        },
        _ => ZIO.succeed(ExitCode.success)
      )
  }
}
