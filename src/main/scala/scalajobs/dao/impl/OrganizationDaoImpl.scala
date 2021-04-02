package scalajobs.dao.impl

import java.util.UUID

import OrganizationDaoImpl.OrganizationRow
import doobie.util.transactor.Transactor
import scalajobs.dao.OrganizationDao
import scalajobs.model.{DbError, Organization}
import zio.{IO, Task}
import doobie._
import doobie.postgres._
import doobie.implicits._
import doobie.postgres.implicits._
import scalajobs.model.DbError.{Conflict, Disaster}
import scalajobs.model.dbParams.OrganizationDbParams
import zio.interop.catz._

object OrganizationDaoImpl {
  final case class OrganizationRow(id: UUID,
                                   name: String,
                                   description: String) {
    def toOrganization: Organization = Organization(Some(id), name, description)
  }
}

final class OrganizationDaoImpl(tr: Transactor[Task])
    extends OrganizationDao.Service {
  override def list: Task[Vector[Organization]] = SQL.all.transact(tr)

  override def get(id: UUID): Task[Option[Organization]] =
    SQL.get(id).transact(tr)

  override def create(
    params: OrganizationDbParams
  ): IO[DbError, Organization] = {
    SQL
      .insert(params)
      .transact(tr)
      .flatMap {
        case Left(error) => IO.fail(Conflict(error))
        case Right(id) =>
          IO.succeed(
            Organization(
              id = Some(id),
              name = params.name,
              description = params.description
            )
          )
      }
      .catchAll(_ => IO.fail(Disaster))
  }

  def deleteAll: Task[Int] = SQL.deleteAll.transact(tr)

  private object SQL {
    def get(id: UUID): ConnectionIO[Option[Organization]] =
      sql"select id, name, description from organizations where id = $id"
        .query[OrganizationRow]
        .map(_.toOrganization)
        .option

    def insert(
      params: OrganizationDbParams
    ): ConnectionIO[Either[String, UUID]] = {
      println(params)

      sql"""
         INSERT INTO organizations(name, description, created_at, updated_at)
         VALUES (${params.name}, ${params.description}, NOW(), NOW())
       """.update
        .withGeneratedKeys[UUID]("id")
        .attemptSomeSqlState {
          case sqlstate.class23.UNIQUE_VIOLATION =>
            s"Organization with name ${params.name} already exists"
        }
        .compile
        .lastOrError
    }
    def all: ConnectionIO[Vector[Organization]] =
      sql"select id, name, description from organizations"
        .query[OrganizationRow]
        .map(_.toOrganization)
        .to[Vector]

    def deleteAll: ConnectionIO[Int] = sql"delete from organizations".update.run
  }

}
