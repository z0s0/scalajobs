package scalajobs.support

import org.testcontainers.containers.PostgreSQLContainer
import scalajobs.configuration.DbConfig
import zio.{Has, URLayer, ZLayer}
import zio.blocking.effectBlocking

object PostgreSQLContainerLayer {
  val container = ZLayer.fromAcquireRelease(effectBlocking {
    val cont = new PostgreSQLContainer("postgres:12")
    cont.start()
    cont
  }.orDie)(cont => effectBlocking(cont.stop()).orDie)

  val config: URLayer[Has[PostgreSQLContainer[Nothing]], Has[DbConfig]] =
    ZLayer.fromService { cont =>
      DbConfig(
        url = cont.getJdbcUrl,
        user = cont.getUsername,
        password = cont.getPassword
      )
    }

  val live = container >+> config
}
