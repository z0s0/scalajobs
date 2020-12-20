package scalajobs.cache

import java.util.UUID

import scalajobs.model.Organization
import zio.{Has, Task, ULayer, ZLayer}

object OrganizationCache {
  type OrganizationCache = Has[Service]

  trait Service extends EntityCache[Task, UUID, Organization] {
    override def get(id: UUID): Task[Option[Organization]]
    override def set(entity: Organization): Task[Unit]
  }

  val live: ULayer[OrganizationCache] = ZLayer.succeed(new Service {
    override def get(id: UUID): Task[Option[Organization]] = Task.none

    override def set(entity: Organization): Task[Unit] = Task.unit
  })
}
