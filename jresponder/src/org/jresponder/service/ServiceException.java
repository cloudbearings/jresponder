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
package org.jresponder.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception throw by services (or potentially useful for other parts of the
 * app too).  Represents an error that corresponds to an exact type -
 * as listed in {@link ServiceExceptionType}.  Also has an optional
 * "data map", which is name/value pairs that describe the error further.
 * The idea is that services or lower level areas can throws one of these
 * exceptions and it can be directly translated into a JSON-RPC 2.0 error
 * message and sent back to the caller.  This avoids complicated error
 * handling logic higher up and should make things simple.
 * 
 * <p>
 * TODO: This class intentionally does not extend RuntimeException (because
 * callers should be catching this and converting it to an error
 * response).  However, this means that be default database transactions
 * are not being rolled back when this is thrown - not sure if this
 * is correct or not, needs to be reviewed (probably is not correct
 * and needs adjustment in the Spring settings).
 * 
 * @author bradpeabody
 *
 */
@SuppressWarnings("serial")
public class ServiceException extends Exception {

	protected ServiceExceptionType serviceExceptionType;
	protected Map<String,Object> dataMap;
	

	/**
	 * Exception with an appropriate type.
	 * @param aServiceExceptionType
	 */
	public ServiceException(ServiceExceptionType aServiceExceptionType) {
		super(aServiceExceptionType.getDescription());
		serviceExceptionType = aServiceExceptionType;
		assert(serviceExceptionType != null);
		dataMap = new HashMap<String,Object>();
	}
	
	/**
	 * Exception with type and map of data to describe it 
	 * @param aServiceExceptionType
	 * @param aDataMap
	 */
	public ServiceException(ServiceExceptionType aServiceExceptionType, Map<String,Object> aDataMap) {
		super(aServiceExceptionType.getDescription());
		serviceExceptionType = aServiceExceptionType;
		assert(serviceExceptionType != null);
		dataMap = aDataMap;
	}
	
	/**
	 * Exception with type and map of data to describe it, also with a
	 * cause exception. (Note that the cause exception, while it may appear
	 * in the log, will generally not be seen by the caller at all.)
	 * 
	 * @param aServiceExceptionType
	 * @param aDataMap
	 * @param aParent
	 */
	public ServiceException(ServiceExceptionType aServiceExceptionType, Map<String,Object> aDataMap, Throwable aParent) {
		super(aServiceExceptionType.getDescription(), aParent);
		serviceExceptionType = aServiceExceptionType;
		assert(serviceExceptionType != null);
		dataMap = aDataMap;
	}
	
	/**
	 * Exception with type and with a cause exception, the data map is left
	 * empty. (Note that the cause exception, while it may appear
	 * in the log, will generally not be seen by the caller at all.)
	 * 
	 * @param aServiceExceptionType
	 * @param aParent
	 */
	public ServiceException(ServiceExceptionType aServiceExceptionType, Throwable aParent) {
		super(aServiceExceptionType.getDescription(), aParent);
		serviceExceptionType = aServiceExceptionType;
		assert(serviceExceptionType != null);
		dataMap = new HashMap<String,Object>();
	}
	
	/**
	 * Return
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {
		return serviceExceptionType.getDescription() + (getCause() != null ? " :: " + super.toString() : "");
	}

	/**
	 * @return the serviceExceptionType
	 */
	public ServiceExceptionType getServiceExceptionType() {
		return serviceExceptionType;
	}
	
	/**
	 * @return the dataMap
	 */
	public Map<String, Object> getDataMap() {
		return dataMap;
	}

}
