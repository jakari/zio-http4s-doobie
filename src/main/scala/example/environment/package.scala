package example

import example.database.{Connection, Transaction}
import example.repository.ExampleRepository
import zio.clock.Clock

package object environment {
  type AppEnvironment = ExampleRepository with Transaction with Clock
}
