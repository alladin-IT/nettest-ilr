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

package at.alladin.nettest.spring.data.couchdb.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import at.alladin.nettest.spring.data.couchdb.api.ViewParams;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.DefaultParanamer;
import com.thoughtworks.paranamer.Paranamer;

/**
 * Merges the arguments of a method call with the parameters of a {@link ViewParams} object.
 * 
 * @author rwitzel
 */
public class ViewParamsMerger {

    protected Paranamer paranamer;

    public ViewParamsMerger() {
        this(new CachingParanamer(new AdaptiveParanamer(
                /* new Java8Paranamer(), new AnnotationParanamer(), */new DefaultParanamer(),
                new BytecodeReadingParanamer())));
    }

    public ViewParamsMerger(Paranamer paranamer) {
        super();
        this.paranamer = paranamer;
    }

    public void mergeViewParams(ViewParams resultingParams, Method method, Object[] args) {

        mergeViewParams(resultingParams, method.getName(), paranamer.lookupParameterNames(method), args);

    }

    public void mergeViewParams(ViewParams resultingParams, String methodName, String[] parameterNames, Object[] args) {

        ViewParams passedParams = null;

        // iterate through arguments
        for (int index = 0; index < parameterNames.length; index++) {
            String parameterName = parameterNames[index];
            Object arg = args[index];

            Field field = ReflectionUtils.findField(ViewParams.class, parameterName);
            if (field != null) {
                // -> parameter for ViewParams
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, resultingParams, arg);
            } else if (parameterName.equals("viewParams") || parameterName.equals("params")) {
                // -> a ViewParam we have to merge into the resulting ViewParams later
                passedParams = (ViewParams) arg;
            } else {
                throw new RuntimeException("no parameter mapping defined for parameter with name " + parameterName
                        + ", and value " + arg);
            }

        }

        // copy passed parameter to created parameters but do not override existing values
        if (passedParams != null) {
            Field[] fields = ViewParams.class.getDeclaredFields();
            for (Field field : fields) {
                ReflectionUtils.makeAccessible(field);
                Object passedValue = ReflectionUtils.getField(field, passedParams);
                Object resultingValue = ReflectionUtils.getField(field, resultingParams);
                if (resultingValue == null && passedValue != null) {
                    ReflectionUtils.setField(field, resultingParams, passedValue);
                }
            }
        }

        // set view name if not set yet
        if (resultingParams.getView() == null) {
            resultingParams.setView(methodName);
        }

        checkParams(resultingParams);
    }

    protected void checkParams(ViewParams viewParams) {

        List<String> validationErrors = new ArrayList<String>();

        if (Boolean.TRUE.equals(viewParams.getIncludeDocs()) && viewParams.getDocumentType() == null) {
            validationErrors.add("includeDocs is true but the document type is null");
        }
        if ((Boolean.FALSE.equals(viewParams.getIncludeDocs()) || viewParams.getIncludeDocs() == null)
                && viewParams.getDocumentType() != null) {
            validationErrors.add("includeDocs is false but the document type is not null");
        }
        
        // null should be allowed (reduce=true) TODO LightCouch feature request! -> pull request is sent
        if (viewParams.getKeyType() == null) { 
            validationErrors.add("keyType is null");
        }
        if (viewParams.getValueType() == null) {
            validationErrors.add("valueType is null");
        }

        if (validationErrors.size() > 0) {
            handleValidationErrors(validationErrors, viewParams);
        }
    }

    protected void handleValidationErrors(List<String> validationErrors, ViewParams viewParams) {

        StringBuilder sb = new StringBuilder(viewParams.toString() + "\n");
        for (String validationError : validationErrors) {
            sb.append(validationError + "\n");
        }

        throw new IllegalArgumentException(sb.toString());
    }

}
