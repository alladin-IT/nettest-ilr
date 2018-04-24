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

package at.alladin.nettest.server.control.exception.measurement;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author lb@specure.com
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MeasurementNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 8419868973808091129L;

	/**
	 * 
	 */
	public MeasurementNotFoundException() {
	
	}

	/**
	 * 
	 * @param message
	 */
	public MeasurementNotFoundException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public MeasurementNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public MeasurementNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MeasurementNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
