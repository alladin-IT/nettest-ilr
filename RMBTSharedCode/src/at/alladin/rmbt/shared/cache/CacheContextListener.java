/*******************************************************************************
 * Copyright 2013-2017 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.rmbt.shared.cache;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 * @author alladin-IT GmbH (?@alladin.at)
 *
 */
public class CacheContextListener implements ServletContextListener
{
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final String addresses = sce.getServletContext().getInitParameter("RMBT_MEMCACHED_ADDRESSES");
        
        if (addresses == null || addresses.isEmpty()) {
            System.out.println("RMBT_MEMCACHED_ADDRESSES not set, cache deactivated");
        } else {
            System.out.println("init memcached with: " + addresses);
            CacheHelper.getInstance().initMemcached(addresses);
            
            try {
            	int cachePeriod = Integer.parseInt(sce.getServletContext().getInitParameter("RMBT_CACHE_PERIOD"));
            	CacheHelper.getInstance().setCachePeriod(cachePeriod);
            	
            	System.out.println("RMBT_CACHE_PERIOD set to " + cachePeriod + ", enabling prerendering");
            } catch(NumberFormatException ex) {
            	System.out.println("RMBT_CACHE_PERIOD not set, disabling prerendering");
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    	
    }
}
