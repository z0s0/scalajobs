package scalajobs.dao

import java.util.UUID

import scalajobs.configuration.DbConnection.DBTransactor
import scalajobs.dao.impl.OrganizationDaoImpl
import scalajobs.model.Organization
import zio.{Has, Task, ZLayer}

object OrganizationDao {
  type OrganizationDao = Has[Service]

  trait Service {
    def list: Task[Vector[Organization]]
    def get(id: UUID): Task[Option[Organization]]
  }

  val live: ZLayer[DBTransactor, Throwable, OrganizationDao] =
    ZLayer.fromService(new OrganizationDaoImpl(_))

}
