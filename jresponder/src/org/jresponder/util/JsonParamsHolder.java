/**
 * =========================================================================
 *     __ ____   ____  __  ____    ___   __  __ ____    ____ ____ 
 *     || || \\ ||    (( \ || \\  // \\  ||\ || || \\  ||    || \\
 *     || ||_// ||==   \\  ||_// ((   )) ||\\|| ||  )) ||==  ||_//
 *  |__|| || \\ ||___ \_)) ||     \\_//  || \|| ||_//  ||___ || \\
 * =========================================================================
 *
 * Copyright 2012 Brad Peabody
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * =========================================================================
 */
package org.jresponder.util;

import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONValue;

import org.jresponder.service.ServiceException;
import org.jresponder.service.ServiceExceptionType;

/**
 * Util class to wrap parameters from JSON-RPC 2 calls and make
 * them easier to get at and automatically catch errors and throw
 * the right exception.
 * 
 * @author bradpeabody
 */
public class JsonParamsHolder {
	
	/**
	 * Set of empty props, can be used where you need an empty map instead of null
	 */
	public static final Map<String,Object> EMPTY_PROPS = new HashMap<String,Object>();
	
	protected Map<String,Object> params;
	
	/**
	 * Makes a new instance from the given params JSON
	 * 
	 * @param aParamJson
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public JsonParamsHolder(String aParamJson) throws ServiceException {
		
		assert(aParamJson != null);
		
		try {
			Object ret = JSONValue.parseWithException(aParamJson);
			params = ((Map<String,Object>)ret);
		}
		catch (Exception e) {
			throw new ServiceException(ServiceExceptionType.INVALID_PARAMS, e);
		}

	}
	
	/**
	 * Gets a parameter as a string. If parameter is not found
	 * then the given default is returned instead. 
	 * 
	 * @param aParamName
	 * @param aDefault
	 * @return
	 */
	public String getString(String aParamName, String aDefault) throws ServiceException {
		assert(aParamName != null);
		Object myValue = params.get(aParamName);
		if (myValue != null) {
			return myValue.toString();
		}
		return aDefault;
	}
	
	/**
	 * Gets a required parameter as a string.  If not found exception
	 * is thrown.
	 * 
	 * @param aParamName
	 * @return
	 * @throws ServiceException
	 */
	public String getString(String aParamName) throws ServiceException {
		assert(aParamName != null);
		Object myValue = params.get(aParamName);
		if (myValue != null) {
			return myValue.toString();
		}
		throw new ServiceException(ServiceExceptionType.PARAM_REQUIRED,
				PropUtil.getInstance().mkprops("name", aParamName));
	}
	
	/**
	 * Gets a parameter as a Long.  If not found then default is
	 * returned.
	 * 
	 * @param aParamName
	 * @param aDefault
	 * @return
	 * @throws ServiceException
	 */
	public Long getInteger(String aParamName, Long aDefault) throws ServiceException {
		assert(aParamName != null);
		Object myValue = params.get(aParamName);
		if (myValue != null) {
			try {
				return Long.parseLong(myValue.toString());
			}
			catch (NumberFormatException e) {
				throw new ServiceException(ServiceExceptionType.PARAM_BAD_FORMAT, e);
			}
			
		}
		return aDefault;

	}
	
	/**
	 * Gets a parameter as a Long.  If not found then exception is thrown.
	 * @param aParamName
	 * @return
	 * @throws ServiceException
	 */
	public Long getInteger(String aParamName) throws ServiceException {
		assert(aParamName != null);
		Object myValue = params.get(aParamName);
		if (myValue != null) {
			try {
				return Long.parseLong(myValue.toString());
			}
			catch (NumberFormatException e) {
				throw new ServiceException(ServiceExceptionType.PARAM_BAD_FORMAT, e);
			}
			
		}
		throw new ServiceException(ServiceExceptionType.PARAM_REQUIRED,
				PropUtil.getInstance().mkprops("name", aParamName));
	}
	
	/**
	 * Gets a parameter as a props Map. If parameter is not found
	 * then the given default is returned instead. 
	 * 
	 * @param aParamName
	 * @param aDefault
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getProps(String aParamName, Map<String,Object> aDefault) throws ServiceException {
		assert(aParamName != null);
		Object myValue = params.get(aParamName);
		if (myValue != null) {
			return (Map<String,Object>)myValue;
		}
		return aDefault;
	}
	
	/**
	 * Gets a required parameter as a string.  If not found exception
	 * is thrown.
	 * 
	 * @param aParamName
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getProps(String aParamName) throws ServiceException {
		assert(aParamName != null);
		Object myValue = params.get(aParamName);
		if (myValue != null) {
			return (Map<String,Object>)myValue;
		}
		throw new ServiceException(ServiceExceptionType.PARAM_REQUIRED,
				PropUtil.getInstance().mkprops("name", aParamName));
	}

}
