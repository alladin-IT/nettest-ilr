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

package at.alladin.nettest.server.control.web.api.v1;

import java.net.InetAddress;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.net.InetAddresses;
import at.alladin.nettest.server.control.service.ClientService;
import at.alladin.nettest.shared.model.request.BasicRequestImpl;
import at.alladin.nettest.shared.model.response.IpResponse;
import at.alladin.nettest.shared.model.response.IpResponse.IpVersion;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 
 * @author lb@specure.com
 * @author Specure GmbH (bp@specure.com)
 *
 */
@RestController
@RequestMapping("/api/v1/ip")
public class IpResource {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(IpResource.class);
	
	/**
	 * 
	 */
	@Inject
	private ClientService clientService;
	
	/**
	 * 
	 * @return
	 */
	@ApiOperation(value = "Returns client IP", notes = "Returns the client's IP address and its version.", response = IpResponse.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "Success", response = IpResponse.class)
	})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "lang", dataType = "string", paramType = "query", value = "Accept-Language override") // Locale
	})
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getIp(@RequestBody(required = false) BasicRequestImpl basicRequest, HttpServletRequest request, @ApiIgnore Locale locale) {
		final String clientIpAddressString = clientService.getClientIpAddressString(request);
		final InetAddress clientIpAddress = InetAddresses.forString(clientIpAddressString);
		
		final IpResponse ipResponse = new IpResponse();
		ipResponse.setIp(clientIpAddressString);
		ipResponse.setVersion(IpVersion.fromInetAddress(clientIpAddress));
		
		// TODO: save status information? (do something with basicRequest?)
		
		return new ResponseEntity<>(ipResponse, HttpStatus.OK);
	}
}
