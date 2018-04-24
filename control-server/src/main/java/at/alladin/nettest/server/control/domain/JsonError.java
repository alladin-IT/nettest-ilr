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

package at.alladin.nettest.server.control.domain;

import com.google.gson.annotations.Expose;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class JsonError {

	/**
	 * 
	 */
	@Expose
	private int code;
	
	/**
	 * 
	 */
	@Expose
	private String message;
	
	/**
	 * 
	 */
	@Expose
	private String fields;
	
	/**
	 * 
	 */
	public JsonError() {
		
	}
	
	/**
	 * 
	 * @param code
	 * @param message
	 */
	public JsonError(int code, String message) {
		this(code, message, null);
	}
	
	/**
	 * 
	 * @param code
	 * @param message
	 * @param fields
	 */
	public JsonError(int code, String message, String fields) {
		this.code = code;
		this.message = message;
		this.fields = fields;
	}

	/**
	 * 
	 * @return
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * 
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 
	 * @return
	 */
	public String getFields() {
		return fields;
	}

	/**
	 * 
	 * @param fields
	 */
	public void setFields(String fields) {
		this.fields = fields;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Error [code=" + code + ", message=" + message + ", fields=" + fields + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JsonError other = (JsonError) obj;
		if (code != other.code)
			return false;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}
}
