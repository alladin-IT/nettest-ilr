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

import at.alladin.nettest.server.control.exception.TestTokenGenerationException;

import at.alladin.rmbt.shared.Helperfunctions;

/**
 * 
 * @author lb@specure.com
 *
 */
public class TestTokenUtil {

	public static class TestToken {
		String token;
		Long timestamp;
		Integer wait;
		
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public Long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}
		public Integer getWait() {
			return wait;
		}
		public void setWait(Integer wait) {
			this.wait = wait;
		}
	}
	
	public static TestToken generateTestToken(final String testUuid, final String testServerSecret) {
    	final Long testSlot = (System.currentTimeMillis()/1000)-2;
    	
    	final String data = testUuid  + "_" + testSlot;
    	final String hmac = Helperfunctions.calculateHMAC(testServerSecret, data);
    	
    	if (hmac.length() == 0) {
    		throw new TestTokenGenerationException();
    	}
    	
    	final TestToken token = new TestToken();
    	token.setToken(data + "_" + hmac);
    	token.setTimestamp(testSlot);
    	token.setWait(0);
    	
    	return token;
	}
}
