package example.routes

import doobie.implicits._
import io.circe.syntax._
import org.http4s.Status
import org.http4s.circe._
import io.circe.generic.auto._

import scala.language.implicitConversions
import example.AppSuite
import example.database.db
import example.JsonCodec._
import example.models.Example
import example.helpers.builder._
import io.circe.Decoder.Result
import zio.interop.catz.taskConcurrentInstance


class ExampleSuite extends AppSuite {
  effectfulTest("Should create a example row") {
    val name = "example-name"

    for {
      _ <- resetDatabase
      response <- postRequest(
        "/examples",
        s"""{
           |"name": "$name"
           |}""".stripMargin
      )
      examples <- transacted.query[Example](sql"SELECT * FROM example")
      _ = assert(examples.size == 1, "There should be exactly one example created inside the database")
        body <- response.asJson.map(_.hcursor)
      _ <- assertResponse(response, Status.Ok, examples.head.asJson)
      _ = assertExample(body.get[Long]("id"), body.get[String]("name"), examples.head)
    } yield ()
  }

  effectfulTest("Should get list of examples") {
    for {
      _ <- exampleBuilder.build
      _ <- exampleBuilder.build
      response <- getReqquest(s"/examples")
      examples <- transacted.query[Example](
        sql"""
             |SELECT
             |*
             |FROM example""".stripMargin
      )
      _ = assert(examples.size == 3, "Expects 3 stories")
      _ <- assertResponse(response, Status.Ok, examples.asJson)
    } yield ()
  }

  def assertExample(
    id: Long,
    name: String,
    actual: Example
  ): Unit = {
    assert(actual.name == name)
    assert(actual.id == id)
  }

  implicit def getJsonValue[A](value: Result[A]): A = value match {
    case Left(error) => throw error
    case Right(value) => value
  }
}
