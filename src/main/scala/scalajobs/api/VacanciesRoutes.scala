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
    case GET -> Root / "vacancies" => Ok(dao.list)
  }
}
