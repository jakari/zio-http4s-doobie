package example.environment

import doobie.Transactor
import example.database.{Connection, DBAL}
import example.repository.ExampleRepository
import zio._
import zio.clock.Clock

object EnvBuilder {
  def buildLiveEnv(transactor: Transactor[Task]): Layer[Any, AppEnvironment] = {
    val connection = Connection.live(transactor)
    val dbal = connection >>> DBAL.live
    val exampleRepository = dbal >>> ExampleRepository.live

    exampleRepository ++ connection ++ Clock.live
  }
}
