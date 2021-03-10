package scalajobs.service

import java.util.UUID

import scalajobs.cache.OrganizationCache
import scalajobs.cache.OrganizationCache.OrganizationCache
import scalajobs.dao.OrganizationDao
import scalajobs.dao.OrganizationDao.OrganizationDao
import scalajobs.model.{GetOrganizationResponse, Organization}
import zio.macros.accessible
import zio.{Has, Task, URLayer, ZLayer}

@accessible
object OrganizationService {
  type OrganizationService = Has[Service]

  trait Service {
    def get(id: UUID): Task[GetOrganizationResponse]
    def list: Task[Vector[Organization]]
  }

  type Deps = OrganizationCache with OrganizationDao

  val live: URLayer[Deps, OrganizationService] =
    ZLayer.fromFunction[Deps, OrganizationService.Service] { ctx =>
      val dao = ctx.get[OrganizationDao.Service]
      val cache = ctx.get[OrganizationCache.Service]

      new Service {
        override def get(id: UUID): Task[GetOrganizationResponse] =
          cache.get(id).flatMap {
            case Some(org) => Task.succeed(GetOrganizationResponse.Found(org))
            case None =>
              dao.get(id).flatMap {
                case Some(org) =>
                  Task.succeed(GetOrganizationResponse.Found(org))
                case None => Task.succeed(GetOrganizationResponse.NotFound)
              }
          }

        override def list: Task[Vector[Organization]] = dao.list
      }
    }
}
