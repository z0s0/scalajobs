package scalajobs.dao

import scalajobs.configuration.DbConnection.DBTransactor
import scalajobs.dao.OrganizationDao.OrganizationDao
import scalajobs.dao.VacancyDao.VacancyDao
import zio.ZLayer
import zio.logging.Logging

object Layer {
  type Daos = VacancyDao with OrganizationDao

  val live
    : ZLayer[DBTransactor, Nothing, Daos] = VacancyDao.live ++ OrganizationDao.live
}
