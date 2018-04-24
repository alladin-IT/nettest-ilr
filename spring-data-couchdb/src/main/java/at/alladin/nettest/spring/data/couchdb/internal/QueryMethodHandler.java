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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

import at.alladin.nettest.spring.data.couchdb.api.CouchDbCrudRepository;
import at.alladin.nettest.spring.data.couchdb.api.ViewParams;

/**
 * This {@link InvocationHandler} delegates to a {@link #customRepository repository with custom method implementations}
 * or the {@link #crudRepository underlying CRUD repository} if these repositories have implemented a method with the
 * same name as the called method. If there is not such a method, the handler collects all arguments in a
 * {@link ViewParams} object and calls {@link CouchDbCrudRepository#find(ViewParams)} on the underlying CRUD repository.
 * 
 * @author rwitzel
 */
@SuppressWarnings("rawtypes")
public class QueryMethodHandler implements InvocationHandler {

    protected CouchDbCrudRepository crudRepository;

    protected Object customRepository;

    protected ViewParamsMerger viewParamsMerger;

    public QueryMethodHandler(CouchDbCrudRepository crudRepository, Object customRepository,
            ViewParamsMerger viewParamsMerger) {
        super();
        this.crudRepository = crudRepository;
        this.customRepository = customRepository;
        this.viewParamsMerger = viewParamsMerger;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // delegate to the custom repository?
        if (customRepository != null) {
            Method underlyingMethod = ReflectionUtils.findMethod(customRepository.getClass(), method.getName(),
                    method.getParameterTypes());

            if (underlyingMethod != null) {
                try {
                    return underlyingMethod.invoke(customRepository, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause(); // we want to throw the original exception
                }
            }
        }

        // delegate to the underlying CRUD repository?
        Method underlyingMethod = ReflectionUtils.findMethod(crudRepository.getClass(), method.getName(),
                method.getParameterTypes());
        if (underlyingMethod != null) {
            try {
                return underlyingMethod.invoke(crudRepository, args);
            } catch (InvocationTargetException e) {
                throw e.getCause(); // we want to throw the original exception
            }
        }

        // find by view
        ViewParams viewParams = new ViewParams();
        viewParamsMerger.mergeViewParams(viewParams, method, args);
        return crudRepository.find(viewParams);
    }

}
