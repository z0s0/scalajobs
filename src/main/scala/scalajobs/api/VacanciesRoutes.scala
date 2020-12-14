package scalajobs.api

import io.circe.Encoder
import org.http4s.{EntityEncoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import scalajobs.dao.VacancyDao.VacancyDao
import zio.{Has, Task, URLayer, ZLayer}
import io.circe.generic.auto._
import zio.interop.catz._
import org.http4s.circe._
import scalajobs.dao.VacancyDao
import scalajobs.model.VacancyFilter

object VacanciesRoutes {
  type VacanciesRoutes = Has[Service]

  trait Service {
    def route: HttpRoutes[Task]
  }

  val live: URLayer[VacancyDao, VacanciesRoutes] =
    ZLayer.fromService(new VacanciesRouter(_))
}

final class VacanciesRouter(dao: VacancyDao.Service)
    extends Http4sDsl[Task]
    with VacanciesRoutes.Service {
  implicit def circeJsonEncoder[A](
    implicit decoder: Encoder[A]
  ): EntityEncoder[Task, A] = jsonEncoderOf

  override def route: HttpRoutes[Task] = HttpRoutes.of[Task] {
    case GET -> Root / "vacancies" / UUIDVar(id) => Ok(dao.get(id))
    case GET -> Root / "vacancies" :? salaryFrom(salaryFrom) +& salaryTo(
          salaryTo
        ) => {
      // TODO make generic and look ok
      val filters = List[VacancyFilter]()
      val withSalaryFrom =
        if (salaryFrom.nonEmpty)
          VacancyFilter.SalaryFrom(salaryFrom.get) :: filters
        else
          filters

      val withSalaryTo =
        if (salaryTo.nonEmpty)
          VacancyFilter.SalaryTo(salaryTo.get) :: withSalaryFrom
        else
          withSalaryFrom

      Ok(dao.list(withSalaryTo))
    }
  }

  object salaryFrom extends OptionalQueryParamDecoderMatcher[Int]("salaryFrom")
  object salaryTo extends OptionalQueryParamDecoderMatcher[Int]("salaryTo")
}
