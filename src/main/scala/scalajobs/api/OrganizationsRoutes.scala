package scalajobs.api

import scalajobs.model.ClientError.{Disaster, Invalid}
import scalajobs.model.dbParams.OrganizationDbParams
import scalajobs.service.Layer.Services
import scalajobs.service.{CaptchaValidator, OrganizationService}
import sttp.tapir.ztapir._
import zio.IO

object OrganizationsRoutes {
  val listOrganizations = Docs.organizationsDocs.zServerLogic { _ =>
    OrganizationService.list.catchAll(_ => IO.fail(Disaster))
  }

  val createOrganization = Docs.createOrganization.zServerLogic { form =>
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
