package scalajobs.configuration

final case class ApiConfig(endpoint: String, port: Int)
final case class DbConfig(url: String, user: String, password: String)

final case class Config(apiConfig: ApiConfig, dbConfig: DbConfig)
