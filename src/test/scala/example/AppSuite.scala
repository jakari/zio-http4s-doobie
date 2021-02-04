package example

import example.helpers.DatabaseSuite
import example.environment.{AppEnvironment, EnvBuilder}
import example.database.{Connection, Transaction}
import example.database.Migrations.Migrations
import example.service.AppConfiguration
import io.circe.Json
import org.http4s.circe._
import org.http4s.Request
import org.http4s._
import org.http4s.dsl.io._
import org.scalatest.{FunSuite, Tag}
import zio._
import zio.interop.catz._

object TestEnv {
  lazy val connection = {
    val transactor = Connection.createSimpleTransactor(AppConfiguration.testConfig)

    val program = Migrations.up()
    Runtime.default.unsafeRun(program.provideLayer(Connection.live(transactor) >>> Transaction.live))
    transactor
  }

  lazy val env: Layer[Any, AppEnvironment] = EnvBuilder.buildLiveEnv(connection)
}

abstract class AppSuite extends FunSuite with DatabaseSuite {
  val app = routes.routes()

  import routes._

  def postRequest[E](path: String, entity: E)(implicit w: EntityEncoder[RouteRIO, E]) = {
    app.run(Request(
      method = POST,
      uri = Uri.unsafeFromString(path)
    ).withEntity(entity))
  }

  def getReqquest(location: String): RouteRIO[Response[RouteRIO]] = {
    app.run(Request(
      method = GET,
      uri = Uri.unsafeFromString(location)))
  }

  def effectfulTest(testName: String, testTags: Tag*)(testFun: ZIO[AppEnvironment, _, _]): Unit =
    test(testName, testTags:_*)(runEffect(testFun))

  def runEffect[A](program: ZIO[AppEnvironment, _, A]): A = {
    Runtime.default.unsafeRun(program.provideLayer(TestEnv.env))
  }

  def assertResponse[F[_]](response: Response[F], expectedStatusCode: Status): Unit =
    assert(response.status.code == expectedStatusCode.code)

  def assertResponse[A](
    response: Response[RouteRIO],
    expectedStatusCode: Status,
    expectedBody: String): ZIO[AppEnvironment, Throwable, Unit] = {
    assertResponse(response, expectedStatusCode)

    for {
      body <- response.as[String]
      _ = assert(body == expectedBody)
    } yield ()
  }

  def assertResponse[A](
    response: Response[RouteRIO],
    expectedStatusCode: Status,
    expectedBody: Json
  ): ZIO[AppEnvironment, Throwable, Unit] = {
    assertResponse(response, expectedStatusCode)
    for {
      r <- response.as[Json]
      _ = assert(r == expectedBody)
    } yield ()
  }
}
