package scalajobs.api

import scalajobs.model.ClientError.{Disaster, Invalid}
import scalajobs.model.dbParams.OrganizationDbParams
import scalajobs.service.Layer.Services
import scalajobs.service.{CaptchaValidator, OrganizationService}
import sttp.tapir.ztapir._
import zio.IO
import zio.zmx.metrics.{MetricAspect, MetricsSyntax}

object OrganizationsRoutes {
  private def counter = MetricAspect.count("organization_routes_requests")

  private val listOrganizations = Docs.organizationsDocs.zServerLogic { _ =>
    (OrganizationService.list @@ counter).catchAll(_ => IO.fail(Disaster))
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
    listOrganizations.widen[Services],
    createOrganization.widen[Services]
  )
}
