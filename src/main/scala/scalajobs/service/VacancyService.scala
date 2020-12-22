package scalajobs.service

import java.time.LocalDateTime
import java.util.UUID

import scalajobs.cache.VacancyCache
import scalajobs.cache.VacancyCache.VacanciesCache
import scalajobs.dao.VacancyDao
import scalajobs.dao.VacancyDao.VacancyDao
import scalajobs.model.dbParams.VacancyDbParams
import scalajobs.model.form.VacancyForm
import scalajobs.model.{
  CreateVacancyResponse,
  Currency,
  OfficePresence,
  Organization,
  Vacancy,
  VacancyFilter
}
import zio.{Has, Task, ZLayer}

object VacancyService {
  type VacancyService = Has[Service]

  trait Service {
    def get(id: UUID): Task[Option[Vacancy]]
    def list(filters: List[VacancyFilter]): Task[Vector[Vacancy]]
    def create(params: VacancyForm): Task[CreateVacancyResponse]
  }

  type Dependencies = VacancyDao with VacanciesCache

  val live: ZLayer[Dependencies, Throwable, VacancyService] =
    ZLayer.fromFunction[Dependencies, VacancyService.Service] { ctx =>
      val dao = ctx.get[VacancyDao.Service]
      val cache = ctx.get[VacancyCache.Service]

      new Service {
        override def get(id: UUID): Task[Option[Vacancy]] =
          cache.get(id).flatMap {
            case res @ Some(_) => Task.succeed(res)
            case None          => dao.get(id)
          }

        override def list(filters: List[VacancyFilter]): Task[Vector[Vacancy]] =
          dao.list(filters)

        override def create(
          params: VacancyForm
        ): Task[CreateVacancyResponse] = {
          params match {
            case VacancyForm(
                None,
                Some(desc),
                Some(orgId),
                Some(salaryFrom),
                Some(salaryTo),
                Some(currency),
                Some(officePresence),
                Some(expiresAt),
                Some(contactEmail),
                Some(link)
                ) =>
              dao
                .create(
                  VacancyDbParams(
                    desc,
                    salaryFrom,
                    salaryTo,
                    orgId,
                    currency,
                    officePresence,
                    expiresAt,
                    contactEmail,
                    link
                  )
                )
                .flatMap {
                  case Some(vacancy) =>
                    Task.succeed(CreateVacancyResponse.Created(vacancy))
                  case None => Task.succeed(CreateVacancyResponse.Invalid)
                }
            case aa =>
              println(aa)
              Task.succeed(CreateVacancyResponse.Invalid)
          }
        }
      }
    }

}
