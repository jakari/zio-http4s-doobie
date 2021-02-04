package example.service

import pureconfig._
import pureconfig.generic.auto._

object AppConfiguration {
  def appConfig: Config = ConfigSource.default.loadOrThrow[Config]
  def testConfig: Config = ConfigSource
    .resources("application.test.conf")
    .loadOrThrow[Config]
}

case class Config(
  database: Database
)

case class Database(
  name: String,
  host: String,
  username: String,
  password: String
)
