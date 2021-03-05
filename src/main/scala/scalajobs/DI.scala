package scalajobs

import scalajobs.api.{OrganizationsRoutes, VacanciesRoutes}
import scalajobs.cache.{OrganizationCache, VacancyCache}
import scalajobs.configuration.{Configuration, DbConnection}
import scalajobs.dao.{OrganizationDao, VacancyDao}
import scalajobs.service.{OrganizationService, VacancyService}
import zio.ZLayer
import zio.blocking.Blocking
import zio.clock.Clock
import zio.logging.slf4j.Slf4jLogger
import zio.logging.Logging

import scalajobs.dao.{Layer => DAOLayer}
import scalajobs.api.{Layer => APILayer}
import scalajobs.service.{Layer => ServiceLayer}
import scalajobs.cache.{Layer => CacheLayer}

object DI {
  val logging = Slf4jLogger.makeWithAnnotationsAsMdc(Nil)

  val live =
    (Configuration.allConfigs ++ Blocking.live) >+>
      Migrations.live >+>
      Migrations.afterMigrations >>>
      DbConnection.transactorLive >>>
      (DAOLayer.live ++ CacheLayer.live) >>>
      ServiceLayer.live >>>
      OrganizationsRoutes.live ++ Configuration.allConfigs
}
