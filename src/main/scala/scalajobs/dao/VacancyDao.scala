package scalajobs.dao

import java.util.UUID

import scalajobs.configuration.DbConnection.DBTransactor
import scalajobs.dao.impl.VacancyDaoImpl
import scalajobs.model.dbParams.VacancyDbParams
import scalajobs.model.{Vacancy, VacancyFilter}
import zio.logging.Logging
import zio.{Has, Task, ZLayer}
import zio.macros.accessible

@accessible
object VacancyDao {
  type VacancyDao = Has[Service]

  trait Service {
    def list(filters: List[VacancyFilter]): Task[Vector[Vacancy]]
    def get(id: UUID): Task[Option[Vacancy]]
    def create(params: VacancyDbParams): Task[Option[Vacancy]]
    def deleteAll: Task[Int]
  }

  val live: ZLayer[DBTransactor, Nothing, VacancyDao] =
    ZLayer.fromService(new VacancyDaoImpl(_))
}
