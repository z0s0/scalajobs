package scalajobs.dao

import scalajobs.configuration.DbConnection.DBTransactor
import scalajobs.model.Vacancy
import zio.{Has, Task, ZLayer}

object VacancyDao {
  type VacancyDao = Has[Service]

  trait Service {
    def list: Task[Vector[String]]
  }

  val live: ZLayer[DBTransactor, Throwable, VacancyDao] =
    ZLayer.fromService { tr =>
      import doobie._
      import doobie.implicits._
      import zio.interop.catz._

      new Service {
        override def list: Task[Vector[String]] = {
          val kek = sql"""select description from vacancies"""
            .query[String]
            .to[Vector]
            .transact(tr)

          Task.succeed(Vector("pidor", "pider", "raul"))
        }
      }
    }
}
