package grails.plugin.cache.ehcache

import groovy.util.logging.Slf4j
import net.sf.ehcache.CacheManager
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean
import org.springframework.core.io.Resource

@Slf4j
class GrailsEhCacheManagerFactoryBean extends EhCacheManagerFactoryBean {

    synchronized void rebuild(Resource location)  {
        log.info "Rebuilding EhCache cache manager"
        CacheManager cacheManager = object
        cacheManager.removalAll()
        cacheManager.shutdown()

        // location might be null in the case of a global ehcache.xml
        if (location != null)  {
            log.info "Setting EhCache manager factory config location to ${location}"
            setConfigLocation(location)
        }

        afterPropertiesSet()
        log.info "EhCache cache manager successfully rebuilt"
    }

}
