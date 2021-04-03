package scalajobs.support

import scalajobs.model.dbParams.OrganizationDbParams
import zio.test.Gen

object Generators {
  val genOrgDbParams =
    for {
      name <- Gen.anyASCIIString
      description <- Gen.anyASCIIString
    } yield OrganizationDbParams(name, description)

}
