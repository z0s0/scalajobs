package scalajobs.service

import java.util.UUID

import scalajobs.cache.VacancyCache
import scalajobs.cache.VacancyCache.VacanciesCache
import scalajobs.dao.VacancyDao
import scalajobs.dao.VacancyDao.VacancyDao
import scalajobs.model.{
  CreateVacancyResponse,
  Vacancy,
  VacancyFilter,
  VacancyParams
}
import zio.{Has, Task, ZLayer}

object VacancyService {
  type VacancyService = Has[Service]

  trait Service {
    def get(id: UUID): Task[Option[Vacancy]]
    def list(filters: List[VacancyFilter]): Task[Vector[Vacancy]]
    def create(params: VacancyParams): Task[CreateVacancyResponse]
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
          params: VacancyParams
        ): Task[CreateVacancyResponse] = ???
      }
    }

}
