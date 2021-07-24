package scalajobs.api

import scalajobs.model.ClientError.{Disaster, Invalid}
import scalajobs.model.dbParams.OrganizationDbParams
import scalajobs.service.Layer.Services
import scalajobs.service.{CaptchaValidator, OrganizationService}
import sttp.tapir.ztapir._
import zio.clock.Clock
import zio.{Chunk, IO, UIO}
import zio.zmx.metrics.{MetricAspect, MetricsSyntax}

object OrganizationsRoutes {
  private val responseTimeBuckets = Chunk(50d, 100d, 200d, 500d, 1000d)
  private val counter = MetricAspect.count("organization_routes_requests")
  private val histogram = MetricAspect.observeHistogram("list_organizations_response_time", responseTimeBuckets)

  private val listOrganizations = Docs.organizationsDocs.zServerLogic { _ =>
    (for {
      (duration, result) <- (OrganizationService.list @@ counter).timed
      _ <- UIO(duration.toMillis.toDouble) @@ histogram
    } yield result).catchAll(_ => IO.fail(Disaster))
  }

  private val createOrganization = Docs.createOrganization.zServerLogic { form =>
    form.validate
      .fold(
        errors => IO.fail(errors.map(Invalid)),
        _ =>
          CaptchaValidator.isValid(form.captcha.get)
            .catchAll(_ => IO.fail(List(Disaster)))
            .flatMap(captchaValid =>
              if (captchaValid)
                OrganizationService
                .create(OrganizationDbParams(form.name.get, form.description.get))
                .mapError(List(_))
              else
                IO.fail(List(Invalid("invalid captcha")))
            )
      )
  }

  val routes = List(
    listOrganizations.widen[Services with Clock],
    createOrganization.widen[Services with Clock]
  )
}
