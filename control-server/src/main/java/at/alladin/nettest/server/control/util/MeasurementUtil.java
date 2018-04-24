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

package at.alladin.nettest.server.control.util;

import java.util.List;

/**
 * 
 * @author lb@specure.com
 *
 */
public class MeasurementUtil {

    /**
     * Calculates the variance of the givens pings.
     * 
     * @param pings
     * @return The variance as double value
     */
    public static Double calculatePingVariance(List<Long> pings) {  	
    	double pingMean = 0;
    	double pingCount = pings.size();
    	
    	if (pingCount < 1) {
    		return null; // cannot be calculated if there are no pings
    	}
    	
    	for (long p : pings) {
    		pingMean += p;
    	}
    	
    	pingMean /= pingCount;
        
    	///
    	
    	double pingVariance = 0;
        
        for (long p : pings) {
        	pingVariance += Math.pow((p - pingMean), 2);
        }
        
        pingVariance /= pingCount;
        
    	return pingVariance;
	}

}
