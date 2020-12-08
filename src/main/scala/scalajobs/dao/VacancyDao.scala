package scalajobs.dao

import java.util.UUID

import scalajobs.configuration.DbConnection.DBTransactor
import scalajobs.dao.impl.VacancyDaoImpl
import scalajobs.model.Vacancy
import zio.logging.Logging
import zio.{Has, Task, ZLayer}

object VacancyDao {
  type VacancyDao = Has[Service]

  trait Service {
    def list: Task[Vector[Vacancy]]
    def get(id: UUID): Task[Option[Vacancy]]
  }

  val live: ZLayer[DBTransactor with Logging, Throwable, VacancyDao] =
    ZLayer.fromService(new VacancyDaoImpl(_))
}
