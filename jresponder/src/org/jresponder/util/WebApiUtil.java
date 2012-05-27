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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility stuff that is specific to the JSON API
 * @author bradpeabody
 *
 */
public class WebApiUtil {
	
	/**
	 * Parse the params to a map
	 * @param aParams
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> paramsToMap(String aParams) {
		
		Object ret = JSONValue.parse(aParams);
		if (ret instanceof Map) {
			return ((Map<String,Object>)ret);
		}
		
		throw new IllegalArgumentException("Unable to parse parameters or wrong type - expected object/map");
	}
	
	/**
	 * Parse the params to a list
	 * @param aParams
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> paramsToList(String aParams) {
		
		Object ret = JSONValue.parse(aParams);
		if (ret instanceof List) {
			return ((List<Object>)ret);
		}
		
		throw new IllegalArgumentException("Unable to parse parameters or wrong type - expected array/list");
	}

	/**
	 * Helper that converts an object to a JSON-RPC 2.0 response with a result
	 * and returns a corresponding
	 * ResponseEntity which can be returned directly back to Spring MVC and go
	 * to the browser. 
	 * @param aObject the response object - usually a Map
	 * @return
	 */
	public static ResponseEntity<String> jsonRpcResult(String aId, Object aObject) {
    	
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put("id", aId);
		ret.put("result", aObject);
		ret.put("jsonrpc", "2.0");
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-type", "application/json-rpc");
		return new ResponseEntity<String>(JSONValue.toJSONString(ret),
				responseHeaders, HttpStatus.CREATED);
	}
	
	/**
	 * Make a JSON-RPC 2.0 error
	 * @param aObject
	 * @param aId
	 * @return
	 */
	public static ResponseEntity<String> jsonRpcError(String aId, Long aErrorCode, String aMessage, Object aData) {
    	
		Map<String,Object> err = new HashMap<String,Object>();
		err.put("code", aErrorCode);
		err.put("message", aMessage);
		if (aData != null) {
			err.put("data", aData);
		}
		
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put("id", aId);
		ret.put("error", err);
		ret.put("jsonrpc", "2.0");

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-type", "application/json-rpc");
		return new ResponseEntity<String>(JSONValue.toJSONString(ret),
				responseHeaders, HttpStatus.CREATED);
	}
	/**
	 * Like namesake but with null data.
	 * 
	 * @param aId
	 * @param aErrorCode
	 * @param aMessage
	 * @return
	 */
	public static ResponseEntity<String> jsonRpcError(String aId, Long aErrorCode, String aMessage) {
		return jsonRpcError(aId, aErrorCode, aMessage, null);
	}
	
	/**
	 * Generic 404 result
	 * @return
	 */
	public static ResponseEntity<String> result404(String aMessage) {
		HttpHeaders responseHeaders = new HttpHeaders();
		return new ResponseEntity<String>(aMessage != null ? aMessage : "Not found",
				responseHeaders, HttpStatus.NOT_FOUND);
	}
	
	public static ResponseEntity<String> plainTextResult(String aText) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-type", "text/plain");
		return new ResponseEntity<String>(aText,
				responseHeaders, HttpStatus.CREATED);
		
	}
	
}
