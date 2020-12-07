package scalajobs

import scalajobs.api.OrganizationsRoutes.OrganizationsRoutes
import scalajobs.api.{OrganizationsRoutes, VacanciesRoutes}
import scalajobs.api.VacanciesRoutes.VacanciesRoutes
import scalajobs.configuration.Configuration.AllConfigs
import scalajobs.configuration.{Configuration, DbConnection}
import scalajobs.dao.OrganizationDao.OrganizationDao
import scalajobs.dao.VacancyDao.VacancyDao
import scalajobs.dao.{OrganizationDao, VacancyDao}
import zio.ZLayer
import zio.blocking.Blocking

object DI {
  val live =
    (Configuration.allConfigs ++ ZLayer.requires[Blocking]) >>>
      (DbConnection.transactorLive) >>>
      (VacancyDao.live ++ OrganizationDao.live) >>>
      (VacanciesRoutes.live ++ OrganizationsRoutes.live ++ Configuration.allConfigs)

}
