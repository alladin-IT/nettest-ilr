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

package at.alladin.nettest.shared.server.config.support;

import java.lang.annotation.Annotation;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class AnnotationBasedGsonExclusionStrategy<T extends Annotation> implements ExclusionStrategy {

	/**
	 * 
	 */
	private Class<T> annotationClass;
	
	/**
	 * 
	 */
	public AnnotationBasedGsonExclusionStrategy(Class<T> annotationClass) {
		this.annotationClass = annotationClass;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.google.gson.ExclusionStrategy#shouldSkipField(com.google.gson.FieldAttributes)
	 */
	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(annotationClass) != null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gson.ExclusionStrategy#shouldSkipClass(java.lang.Class)
	 */
	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return clazz.getAnnotation(annotationClass) != null;
	}
}
