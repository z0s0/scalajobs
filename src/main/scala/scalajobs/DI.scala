package scalajobs

import scalajobs.api.OrganizationsRoutes.OrganizationsRoutes
import scalajobs.api.{OrganizationsRoutes, VacanciesRoutes}
import scalajobs.api.VacanciesRoutes.VacanciesRoutes
import scalajobs.configuration.Configuration.AllConfigs
import scalajobs.configuration.{Configuration, DbConnection}
import scalajobs.dao.OrganizationDao.OrganizationDao
import scalajobs.dao.VacancyDao.VacancyDao
import scalajobs.dao.{OrganizationDao, VacancyDao}
import scalajobs.db.Migrations
import zio.ZLayer
import zio.blocking.Blocking
import zio.clock.Clock
import zio.logging.Logging

object DI {
  val live =
    (Configuration.allConfigs ++ ZLayer
      .requires[Blocking with Logging with Clock]) >+>
      Migrations.live >+>
      Migrations.afterMigrations >>>
      (DbConnection.transactorLive) >>>
      (VacancyDao.live ++ OrganizationDao.live) >>>
      (VacanciesRoutes.live ++ OrganizationsRoutes.live ++ Configuration.allConfigs)

}
