package scalajobs.service

import java.util.UUID

import scalajobs.dao.VacancyDao
import scalajobs.dao.VacancyDao.VacancyDao
import scalajobs.model.dbParams.VacancyDbParams
import scalajobs.model.form.VacancyForm
import scalajobs.model.{ClientError, DbError, Helpers, Vacancy, VacancyFilter}
import zio.{Has, IO, Task, ZIO, ZLayer}

object VacancyService {
  type VacancyService = Has[Service]

  trait Service {
    def get(id: UUID): Task[Option[Vacancy]]
    def list(filters: List[VacancyFilter]): Task[Vector[Vacancy]]
    def create(params: VacancyForm): IO[ClientError, Vacancy]
  }

  val live: ZLayer[VacancyDao, Nothing, VacancyService] =
    ZLayer.fromFunction[VacancyDao, VacancyService.Service] { ctx =>
      val dao = ctx.get[VacancyDao.Service]

      new Service {
        override def get(id: UUID): Task[Option[Vacancy]] = dao.get(id)

        override def list(filters: List[VacancyFilter]): Task[Vector[Vacancy]] =
          dao.list(filters)

        override def create(form: VacancyForm): IO[ClientError, Vacancy] = {
          val dbParams = VacancyDbParams(
            title = form.title.get,
            description = form.description.get,
            salaryFrom = form.salaryFrom.get,
            salaryTo = form.salaryTo.get,
            organizationId = UUID.fromString(form.organizationId.get),
            currency = form.currency.get,
            officePresence = form.officePresence.get,
            expiresAt = Helpers.localDateTimeFromISO8601(form.expiresAt.get),
            contactEmail = form.contactEmail.get,
            link = form.link.get
          )

          dao.create(dbParams).mapError {
            case DbError.Conflict(reason) => ClientError.Invalid(reason)
            case DbError.Disaster         => ClientError.Disaster
          }
        }
      }
    }

  def list(
    filter: List[VacancyFilter]
  ): ZIO[VacancyService, Throwable, Vector[Vacancy]] =
    ZIO.accessM(_.get.list(filter))

  def get(id: UUID): ZIO[VacancyService, Throwable, Option[Vacancy]] =
    ZIO.accessM(_.get.get(id))

  def create(params: VacancyForm): ZIO[VacancyService, ClientError, Vacancy] =
    ZIO.accessM(_.get.create(params))
}
