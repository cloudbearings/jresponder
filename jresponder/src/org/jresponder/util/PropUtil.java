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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import net.minidev.json.JSONValue;

/**
 * Utilities related to properties and maps and cool stuff like that.
 * 
 * @author bradpeabody
 *
 */
@Component("jrPropUtil")
public class PropUtil implements InitializingBean {
	
	/* ====================================================================== */
	/* singleton support with override - boiler plate (see package desc)      */
	private static PropUtil instance;
	public static PropUtil getInstance() { return instance; }
	public static void setInstance(PropUtil inst) { instance = inst; }
	public void afterPropertiesSet() { setInstance(this); }
	/* ====================================================================== */

	/**
	 * Merge from the aFromMap to the aIntoMap (dst &lt;- src) (assembly style).
	 * Null value in from map causes corresponding entry in dst map to be
	 * removed.
	 * @param aIntoMap
	 * @param aFromMap
	 * @return Map returns aIntoMap - in case it helps you chain calls together easier
	 */
	public Map<String,Object> propMerge(Map<String,Object> aIntoMap, Map<String,Object> aFromMap) {
		for (String myKey: aFromMap.keySet()) {
			Object myValue = aFromMap.get(myKey);
			if (myValue == null) {
				aIntoMap.remove(myKey);
			}
			else {
				aIntoMap.put(myKey, myValue);
			}
		}
		return aIntoMap;
	}
	
	/**
	 * Helper method to make a map from the keys and values passed - which are
	 * in the form of
	 * <code>mkprops(key1, value1, key2, value3, key3, value3, ...)</code>
	 * @param args
	 * @return
	 */
	public Map<String,Object> mkprops(Object...args) {
		if (args.length % 2 != 0) {
			throw new IllegalArgumentException("mkmap must have an even number of arguments - check your code!");
		}
		Map<String,Object> ret =  new HashMap<String,Object>();
		
		for (int i = 0; i < args.length; i += 2) {
			Object myKey = args[i];
			Object myValue = args[i+1];
			ret.put(myKey.toString(), myValue);
		}
		
		return ret;
	}
	
	/**
	 * Convert a props string to a Map
	 * @param aString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> propsToMap(String aString) {
		if (aString == null || aString.trim().length() < 1) {
			return null;
		}
		Object myO = JSONValue.parse(aString);
		Map<String,Object> myRet = (Map<String,Object>)myO;
		return myRet;
	}
	
	/**
	 * Convert a props Map to a string
	 * @param aMap
	 * @return
	 */
	public String propsToString(Map<String,Object> aMap) {
		if (aMap == null) {
			return null;
		}
		return JSONValue.toJSONString(aMap);
	}
	
}

