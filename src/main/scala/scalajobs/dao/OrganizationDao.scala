package scalajobs.dao

import doobie.util.transactor.Transactor
import scalajobs.configuration.DbConnection.DBTransactor
import scalajobs.model.Organization
import zio.{Has, Task, ZLayer}

object OrganizationDao {
  type OrganizationDao = Has[Service]

  trait Service {
    def list: Task[Vector[String]]
  }

  val live: ZLayer[DBTransactor, Throwable, OrganizationDao] =
    ZLayer.fromService { tr =>
      new Service {
        import doobie._
        import doobie.implicits._
        import doobie.postgres.implicits._
        import zio.interop.catz._

        override def list: Task[Vector[String]] = {
          val action = sql"""select name from organizations"""
            .query[String]
            .to[Vector]
            .transact(tr)

          action
        }
      }
    }

}
