package scalajobs.cache

import java.util.UUID

import scalajobs.model.Vacancy
import zio.{Has, Task, ULayer, ZLayer}

object VacancyCache {
  type VacanciesCache = Has[Service]

  trait Service extends EntityCache[Task, UUID, Vacancy] {
    def get(id: UUID): Task[Option[Vacancy]]
    def set(vacancy: Vacancy): Task[Unit]
  }

  val live: ULayer[VacanciesCache] =
    ZLayer.succeed(new Service {
      override def get(id: UUID): Task[Option[Vacancy]] = Task.none

      override def set(vacancy: Vacancy): Task[Unit] = Task.unit
    })
}
