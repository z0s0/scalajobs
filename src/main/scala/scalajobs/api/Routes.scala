package scalajobs.api

import scalajobs.Main.AppEnv
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._

object Routes {
  private val vacanciesRoutes = VacanciesRoutes.routes.map(_.widen[AppEnv])
  private val organizationRoutes = OrganizationsRoutes.routes.map(_.widen[AppEnv])
  private val tagsRoutes = TagsRoutes.routes.map(_.widen[AppEnv])
  private val metricsRoutes = MetricsRoutes.routes.map(_.widen[AppEnv])

  val routes = ZHttp4sServerInterpreter
    .from(vacanciesRoutes ++ organizationRoutes ++ tagsRoutes ++ metricsRoutes)
    .toRoutes
}
