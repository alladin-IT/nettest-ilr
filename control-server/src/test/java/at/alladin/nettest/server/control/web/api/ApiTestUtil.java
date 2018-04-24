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

package at.alladin.nettest.server.control.web.api;

import java.nio.charset.Charset;

import org.springframework.http.MediaType;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class ApiTestUtil {

	/**
	 * 
	 */
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
		MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(), 
        Charset.forName("utf8")
	);
}
