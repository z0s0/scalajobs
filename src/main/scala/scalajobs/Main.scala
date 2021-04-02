package scalajobs

import org.http4s.server.Router
import scalajobs.configuration.ApiConfig
import zio.{Has, RIO, ZIO}
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.syntax.kleisli._
import org.http4s.server.blaze.BlazeServerBuilder
import org.slf4j.LoggerFactory
import scalajobs.api.{Docs, OrganizationsRoutes, TagsRoutes, VacanciesRoutes}
import scalajobs.configuration.Configuration.AllConfigs
import scalajobs.service.Layer.Services
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import zio.clock.Clock
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import sttp.tapir.ztapir._
import zio.interop.catz._

object Main {
  type AppEnv = Clock with Services with AllConfigs
  val vacanciesRoutes = VacanciesRoutes.routes.map(_.widen[AppEnv])
  val organizationRoutes = OrganizationsRoutes.routes.map(_.widen[AppEnv])
  val tagsRoutes = TagsRoutes.routes.map(_.widen[AppEnv])

  val routes = ZHttp4sServerInterpreter
    .from(vacanciesRoutes ++ organizationRoutes ++ tagsRoutes)
    .toRoutes

  private val log = LoggerFactory.getLogger("RuntimeReporter")

  def main(args: Array[String]): Unit = {
    val program = for {
      apiConf <- ZIO.access[Has[ApiConfig]](_.get)
      _ <- runHttp(routes, apiConf)
    } yield ()

    val runtime = zio.Runtime.default.withReportFailure { cause =>
      if (cause.died) log.error(cause.prettyPrint)
    }

    val layered = program.provideCustomLayer(DI.live)

    runtime.unsafeRun(layered.orDie)
  }

  private def runHttp[R <: Clock](routes: HttpRoutes[RIO[R, *]],
                                  apiConf: ApiConfig) = {
    type Task[T] = RIO[R, T]

    ZIO.runtime[R].flatMap { implicit rt =>
      val swagger = new SwaggerHttp4s(Docs.yaml).routes[Task]

      BlazeServerBuilder[Task]
        .bindHttp(apiConf.port, "localhost")
        .withHttpApp(Router("/" -> (routes <+> swagger)).orNotFound)
        .serve
        .compile[Task, Task, cats.effect.ExitCode]
        .drain
    }
  }
}
