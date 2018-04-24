/*******************************************************************************
 * Copyright 2016-2017 alladin-IT GmbH
 * Copyright 2016 SPECURE GmbH
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

package at.alladin.nettest.server.control.config;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public final class Constants {

	/**
	 * TODO
	 * move secret key elsewhere
	 */
	public static final String SECRET_KEY = "drefothJo7ipheapgokejB3oyky6uf2JoljyupEiTevcijimtabv8goboc";
	
	public static final String SPRING_PROFILES_ACTIVE_KEY = "spring.profiles.active";
	public static final String SPRING_PROFILES_ACTIVE_STRING = "SPRING_PROFILES_ACTIVE";
	
	/**
	 * 
	 */
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    
    /**
     * 
     */
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    ////
    
    /**
     * 
     */
    public static final String DATABASES_YAML = "databases.yml";
    
    /**
     * 
     */
    public static final String DATABASES_YAML_CLASSPATH = "config/" + DATABASES_YAML;
	
    /**
     * 
     */
    public static final String EXTERNAL_CONFIG_LOCATION = "/etc/nettest/control-server/";
    
    /**
	 * 
	 */
    public static final String DB_CLIENT_NAME_SUFFIX = "DbClient";
    
    /**
     * 
     */
    private Constants() {
    	
    }
}
