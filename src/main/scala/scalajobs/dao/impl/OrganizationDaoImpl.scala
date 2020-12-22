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
import cats.implicits._
import scalajobs.model.dbParams.OrganizationDbParams
import zio.interop.catz._

object OrganizationDaoImpl {
  final case class OrganizationRow(id: UUID, name: String) {
    def toOrganization: Organization = Organization(Some(id), name)
  }
}

final class OrganizationDaoImpl(tr: Transactor[Task])
    extends OrganizationDao.Service {
  override def list: Task[Vector[Organization]] = SQL.all.transact(tr)

  override def get(id: UUID): Task[Option[Organization]] =
    SQL.get(id).transact(tr)

  override def create(
    params: OrganizationDbParams
  ): Task[Option[Organization]] = {
    (for {
      id <- SQL.insert(params)
      organization <- id match {
        case Some(uuid) => SQL.get(uuid)
        case None       => Option.empty[Organization].pure[ConnectionIO]
      }
    } yield organization).transact(tr)
  }

  def deleteAll: Task[Int] = SQL.deleteAll.transact(tr)

  private object SQL {
    def get(id: UUID): ConnectionIO[Option[Organization]] =
      sql"select id, name from organizations where id = $id"
        .query[OrganizationRow]
        .map(_.toOrganization)
        .option

    def insert(params: OrganizationDbParams): ConnectionIO[Option[UUID]] =
      sql"""
         INSERT INTO organizations(name, created_at, updated_at) VALUES (${params.name}, NOW(), NOW())
       """.update
        .withGeneratedKeys[UUID]("id")
        .compile
        .last

    def all: ConnectionIO[Vector[Organization]] =
      sql"select id, name from organizations"
        .query[OrganizationRow]
        .map(_.toOrganization)
        .to[Vector]

    def deleteAll: ConnectionIO[Int] = sql"delete from organizations".update.run
  }

}
