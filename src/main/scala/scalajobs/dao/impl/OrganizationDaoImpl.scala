package scalajobs.dao.impl

import java.util.UUID

import OrganizationDaoImpl.OrganizationRow
import doobie.util.transactor.Transactor
import scalajobs.dao.OrganizationDao
import scalajobs.model.Organization
import zio.Task
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import zio.interop.catz._

object OrganizationDaoImpl {
  final case class OrganizationRow(id: UUID, name: String) {
    def toOrganization: Organization = Organization(Some(id), name)
  }

}
final class OrganizationDaoImpl(tr: Transactor[Task])
    extends OrganizationDao.Service {
  override def list: Task[Vector[Organization]] =
    sql"""select id, name from organizations"""
      .query[OrganizationRow]
      .map(_.toOrganization)
      .to[Vector]
      .transact(tr)

  override def get(id: UUID): Task[Option[Organization]] =
    sql"""select id, name from organizations where id = $id"""
      .query[OrganizationRow]
      .map(_.toOrganization)
      .option
      .transact(tr)

}
