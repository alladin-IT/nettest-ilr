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

package at.alladin.nettest.shared.server.helper.json;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import at.alladin.rmbt.shared.db.QoSTestResult.TestType;
import at.alladin.rmbt.shared.qos.AbstractResult;

public class QosAbstractResultJsonDeserializer implements JsonDeserializer<AbstractResult> {

	private final Logger logger = LoggerFactory.getLogger(QosAbstractResultJsonDeserializer.class);
	
	/*
	 * (non-Javadoc)
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public AbstractResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject jsonObject = json.getAsJsonObject();
		
		try {
			final TestType type = TestType.valueOf(jsonObject.get("test_type").getAsString().toUpperCase());
		
			return context.deserialize(json, type.getClazz());
		} catch (Exception e) {
			logger.error("Could not deserialize json object into a result class", e);
		}
		
		return null;
	}
}
