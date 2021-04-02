package scalajobs.api

import scalajobs.model.ClientError.{Disaster, Invalid}
import scalajobs.model.dbParams.OrganizationDbParams
import scalajobs.service.OrganizationService
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
          OrganizationService
            .create(OrganizationDbParams(form.name.get, form.description.get))
            .mapError(List(_))
      )
  }

  val routes = List(listOrganizations, createOrganization)
}
