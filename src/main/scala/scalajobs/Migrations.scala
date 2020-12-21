package scalajobs

import org.flywaydb.core.Flyway
import scalajobs.configuration.DbConfig
import zio.blocking.{Blocking, effectBlocking}
import zio.clock.Clock
import zio.logging.Logging
import zio._

object Migrations {
  type Migrations = Has[Service]

  type Env = Blocking with Logging with Clock

  trait Service {
    def applyMigrations(): URIO[Env, Unit]
  }

  sealed trait AfterMigrations
  type WithMigrations = Has[AfterMigrations]

  val live: URLayer[Has[DbConfig], Migrations] =
    ZLayer.fromService { config => () =>
      effectBlocking {
        Flyway
          .configure()
          .dataSource(config.url, config.user, config.password)
          .load()
          .migrate()
      }.orDie.unit
    }

  val afterMigrations: URLayer[Env with Migrations, WithMigrations] = ZIO
    .service[Service]
    .flatMap(_.applyMigrations())
    .as(new AfterMigrations {})
    .toLayer
}