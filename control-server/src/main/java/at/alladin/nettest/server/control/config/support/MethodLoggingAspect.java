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

package at.alladin.nettest.server.control.config.support;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;

import at.alladin.nettest.shared.server.helper.GsonHelper;

import com.google.gson.Gson;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@Aspect
public class MethodLoggingAspect {

	private final Logger logger = LoggerFactory.getLogger(MethodLoggingAspect.class);
	
	/**
	 * 
	 */
	private final Gson gson = GsonHelper.createMethodLoggerGsonBuilder().create();
	
	/**
	 * 
	 */
	public MethodLoggingAspect() {		

	}
	
	/**
	 * 
	 */
	@Pointcut("within(at.alladin.nettest.server.control.web..*) || within(at.alladin.nettest.server.control.service..*)")
    public void loggingPointcut() {
		
    }
	
	/**
	 * 
	 * @param joinPoint
	 * @param t
	 */
	@AfterThrowing(pointcut = "loggingPointcut()", throwing = "t")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable t) {
		logger.error(
			"Exception in {}.{}() with cause = {} and exception {}", 
			joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            t.getCause(),
            t
		);
    }

	/**
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
    @Around("loggingPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    	StopWatch stopWatch = null;
    	
    	if (logger.isDebugEnabled()) {
            logger.debug(
            	"Enter: {}.{}() with argument[s] = {}", 
            	joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), 
                Arrays.toString(joinPoint.getArgs())
            );
            
            stopWatch = new StopWatch();
            stopWatch.start();
        }
    	
    	try {
            Object result = joinPoint.proceed();
            
            if (logger.isDebugEnabled()) {
            	stopWatch.stop();
            
            	String resultString = null;
            	if (result != null) {
	            	if (result instanceof ResponseEntity<?>) {
	            		resultString = gson.toJson(((ResponseEntity<?>)result).getBody());
	            	} else {
	            		resultString = result.toString();
	            	}
            	}
            	
                logger.debug(
                	"Exit: {}.{}(), took: {} ms, with result = {}",
                	joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), 
                    stopWatch.getTotalTimeMillis(),
                    resultString
                );
                
                stopWatch = null;
            }
            
            return result;
        } finally {
        	// do nothing
        }
    }
}
