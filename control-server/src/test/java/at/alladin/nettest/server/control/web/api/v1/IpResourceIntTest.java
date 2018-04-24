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
//
//import javax.annotation.PostConstruct;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.MockitoAnnotations;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.boot.test.IntegrationTest;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.specure.nettest.server.control.ControlServerApplication;
//import at.alladin.nettest.server.control.web.api.ApiTestUtil;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = ControlServerApplication.class)
//@WebAppConfiguration
//@IntegrationTest
//public class IpResourceIntTest {
//	
//	/**
//	 * 
//	 */
//	private MockMvc ipResourceMock;
//	
//	/**
//	 * 
//	 */
//	@PostConstruct
//    public void postConstruct() {
//        MockitoAnnotations.initMocks(this);
//        
//        //final Object dbObj = null; // TODO
//        
//        final IpResource ipResource = new IpResource();
//        //ReflectionTestUtils.setField(ipResource, "dbObj", dbObj);
//        
//        ipResourceMock = MockMvcBuilders.standaloneSetup(ipResource)
//        		//.setCustomArgumentResolvers(pageableArgumentResolver)
//                //.setMessageConverters(jacksonMessageConverter)
//        		.build();
//    }
//
//	/**
//	 * 
//	 */
//    @Before
//    public void setUp() {
//    	
//    }
//	
//    /**
//     * 
//     */
//    @After
//    public void tearDown() {
//
//	}
//    
//	/**
//	 * @throws Exception 
//	 * 
//	 */
//	@Test
//	public void testValidIpRequest() throws Exception {
//		ipResourceMock.perform(get("/api/v1/ip")
//                .contentType(ApiTestUtil.APPLICATION_JSON_UTF8))
//                //.content(""))
//                .andExpect(status().isOk());
//	}
//}
