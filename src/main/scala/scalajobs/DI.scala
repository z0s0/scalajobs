package scalajobs

import scalajobs.configuration.{Configuration, DbConnection}
import zio.blocking.Blocking

import scalajobs.dao.{Layer => DAOLayer}
import scalajobs.service.{Layer => ServiceLayer}

object DI {
  val live =
    (Configuration.allConfigs ++ Blocking.live) >+>
      Migrations.live >+>
      Migrations.afterMigrations >>>
      DbConnection.transactorLive >>>
      (DAOLayer.live) >>>
      ServiceLayer.live ++ Configuration.allConfigs
}
