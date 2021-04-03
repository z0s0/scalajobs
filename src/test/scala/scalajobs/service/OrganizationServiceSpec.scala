package scalajobs.service

import scalajobs.dao.OrganizationDao
import zio.test.DefaultRunnableSpec
import zio.test.mock.mockable

@mockable[OrganizationDao.Service]
object OrganizationServiceMock
