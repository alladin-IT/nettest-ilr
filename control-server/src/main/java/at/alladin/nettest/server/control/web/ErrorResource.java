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

package at.alladin.nettest.server.control.web;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import at.alladin.nettest.server.control.domain.JsonError;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@RestController
public class ErrorResource implements ErrorController {

	private final Logger logger = LoggerFactory.getLogger(ErrorResource.class);
	
	/**
	 * 
	 */
	@Inject
	private ErrorAttributes errorAttributes;
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JsonError> error(HttpServletRequest request, HttpServletResponse response) {
		final HttpStatus status = HttpStatus.valueOf(response.getStatus());
		
		return new ResponseEntity<>(buildError(request, true), status);
	}
	
	/**
	 * 
	 * @param request
	 * @param includeStackTrace
	 * @return
	 */
	private JsonError buildError(HttpServletRequest request, boolean includeStackTrace) { // TODO: create better errors
		final RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		final Map<String, Object> errorMap = errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
		
		//logger.info("error {} -> {}", errorMap.get("message").toString(), ((Exception)errorMap.get("trace")).getCause().getMessage());
		
		return new JsonError(1, errorMap.get("message").toString(), errorMap.toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.boot.autoconfigure.web.ErrorController#getErrorPath()
	 */
	@Override
	public String getErrorPath() {
		return "/error";
	}
}
