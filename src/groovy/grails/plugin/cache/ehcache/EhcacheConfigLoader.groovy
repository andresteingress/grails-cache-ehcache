/* Copyright 2012 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.cache.ehcache

import grails.plugin.cache.ConfigLoader
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource

/**
 * @author Burt Beckwith
 */
class EhcacheConfigLoader extends ConfigLoader {

    GrailsApplication grailsApplication

	void reload(List<ConfigObject> configs, ApplicationContext ctx) {

        GrailsEhCacheManagerFactoryBean grailsEhCacheManagerFactoryBean =
            grailsApplication.mainContext.getBean('&ehcacheCacheManager') as GrailsEhCacheManagerFactoryBean

        Resource resource = null

        if (!hasEhCacheXmlFile())  {
            EhcacheConfigBuilder builder = new EhcacheConfigBuilder()
            for (ConfigObject co : configs) {
                def config = co.config
                if (config instanceof Closure) {
                    builder.parse config
                }
            }

            String xml = builder.toXml()
            log.debug "Ehcache generated XML:\n$xml"
            resource = new ByteArrayResource(xml.bytes)
        }

		GrailsEhcacheCacheManager cacheManager = ctx.grailsCacheManager

		// make copy of names to avoid CME
		for (String name in ([] + cacheManager.cacheNames)) {
			cacheManager.destroyCache name
		}

        grailsEhCacheManagerFactoryBean.rebuild(resource)
        cacheManager.cacheManager = grailsEhCacheManagerFactoryBean.object
        cacheManager.afterPropertiesSet()
	}

    boolean hasEhCacheXmlFile()  {
        return grailsApplication.config.grails.cache.ehcache.ehcacheXmlLocation instanceof CharSequence
    }
}
