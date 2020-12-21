package example

import zio.Has

package object repository {
  type ExampleRepository = Has[ExampleRepository.Service]
}
