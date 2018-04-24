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

package at.alladin.rmbt.shared;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;

import com.google.common.net.InetAddresses;
import com.maxmind.geoip.Country;
import com.maxmind.geoip.LookupService;

/**
 * 
 * @author alladin-IT GmbH (?@alladin.at)
 *
 */
public abstract class GeoIPHelper {
	
    private static volatile LookupService lookupServiceV4;
    private static volatile boolean lookupServiceV4Failure;
    private static volatile LookupService lookupServiceV6;
    private static volatile boolean lookupServiceV6Failure;

    private final static Object LOOKUP_SERVICE_LOCK = new Object(); 

    /**
     * 
     * @return
     */
    private static LookupService getLookupServiceV4() {
        synchronized (LOOKUP_SERVICE_LOCK) {
            if (lookupServiceV4Failure) {
                return null;
            }
            
            if (lookupServiceV4 != null) {
                return lookupServiceV4;
            }
            
            try {
                return lookupServiceV4 = getLookupService("/usr/share/GeoIP/GeoIP.dat"/*, "http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz"*/);
            } catch (Exception e) {
                lookupServiceV4Failure = true;
                return null;
            }
        }
    }
    
    /**
     * 
     * @return
     */
    private static LookupService getLookupServiceV6() {
        synchronized (LOOKUP_SERVICE_LOCK) {
            if (lookupServiceV6Failure) {
                return null;
            }
            
            if (lookupServiceV6 != null) {
                return lookupServiceV6;
            }
            
            try {
                return lookupServiceV6 = getLookupService("/usr/share/GeoIP/GeoIPv6.dat"/*, "http://geolite.maxmind.com/download/geoip/database/GeoIPv6.dat.gz"*/);
            } catch (Exception e) {
                lookupServiceV6Failure = true;
                return null;
            }
        }
    }
    
    /**
     * 
     * @param path
     * @return
     * @throws IOException
     */
    private static LookupService getLookupService(String path/*, String downloadUrl*/) throws IOException {
        final File file = new File(path);
        
        if (!(file.exists() && file.isFile() && file.canRead())) {
        	throw new FileNotFoundException();
        }
        
        return new LookupService(file);
    }
    
    /**
     * 
     * @param adr
     * @return
     */
    public static String lookupCountry(final InetAddress adr) {
        try {
            final Country country;
            if (adr instanceof Inet6Address) {
                final LookupService ls = getLookupServiceV6();
                if (ls == null) {
                    return null;
                }
                
                country = ls.getCountryV6(adr);
            } else {
                final LookupService ls = getLookupServiceV4();
                if (ls == null) {
                    return null;
                }
                
                country = ls.getCountry(adr);
            }
            
            return country.getCode();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
    
    //???
    public static void main(String[] args) {
    	assert "AT".equals(lookupCountry(InetAddresses.forString("2a01:190:1700:39::75"))) : "IP must be resolved to AT";
    	assert "DE".equals(lookupCountry(InetAddresses.forString("78.47.24.204"))) : "IP must be resolved to DE";
    	System.out.println("2/2 - Lookup test passed");
    }
}
