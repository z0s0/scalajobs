package scalajobs.dao

import java.util.UUID

import scalajobs.configuration.DbConnection.DBTransactor
import scalajobs.dao.impl.VacancyDaoImpl
import scalajobs.model.dbParams.VacancyDbParams
import scalajobs.model.{DbError, Vacancy, VacancyFilter}
import zio.{Has, IO, Task, ZLayer}
import zio.macros.accessible

@accessible
object VacancyDao {
  type VacancyDao = Has[Service]

  trait Service {
    def list(filters: List[VacancyFilter]): Task[Vector[Vacancy]]
    def get(id: UUID): Task[Option[Vacancy]]
    def create(params: VacancyDbParams): IO[DbError, Vacancy]
    private[dao] def deleteAll: Task[Int]
    private[dao] def approve(vacancyId: UUID): Task[Unit]
  }

  val live: ZLayer[DBTransactor, Nothing, VacancyDao] =
    ZLayer.fromService(new VacancyDaoImpl(_))
}
