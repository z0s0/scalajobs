package scalajobs.configuration

import cats.effect.Blocker
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import scalajobs.db.Migrations
import scalajobs.db.Migrations.{Env, Migrations, WithMigrations}
import zio.blocking.{Blocking, blocking}
import zio.clock.Clock
import zio.{Has, Managed, Task, URLayer, ZIO, ZLayer}

import scala.concurrent.ExecutionContext
import zio.interop.catz._
import zio.logging.Logging

object DbConnection {
  type DBTransactor = Has[Transactor[Task]]

  type Deps = Has[DbConfig] with Blocking with Clock with Logging

  def mkTransactor(
    conf: DbConfig,
    connectEC: ExecutionContext,
    transactEC: ExecutionContext
  ): Managed[Throwable, Transactor[Task]] = {
    HikariTransactor
      .newHikariTransactor[Task](
        "org.postgresql.Driver",
        conf.url,
        conf.user,
        conf.password,
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      )
      .toManagedZIO
  }
  val transactorLive
    : ZLayer[Has[DbConfig] with Blocking, Throwable, DBTransactor] =
    ZLayer.fromManaged(for {
      conf <- ZIO.access[Has[DbConfig]](_.get).toManaged_
      connectEC <- ZIO.descriptor.map(_.executor.asEC).toManaged_
      blockingEC <- blocking { ZIO.descriptor.map(_.executor.asEC) }.toManaged_
      transactor <- mkTransactor(conf, connectEC, blockingEC)
    } yield transactor)

//  val live: ZLayer[Deps with Has[DbConfig] with Env with Migrations,
//                   Throwable,
//                   DBTransactor] =
//    (ZLayer
//      .requires[Deps] ++ Migrations.live >>> Migrations.afterMigrations) >>> transactorLive
}
