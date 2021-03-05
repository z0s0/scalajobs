package scalajobs.api

import java.util.UUID

import io.circe.Encoder
import zio.{Has, Task, ZLayer}
import org.http4s.circe._
import org.http4s.EntityEncoder
import zio._
import zio.interop.catz._
import org.http4s.HttpRoutes
import scalajobs.model.Organization._
import scalajobs.model.{GetOrganizationResponse, Organization}
import scalajobs.service.OrganizationService
import scalajobs.service.OrganizationService.OrganizationService
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.ztapir._
import zio.clock.Clock
import sttp.tapir.generic.auto._
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter

object OrganizationsRoutes {
  type OrganizationsRoutes = Has[HttpRoutes[RIO[Clock, *]]]

  val live: URLayer[OrganizationService, OrganizationsRoutes] =
    ZLayer.fromService { srv =>
      val organizationsLogic = Docs.organizationsDocs.zServerLogic { _ =>
        srv.list.orElseFail(())
      }

      val organizationLogic = Docs.organizationDocs.zServerLogic { id =>
        srv
          .get(id)
          .catchAll(_ => IO.fail("Internal Error"))
          .flatMap {
            case GetOrganizationResponse.Found(organization) =>
              IO.succeed(organization)
            case GetOrganizationResponse.NotFound => IO.fail("not found")
          }
      }

      ZHttp4sServerInterpreter
        .from(List(organizationsLogic, organizationLogic))
        .toRoutes
    }

}
