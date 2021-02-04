package example.environment

import doobie.Transactor
import example.database.{Connection, Transaction}
import example.repository.ExampleRepository
import zio._
import zio.clock.Clock

object EnvBuilder {
  def buildLiveEnv(transactor: Transactor[Task]): TaskLayer[AppEnvironment] = {
    val connection = Connection.live(transactor)
    val transaction = connection >>> Transaction.live

    val exampleRepository = transaction >>> ExampleRepository.live

    exampleRepository ++ transaction ++ Clock.live
  }
}
