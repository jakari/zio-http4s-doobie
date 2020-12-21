package example

import example.database.{Connection, DBAL}
import example.repository.ExampleRepository
import zio.clock.Clock

package object environment {
  type AppEnvironment = ExampleRepository with Clock with Connection
}
