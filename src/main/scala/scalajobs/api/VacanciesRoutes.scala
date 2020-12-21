package scalajobs.api

import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import zio.{Has, Task, URLayer, ZLayer}
import io.circe.generic.auto._
import zio.interop.catz._
import org.http4s.circe._
import scalajobs.model.form.VacancyForm
import scalajobs.model.{CreateVacancyResponse, VacancyFilter}
import scalajobs.service.VacancyService
import scalajobs.service.VacancyService.VacancyService

object VacanciesRoutes {
  type VacanciesRoutes = Has[Service]

  trait Service {
    def route: HttpRoutes[Task]
  }

  val live: URLayer[VacancyService, VacanciesRoutes] =
    ZLayer.fromService(new VacanciesRouter(_))
}

final class VacanciesRouter(srv: VacancyService.Service)
    extends Http4sDsl[Task]
    with VacanciesRoutes.Service {

  implicit def circeJsonDecoder[A: Decoder]: EntityDecoder[Task, A] = jsonOf
  implicit def circeJsonEncoder[A: Encoder]: EntityEncoder[Task, A] =
    jsonEncoderOf

  override def route: HttpRoutes[Task] = HttpRoutes.of[Task] {
    case GET -> Root / "vacancies" / UUIDVar(id) =>
      srv.get(id).flatMap {
        case Some(value) => Ok(value)
        case None        => NotFound("vacancy does not exist")
      }

    case GET -> Root / "vacancies" :? salaryFrom(salaryFrom) +& salaryTo(
          salaryTo
        ) +& actual(actualFlag) => {
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

      val withActual =
        if (actualFlag.nonEmpty)
          VacancyFilter.Actual(actualFlag.get) :: withSalaryTo
        else
          withSalaryTo

      Ok(srv.list(withActual))
    }

    case req @ POST -> Root / "vacancies" =>
      req.decode[VacancyForm] { params =>
        srv.create(params).flatMap {
          case CreateVacancyResponse.Created(vacancy) => Created(vacancy)
          case CreateVacancyResponse.Invalid          => UnprocessableEntity()
        }
      }
  }

  object salaryFrom extends OptionalQueryParamDecoderMatcher[Int]("salaryFrom")
  object salaryTo extends OptionalQueryParamDecoderMatcher[Int]("salaryTo")
  object actual extends OptionalQueryParamDecoderMatcher[Boolean]("actual")
}
