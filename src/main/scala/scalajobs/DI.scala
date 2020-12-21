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

object DI {
  val logging = Slf4jLogger.makeWithAnnotationsAsMdc(Nil)

  val live =
    (Configuration.allConfigs ++ ZLayer
      .requires[Blocking with Logging with Clock] ++ logging) >+>
      Migrations.live >+>
      Migrations.afterMigrations >>>
      (DbConnection.transactorLive ++ logging) >>>
      (VacancyDao.live ++ OrganizationDao.live ++ VacancyCache.live ++ OrganizationCache.live) >>>
      (VacancyService.live ++ OrganizationService.live) >>>
      (VacanciesRoutes.live ++ OrganizationsRoutes.live ++ Configuration.allConfigs)

}
