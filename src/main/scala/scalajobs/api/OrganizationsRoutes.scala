package scalajobs.api

import io.circe.Encoder
import scalajobs.dao.OrganizationDao.OrganizationDao
import zio.{Has, Task, ZLayer}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.Http4sDsl._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import zio._
import zio.interop.catz._
import org.http4s.HttpRoutes
import scalajobs.model.Organization._
import scalajobs.dao.OrganizationDao
import scalajobs.model.GetOrganizationResponse
import scalajobs.service.OrganizationService
import scalajobs.service.OrganizationService.OrganizationService

object OrganizationsRoutes {
  type OrganizationsRoutes = Has[Service]

  trait Service {
    def route: HttpRoutes[Task]
  }

  val live: URLayer[OrganizationService, OrganizationsRoutes] =
    ZLayer.fromService(new OrganizationsRouter(_))
}

final class OrganizationsRouter(srv: OrganizationService.Service)
    extends Http4sDsl[Task]
    with OrganizationsRoutes.Service {

  implicit def circeJsonEncoder[A](
    implicit encoder: Encoder[A]
  ): EntityEncoder[Task, A] = jsonEncoderOf

  override def route: HttpRoutes[Task] = HttpRoutes.of[Task] {
    case GET -> Root / "organizations" => Ok(srv.list)

    case GET -> Root / "organizations" / UUIDVar(id) =>
      srv.get(id).flatMap {
        case GetOrganizationResponse.Found(organization) => Ok(organization)
        case GetOrganizationResponse.NotFound            => NotFound()
      }
  }
}
