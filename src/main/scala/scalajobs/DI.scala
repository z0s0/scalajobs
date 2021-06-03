package scalajobs

import scalajobs.configuration.{Configuration, DbConnection}
import zio.blocking.Blocking
import scalajobs.dao.{Layer => DAOLayer}
import scalajobs.service.{Layer => ServiceLayer}
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend

object DI {
  val live =
    (Configuration.allConfigs ++ Blocking.live) >+>
      Migrations.live >+>
      Migrations.afterMigrations >>>
      DbConnection.transactorLive >>>
      (DAOLayer.live ++ Configuration.allConfigs ++ AsyncHttpClientZioBackend.layer()) >>>
      ServiceLayer.live ++ Configuration.allConfigs
}
