package example.database

import cats.effect.Blocker
import example.database.Connection.Service
import example.service.Config
import doobie._
import doobie.hikari._
import zio._
import zio.ZIO
import zio.interop.catz._

trait Connection {
  def connection: Service
}

object Connection {
  def create(config: Config): Managed[Throwable, HikariTransactor[Task]] = {
    // Resource yielding a transactor configured with a bounded connect EC and an unbounded
    // transaction EC. Everything will be closed and shut down cleanly after use.

    val xa = for {
      ce <- ExecutionContexts.fixedThreadPool[Task](32) // our connect EC
      te <- Blocker[Task] // our transaction EC
      xa <- HikariTransactor.newHikariTransactor[Task](
        "org.postgresql.Driver",
        s"jdbc:postgresql://${config.database.host}/" + config.database.name,
        config.database.username,
        config.database.password,
        ce,
        te
      )
    } yield xa

    val res = xa
      .allocated
      .map { case (transactor, cleanupM) =>
        Reservation(ZIO.succeed(transactor), _ => cleanupM.orDie)
      }.uninterruptible

    Managed(res)
  }

  def createSimpleTransactor(config: Config): Transactor[Task] = {
    Transactor.fromDriverManager(
      "org.postgresql.Driver",
      "jdbc:postgresql:" + config.database.name,
      config.database.username,
      config.database.password
    )
  }

  trait Service {
    def xa: Transactor[Task]
  }
}

