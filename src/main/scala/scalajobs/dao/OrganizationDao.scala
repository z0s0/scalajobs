package scalajobs.dao

import java.util.UUID

import scalajobs.configuration.DbConnection.DBTransactor
import scalajobs.dao.impl.OrganizationDaoImpl
import scalajobs.model.Organization
import scalajobs.model.dbParams.OrganizationDbParams
import zio.{Has, Task, ZLayer}
import zio.macros.accessible

@accessible
object OrganizationDao {
  type OrganizationDao = Has[Service]

  trait Service {
    def list: Task[Vector[Organization]]
    def get(id: UUID): Task[Option[Organization]]
    def create(params: OrganizationDbParams): Task[Option[Organization]]
    def deleteAll: Task[Int]
  }

  val live: ZLayer[DBTransactor, Nothing, OrganizationDao] =
    ZLayer.fromService(new OrganizationDaoImpl(_))

}
