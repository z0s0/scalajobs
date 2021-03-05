package scalajobs.api

import org.http4s.HttpRoutes
import scalajobs.model.CreateVacancyResponse
import scalajobs.service.VacancyService.VacancyService
import zio.{Has, IO, RIO, URLayer, ZLayer}
import zio.interop.catz._
import zio.clock.Clock
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.ztapir._

object VacanciesRoutes {
  type VacanciesRoutes = Has[HttpRoutes[RIO[Clock, *]]]

  val live: URLayer[VacancyService, VacanciesRoutes] =
    ZLayer.fromService { srv =>
      val getVacancyLogic = Docs.vacancyDocs.zServerLogic { id =>
        srv
          .get(id)
          .catchAll(_ => IO.fail("Internal Error"))
          .flatMap {
            case Some(vacancy) => IO.succeed(vacancy)
            case None          => IO.fail("not found")
          }
      }

      val listVacanciesLogic = Docs.vacanciesDocs.zServerLogic { filters =>
        srv.list(List()).orElseFail(())
      }

      val createVacancyLogic = Docs.createVacancyDocs.zServerLogic { form =>
        srv
          .create(form)
          .catchAll(_ => IO.fail("Internal err"))
          .flatMap {
            case CreateVacancyResponse.Created(vacancy) => IO.succeed(vacancy)
            case CreateVacancyResponse.Invalid          => IO.fail("invalid")
          }
      }

      ZHttp4sServerInterpreter
        .from(List(getVacancyLogic, listVacanciesLogic, createVacancyLogic))
        .toRoutes
    }
}
