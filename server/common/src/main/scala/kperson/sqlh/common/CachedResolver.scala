package kperson.sqlh.common

case class CacheCredentials(dataSource: DataSource, expiration: Long)

class CachedResolver(childResolver: DataSourceReferenceResolver, expirationMilliseconds: Long) extends DataSourceReferenceResolver {

  private val cache = scala.collection.mutable.Map[String, CacheCredentials]()


  def convertCustomToDataSource(custom: Custom): Option[DataSource] = {
    val now = System.currentTimeMillis()
    cache.get(custom.value) match {
      case Some(resolved) if now < resolved.expiration => Some(resolved.dataSource)
      case Some(_) =>
        cache.remove(custom.value)
        convertCustomToDataSource(custom)
      case _ =>
        childResolver.convertCustomToDataSource(custom) match {
          case Some(resolved) =>
            cache(custom.value) = CacheCredentials(resolved, now + expirationMilliseconds)
            Some(resolved)
          case _ => None
        }
    }
  }

}
