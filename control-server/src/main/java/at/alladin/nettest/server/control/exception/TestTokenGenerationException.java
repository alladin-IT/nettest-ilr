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

package at.alladin.nettest.server.control.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TestTokenGenerationException extends RuntimeException {

	private static final long serialVersionUID = 8419868973808091129L;

	/**
	 * 
	 */
	public TestTokenGenerationException() {
	
	}

	/**
	 * 
	 * @param message
	 */
	public TestTokenGenerationException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public TestTokenGenerationException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public TestTokenGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public TestTokenGenerationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
