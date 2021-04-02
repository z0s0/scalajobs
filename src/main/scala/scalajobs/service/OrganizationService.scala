package scalajobs.service

import scalajobs.dao.OrganizationDao.OrganizationDao
import scalajobs.model.{ClientError, DbError, Organization}
import scalajobs.model.dbParams.OrganizationDbParams
import zio.{Has, IO, Task, ZIO, ZLayer}

object OrganizationService {
  type OrganizationService = Has[Service]

  trait Service {
    def list: Task[List[Organization]]
    def create(params: OrganizationDbParams): IO[ClientError, Organization]
  }

  val live: ZLayer[OrganizationDao, Nothing, OrganizationService] =
    ZLayer.fromService { dao =>
      new Service {
        override def list: Task[List[Organization]] =
          dao.list.map(_.toList)

        override def create(
          params: OrganizationDbParams
        ): IO[ClientError, Organization] = {
          dao.create(params).catchAll {
            case DbError.Conflict(reason) =>
              IO.fail(ClientError.Invalid(reason))
            case DbError.Disaster => IO.fail(ClientError.Disaster)
          }
        }
      }
    }

  def list: ZIO[OrganizationService, Throwable, List[Organization]] =
    ZIO.accessM(_.get.list)
  def create(
    params: OrganizationDbParams
  ): ZIO[OrganizationService, ClientError, Organization] =
    ZIO.accessM(_.get.create(params))
}
