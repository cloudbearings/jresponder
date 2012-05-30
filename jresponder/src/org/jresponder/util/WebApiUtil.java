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
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONValue;

import org.jresponder.service.ServiceException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Utility stuff that is specific to the JSON API
 * @author bradpeabody
 *
 */
@Component("jrWebApiUtil")
public class WebApiUtil implements InitializingBean {
	
	/* ====================================================================== */
	/* singleton support with override - boiler plate (see package desc)      */
	private static WebApiUtil instance;
	public static WebApiUtil getInstance() { return instance; }
	public static void setInstance(WebApiUtil inst) { instance = inst; }
	public void afterPropertiesSet() { setInstance(this); }
	/* ====================================================================== */

	
//	/**
//	 * Parse the params to a map
//	 * @param aParams
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public Map<String,Object> paramsToMap(String aParams) {
//		
//		Object ret = JSONValue.parse(aParams);
//		if (ret instanceof Map) {
//			return ((Map<String,Object>)ret);
//		}
//		
//		throw new IllegalArgumentException("Unable to parse parameters or wrong type - expected object/map");
//	}
//	
//	/**
//	 * Parse the params to a list
//	 * @param aParams
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public List<Object> paramsToList(String aParams) {
//		
//		Object ret = JSONValue.parse(aParams);
//		if (ret instanceof List) {
//			return ((List<Object>)ret);
//		}
//		
//		throw new IllegalArgumentException("Unable to parse parameters or wrong type - expected array/list");
//	}

	/**
	 * Helper that converts an object to a JSON-RPC 2.0 response with a result
	 * and returns a corresponding
	 * ResponseEntity which can be returned directly back to Spring MVC and go
	 * to the browser.
	 * @param aId the JSON-RPC 2.0 request id
	 * @param aCallback if non-null means JSONP is requested with the specified
	 *                  callback function name 
	 * @param aObject the response object - usually a Map
	 * @return
	 */
	public ResponseEntity<String> jsonRpcResult(String aId, String aCallback, Object aObject) {
    	
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put("id", aId);
		ret.put("result", aObject);
		ret.put("jsonrpc", "2.0");
		
		String myResultString = JSONValue.toJSONString(ret);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		
		// normal JSON response
		if (aCallback == null || aCallback.trim().length() < 1) {
			responseHeaders.set("Content-type", "application/json-rpc");
		}
		// JSONP handled differently
		else {
			responseHeaders.set("Content-type", "application/javascript");
			// wrap result in JSONP callback
			myResultString = aCallback + "&&" + aCallback + "(" + myResultString + ")";
		}
		
		
		return new ResponseEntity<String>(myResultString,
				responseHeaders, HttpStatus.CREATED);
	}
	
	/**
	 * Make a JSON-RPC 2.0 error from a {@link ServiceException}.
	 * @return
	 */
	public ResponseEntity<String> jsonRpcError(String aId, String aCallback, ServiceException e) {
    	
		Map<String,Object> err = new HashMap<String,Object>();
		err.put("code", e.getServiceExceptionType().getCode());
		err.put("message", e.toString());
		err.put("data", e.getDataMap());
		
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put("id", aId);
		ret.put("error", err);
		ret.put("jsonrpc", "2.0");
		
		String myResultString = JSONValue.toJSONString(ret);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		
		// normal JSON response
		if (aCallback == null || aCallback.trim().length() < 1) {
			responseHeaders.set("Content-type", "application/json-rpc");
		}
		// JSONP handled differently
		else {
			responseHeaders.set("Content-type", "application/javascript");
			// wrap result in JSONP callback
			myResultString = aCallback + "&&" + aCallback + "(" + myResultString + ")";
		}

		
		return new ResponseEntity<String>(myResultString,
				responseHeaders, HttpStatus.CREATED);
	}
	
//	/**
//	 * Make a JSON-RPC 2.0 error
//	 * @param aId the JSON-RPC 2.0 request id
//	 * @param aCallback if non-null means JSONP is requested with the specified
//	 *                  callback function name 
//	 * @param aObject
//	 * @return
//	 */
//	public ResponseEntity<String> jsonRpcError(String aId, String aCallback, Long aErrorCode, String aMessage, Object aData) {
//    	
//		Map<String,Object> err = new HashMap<String,Object>();
//		err.put("code", aErrorCode);
//		err.put("message", aMessage);
//		if (aData != null) {
//			err.put("data", aData);
//		}
//		
//		Map<String,Object> ret = new HashMap<String,Object>();
//		ret.put("id", aId);
//		ret.put("error", err);
//		ret.put("jsonrpc", "2.0");
//		
//		String myResultString = JSONValue.toJSONString(ret);
//		
//		HttpHeaders responseHeaders = new HttpHeaders();
//		
//		// normal JSON response
//		if (aCallback == null || aCallback.trim().length() < 1) {
//			responseHeaders.set("Content-type", "application/json-rpc");
//		}
//		// JSONP handled differently
//		else {
//			responseHeaders.set("Content-type", "application/javascript");
//			// wrap result in JSONP callback
//			myResultString = aCallback + "&&" + aCallback + "(" + myResultString + ")";
//		}
//
//		
//		return new ResponseEntity<String>(myResultString,
//				responseHeaders, HttpStatus.CREATED);
//	}
	
//	/**
//	 * Like namesake but with null data.
//	 * 
//	 * @param aId the JSON-RPC 2.0 request id
//	 * @param aCallback if non-null means JSONP is requested with the specified
//	 *                  callback function name 
//	 * @param aErrorCode
//	 * @param aMessage
//	 * @return
//	 */
//	public ResponseEntity<String> jsonRpcError(String aId, String aCallback, Long aErrorCode, String aMessage) {
//		return jsonRpcError(aId, aCallback, aErrorCode, aMessage, null);
//	}
	
	/**
	 * Generic 404 result
	 * @return
	 */
	public ResponseEntity<String> result404(String aMessage) {
		HttpHeaders responseHeaders = new HttpHeaders();
		return new ResponseEntity<String>(aMessage != null ? aMessage : "Not found",
				responseHeaders, HttpStatus.NOT_FOUND);
	}
	
	/**
	 * Dump result as plain text
	 * 
	 * @param aText
	 * @return
	 */
	public ResponseEntity<String> plainTextResult(String aText) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-type", "text/plain");
		return new ResponseEntity<String>(aText,
				responseHeaders, HttpStatus.CREATED);
		
	}
	
}
