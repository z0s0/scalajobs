package scalajobs.api

import java.util.UUID

import io.circe.Encoder
import zio.{Has, Task, ZLayer}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import zio._
import zio.interop.catz._
import org.http4s.HttpRoutes
import scalajobs.model.Organization._
import scalajobs.model.{GetOrganizationResponse, Organization}
import scalajobs.service.OrganizationService
import scalajobs.service.OrganizationService.OrganizationService
import sttp.model.StatusCode
import sttp.tapir.Endpoint
import sttp.tapir.json.circe._
import sttp.tapir.ztapir._
import zio.clock.Clock
import sttp.tapir.generic.auto._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter

object OrganizationsRoutes {
  type OrganizationsRoutes = Has[HttpRoutes[RIO[Clock, *]]]

  val organizationsDocs =
    endpoint.get
      .in("organizations")
      .out(jsonBody[Vector[Organization]])

  val organizationDocs =
    endpoint.get
      .in("organizations" / path[UUID])
      .out(jsonBody[Organization])
      .errorOut(stringBody)
      .errorOut(statusCode(StatusCode.NotFound))

  val live: URLayer[OrganizationService, OrganizationsRoutes] =
    ZLayer.fromService { srv =>
      val orgRoute = organizationsDocs.zServerLogic { _ =>
        srv.list.orElseFail(())
      }

      ZHttp4sServerInterpreter.from(orgRoute).toRoutes
    }

}

final class OrganizationsRouter(srv: OrganizationService.Service)
    extends Http4sDsl[Task] {

  implicit def circeJsonEncoder[A](
    implicit encoder: Encoder[A]
  ): EntityEncoder[Task, A] = jsonEncoderOf

  def route: HttpRoutes[Task] = HttpRoutes.of[Task] {
    case GET -> Root / "organizations" => Ok(srv.list)

    case GET -> Root / "organizations" / UUIDVar(id) =>
      srv.get(id).flatMap {
        case GetOrganizationResponse.Found(organization) => Ok(organization)
        case GetOrganizationResponse.NotFound            => NotFound()
      }
  }
}
