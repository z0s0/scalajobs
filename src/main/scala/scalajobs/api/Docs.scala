package scalajobs.api

import java.util.UUID

import scalajobs.model.form.{OrganizationForm, VacancyForm}
import scalajobs.model.{ClientError, Organization, Tag, Vacancy}
import sttp.model.StatusCode
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir._
import sttp.tapir.generic.auto._
import sttp.tapir.openapi.circe.yaml._

object Docs {
  val organizationDocs =
    endpoint.get
      .in("organizations" / path[UUID])
      .out(jsonBody[Organization])
      .errorOut(stringBody)
      .errorOut(statusCode(StatusCode.NotFound))

  val organizationsDocs =
    endpoint.get
      .in("organizations")
      .out(jsonBody[List[Organization]])

  val createOrganization =
    endpoint.post
      .in("organizations")
      .in(jsonBody[OrganizationForm])
      .out(jsonBody[Organization])
      .errorOut(jsonBody[List[ClientError]])
      .errorOut(statusCode(StatusCode.BadRequest))

  val vacanciesDocs =
    endpoint.get
      .in("vacancies")
      .out(jsonBody[Vector[Vacancy]])

  val vacancyDocs =
    endpoint.get
      .in("vacancies" / path[UUID])
      .errorOut(stringBody)
      .errorOut(statusCode(StatusCode.NotFound))
      .out(jsonBody[Vacancy])

  val createVacancyDocs =
    endpoint.post
      .in("vacancies")
      .in(jsonBody[VacancyForm])
      .out(jsonBody[Vacancy])
      .out(statusCode(StatusCode.Created))
      .errorOut(jsonBody[List[ClientError]])
      .errorOut(statusCode(StatusCode.BadRequest))

  val listTags =
    endpoint.get
      .in("tags")
      .out(jsonBody[List[Tag]])

  val docs =
    List(
      organizationsDocs,
      organizationDocs,
      createOrganization,
      vacancyDocs,
      vacanciesDocs,
      createVacancyDocs,
      listTags
    )

  val yaml =
    OpenAPIDocsInterpreter.toOpenAPI(docs, "Scalajobs", "1.0").toYaml
}
