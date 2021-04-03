package scalajobs.api

import scalajobs.Main.AppEnv
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._

object Routes {
  val vacanciesRoutes = VacanciesRoutes.routes.map(_.widen[AppEnv])
  val organizationRoutes = OrganizationsRoutes.routes.map(_.widen[AppEnv])
  val tagsRoutes = TagsRoutes.routes.map(_.widen[AppEnv])

  val routes = ZHttp4sServerInterpreter
    .from(vacanciesRoutes ++ organizationRoutes ++ tagsRoutes)
    .toRoutes
}
