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

package at.alladin.nettest.spring.data.couchdb.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;
import org.lightcouch.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import at.alladin.nettest.spring.data.couchdb.api.CouchDbCrudRepository;
import at.alladin.nettest.spring.data.couchdb.api.EntityInformation;
import at.alladin.nettest.spring.data.couchdb.api.ViewParams;
import at.alladin.nettest.spring.data.couchdb.api.ViewResult;
import at.alladin.nettest.spring.data.couchdb.api.ViewResultRow;
import at.alladin.nettest.spring.data.couchdb.api.exceptions.BulkOperationError;
import at.alladin.nettest.spring.data.couchdb.api.exceptions.BulkOperationException;
import at.alladin.nettest.spring.data.couchdb.internal.AdapterUtils;
import at.alladin.nettest.spring.data.couchdb.lightcouch.LightCouchViewConfigurer;
import at.alladin.nettest.spring.data.couchdb.support.GenericEntityInformation;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 * @param <T>
 */
/**
 * This implementation of {@link CouchDbCrudRepository} uses LightCouch's {@link CouchDbClient}.
 * <p>
 * This implementation requires a special view in a design document. EXAMPLE: <code>
  "views" : {
    "by_id" : {
      "map" : "function(doc) { if(doc.type == '...') {emit(doc._id, { _id : doc._id, _rev: doc._rev } )} }",
      "reduce" : "_count"
    }
  }
 </code>
 * <p>
 * Take care when using {@link #deleteAll()} because it loads the IDs and revisions of all documents of the
 * abovementioned view at once, then deletes the documents.
 * 
 * @author rwitzel
 */
public class LightCouchCouchDbRepository<T> implements CouchDbRepository<T> {

	private Logger logger = LoggerFactory.getLogger(LightCouchCouchDbRepository.class);
	
	protected CouchDbClient couchDbClient;
	
    protected LightCouchViewConfigurer viewBuilder = new LightCouchViewConfigurer();

    protected Class<T> type;

    protected EntityInformation<T, String> ei;

    protected boolean allOrNothing;
	
	/**
	 * 
	 * @param type
	 * @param couchDbClient
	 */
	public LightCouchCouchDbRepository(Class<T> type, CouchDbClient couchDbClient) {
		this(type, false, couchDbClient, new GenericEntityInformation<T, String>(type, (Class<String>) String.class));
	}
	
	/**
	 * 
	 * @param type
	 * @param allOrNothing
	 * @param couchDbClient
	 * @param ei
	 */
	public LightCouchCouchDbRepository(Class<T> type, boolean allOrNothing, CouchDbClient couchDbClient, EntityInformation<T, String> ei) {
		this.type = type;
        this.ei = ei;
        this.allOrNothing = allOrNothing;
        this.couchDbClient = couchDbClient;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<T> findAll(Pageable pageable) {
		final Long count = count();
		
		final List<T> list = couchDbClient
				//.view(designDocument() + "by_id")
				.view(generalDesignDocument() + "by_uuid")
				.startKey(doctype(), null)
				.endKey(doctype(), new Object())
				.reduce(false)
				.includeDocs(true)
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.query(type);
		
		return new PageImpl<>(list, pageable, count);
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository#findAll(org.springframework.data.domain.Sort)
	 */
	@Override
	public List<T> findAll(Sort sort) {
		throw new IllegalArgumentException("not yet implemented");
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository#findAll()
	 */
	@Override
	public List<T> findAll() {
		return couchDbClient
			//.view(designDocument() + "by_id")
			.view(generalDesignDocument() + "by_uuid")
			.startKey(doctype(), null)
			.endKey(doctype(), new Object())
			.reduce(false)
			.includeDocs(true)
			.query(type);
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository#findAll(java.lang.Iterable)
	 */
	@Override
	public List<T> findAll(Iterable<String> ids) {
		throw new IllegalArgumentException("not yet implemented");
//		Assert.notNull(ids, "The given list of IDs must not be null.");
//
//        return couchDbClient
//        	.view(designDocument() + "by_id")
//        	.keys(ei.toCouchIds(ids))
//        	.reduce(false)
//        	.includeDocs(true)
//        	.query(type);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#save(S)
	 */
	@Override
	public <S extends T> S save(S entity) {
		Assert.notNull(entity, "The given entity must not be null.");

        Response response;
        if (ei.isNew(entity)) {
            response = couchDbClient.save(entity);
        } else {
            response = couchDbClient.update(entity);
        }

        handleResponse(response, entity);

        return entity;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository#save(java.lang.Iterable)
	 */
	@Override
	public <S extends T> List<S> save(Iterable<S> entities) {
		Assert.notNull(entities, "The given list of entities must not be null.");

        final List<S> entityList = StreamSupport.stream(entities.spliterator(), true).collect(Collectors.toList());
        
        executeBulk(entityList, true);

        return entityList; //entities;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findOne(java.io.Serializable)
	 */
	@Override
	public T findOne(String id) {
		Assert.notNull(id, "The given ID must not be null.");

        try {
            return couchDbClient.find(type, ei.toCouchId(id));
        } catch (NoDocumentException e) {
            logger.debug("document with ID " + id + " not found"/*, e*/);
            return null;
        }
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository#findOneByUuid(java.lang.String)
	 */
	@Override
	public Optional<T> findOneByUuid(String uuid) {
		Assert.notNull(uuid, "The given UUID must not be null.");
		
		try {
			final List<T> entities = couchDbClient
				//.view(designDocument() + "by_uuid")
				.view(generalDesignDocument() + "by_uuid")
				.key(doctype(), uuid)
				.reduce(false)
				.includeDocs(true)
				.query(type);
			
			if (entities.isEmpty()) {
				throw new NoDocumentException("Document with " + uuid + " not found");
			}
			
			return Optional.ofNullable(entities.get(0));
        } catch (NoDocumentException e) {
            logger.debug("Error in findOneByUuid", e);
            
            return Optional.empty();
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)
	 */
	@Override
	public boolean exists(String id) {
		Assert.notNull(id, "The given ID must not be null.");

        return couchDbClient.contains(ei.toCouchId(id));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#count()
	 */
	@Override
	public long count() {
		@SuppressWarnings("rawtypes")
        final List<Map> results = couchDbClient
    		//.view(designDocument() + "by_id")
			.view(generalDesignDocument() + "by_uuid")
			.startKey(doctype(), null)
			.endKey(doctype(), new Object())
        	.reduce(true)
        	.query(Map.class);
        
		if (results.size() == 0) {
            return 0;
        } else {
            return ((Number) results.get(0).get("value")).longValue();
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.io.Serializable)
	 */
	@Override
//	@SuppressWarnings("rawtypes")
	public void delete(String id) {
//		Assert.notNull(id, "The given ID must not be null.");
//
//        final List<Map> results = couchDbClient
//        	.view(designDocument() + "by_id") // TODO
//        	.key(ei.toCouchId(id))
//        	.reduce(false)
//        	.includeDocs(false)
//        	.query(Map.class);
//        
//        if (results.size() != 0) {
//            final Map result = (Map) results.get(0).get("value");
//            final Response response = couchDbClient.remove((String) result.get("_id"), (String) result.get("_rev"));
//            
//            handleResponse(response, null);
//        }
		// TODO: implement
		throw new IllegalArgumentException("not yet implemented");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
	 */
	@Override
	public void delete(T entity) {
		Response response = couchDbClient.remove(entity);
        handleResponse(response, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void delete(Iterable<? extends T> entities) {
		Assert.notNull(entities, "The given list of entities must not be null.");

        final List<Map> collection = new ArrayList<>();
        for (T entity : entities) {
            collection.add(createBulkDeleteDocument(ei.getCouchId(entity), ei.getRev(entity)));
        }

        executeBulk(collection, false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#deleteAll()
	 */
	@Override
//	@SuppressWarnings("rawtypes")
	public void deleteAll() {
//        final List<Map> results = couchDbClient
//        	.view(designDocument() + "by_id") // TODO
//        	.reduce(false).includeDocs(false)
//        	.query(Map.class);
//        final List<Map<String, Object>> deleteDocuments = new ArrayList<>();
//        
//        for (Map result : results) {
//            @SuppressWarnings("unchecked")
//			final Map<String, Object> deleteDocument = (Map<String, Object>) result.get("value");
//            
//            deleteDocument.put("_deleted", true);
//            deleteDocuments.add(deleteDocument);
//        }
//
//        executeBulk(deleteDocuments, false);
		
		// TODO: implement
		throw new IllegalArgumentException("not yet implemented");
	}
	
	////
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository#find(at.alladin.nettest.spring.data.couchdb.api.ViewParams)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public <R> R find(ViewParams viewParams) {
		final String designDocument = viewParams.getDesignDocument() != null ? viewParams.getDesignDocument() + "/" : designDocument();
        final View view = couchDbClient.view(designDocument + viewParams.getView());
        viewBuilder.configure(view, viewParams);

        ViewResult viewResult;
        try {
            org.lightcouch.ViewResult lightCouchViewResult = view.queryView(viewParams.getKeyType(),
                    viewParams.getValueType(), viewParams.getDocumentType());
            viewResult = toViewResult(lightCouchViewResult);
        } catch (NoDocumentException e) {
            //logger.debug("no documents found for view parameters  " + viewParams, e);
            // best effort -> TODO LightCouch feature request -> pull request sent 
            viewResult = new ViewResult(); 
        }

        return AdapterUtils.transformViewResult(viewResult, viewParams.getReturnType());
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository#find(at.alladin.nettest.spring.data.couchdb.api.ViewParams, org.springframework.data.domain.Pageable)
	 */
	@Override
	public <R> R find(ViewParams viewParams, Pageable pageable) {
		viewParams.setSkip(pageable.getOffset());
		viewParams.setLimit(pageable.getPageSize());
		
		return find(viewParams);
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository#findByView(at.alladin.nettest.spring.data.couchdb.api.ViewParams)
	 */
	@Override
	public List<T> findByView(ViewParams viewParams) {
		prepareViewParams(viewParams);
		
		return transformViewResultToEntityList(find(viewParams), viewParams.getIncludeDocs());
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository#findByView(at.alladin.nettest.spring.data.couchdb.api.ViewParams, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<T> findByView(ViewParams viewParams, Pageable pageable) {
		prepareViewParams(viewParams);
		
		final List<T> entityList = transformViewResultToEntityList(find(viewParams, pageable), viewParams.getIncludeDocs());
		return new PageImpl<>(entityList, pageable, -1); // TODO: calculate total by fetching the view without and with reduce (_count)
	}
	
	///
	
	/**
	 * 
	 * @param viewResult
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<T> transformViewResultToEntityList(ViewResult viewResult, boolean includeDocs) {
		return viewResult.getRows()
			.stream()
			.map(viewResultRow -> {
				return (T) (includeDocs ? viewResultRow.getDoc() : viewResultRow.getValue());
			}).collect(Collectors.toList());
	}
	
	/**
	 * 
	 * @param viewParams
	 */
	private void prepareViewParams(ViewParams viewParams) {
		if (viewParams.getIncludeDocs() == null) {
			viewParams.setIncludeDocs(false);
		}
		
		if (viewParams.getKeyType() == null) {
			viewParams.setKeyType(String.class);
		}
		
		if (viewParams.getValueType() == null) {
			viewParams.setValueType(type);
		}
		
		if (viewParams.getDocumentType() == null) {
			viewParams.setDocumentType(type);
		}
		
		if (viewParams.getDesignDocument() == null) {
			viewParams.setDesignDocument(type.getSimpleName());
		}
	}
	
	////
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
    protected ViewResult toViewResult(org.lightcouch.ViewResult viewResult) {
        ViewResult result = new ViewResult();
        result.setOffset(result.getOffset());
        result.setTotalRows(viewResult.getTotalRows());
        result.setUpdateSeq(viewResult.getUpdateSeq());

        List<org.lightcouch.ViewResult.Rows> rows = viewResult.getRows();
        List<ViewResultRow> resultRows = new ArrayList<>();
        for (org.lightcouch.ViewResult.Rows row : rows) {
            ViewResultRow resultRow = new ViewResultRow();
            resultRow.setId(row.getId());
            resultRow.setDoc(row.getDoc());
            resultRow.setKey(row.getKey());
            resultRow.setValue(row.getValue());
            resultRows.add(resultRow);
        }

        result.setRows(resultRows);

        return result;
    }
	
	////
	
	/**
	 * 
	 * @return
	 */
	protected String designDocument() {
        return type.getSimpleName() + "/";
    }
	
	/**
	 * 
	 * @return
	 */
	protected String generalDesignDocument() {
		return "General/";
	}
	
	/**
	 * 
	 * @return
	 */
	protected String doctype() {
		return type.getSimpleName();
	}
	
	/**
     * @param response the response
     * @param entity Null if the entity shall not be updated
     */
    protected void handleResponse(Response response, T entity) {
        if (response.getError() != null) {
            throw new RuntimeException(String.format("error: %s, reason: %s, entity id: %s, rev: %s",
                    response.getError(), response.getReason(), response.getId(), response.getRev()));
        } else if (entity != null) {
            if (ei.isNew(entity)) {
                ei.setId(entity, response.getId());
            }
            
            ei.setRev(entity, response.getRev());
        }
    }
    
    protected Map<String, Object> createBulkDeleteDocument(String id, String revision) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("_id", id);
        map.put("_rev", revision);
        map.put("_deleted", true);
        return map;
    }

    @SuppressWarnings("unchecked")
    protected void executeBulk(List<?> list, boolean updateEntities) {
        List<Response> responses = couchDbClient.bulk(list, allOrNothing);

        // handle errors
        List<BulkOperationError> errors = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        int index = 0;
        for (Response response : responses) {
            if (response.getError() != null) {
                errors.add(new BulkOperationError(response.getId(), response.getRev(), response.getError(), response
                        .getReason()));
                messages.add(response.toString());
            } else if (updateEntities) {
                T entity = (T) list.get(index);
                if (ei.isNew(entity)) {
                    ei.setId(entity, response.getId());
                }
                ei.setRev(entity, response.getRev());
            }
            index++;
        }
        if (errors.size() > 0) {
            throw new BulkOperationException(StringUtils.collectionToCommaDelimitedString(messages), errors);
        } else {
            logger.debug("All documents have been processed.");
        }
    }
    
    @Override
    public CouchDbClient getCouchDbClient() {
    	return couchDbClient;
    }
    
    public View getView(String view) {
    	return couchDbClient.view(designDocument() + view);
	}
}
