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

/**
 * Enumerates the various types of errors that can occur during service calls.
 * Generally, try to reuse types from here when you can, and only add new ones
 * if the scenario really is something unique that hasn't come up in the app
 * thus far.
 * 
 * @author bradpeabody
 */
public enum ServiceExceptionType {

	/**
	 * The indicated subscription could not be found
	 */
	NO_SUCH_SUBSCRIPTION(1001),
	
	/**
	 * The indicated subscriber could not be found
	 */
	NO_SUCH_SUBSCRIBER(1002),
	
	/**
	 * Subscriber is not marked as "OK", and so we're refusing to do the
	 * requested action.
	 */
	SUBSCRIBER_NOT_OK(1003),
	
	/**
	 * Subscription status was not what it was supposed to be for the indicated
	 * action.
	 */
	UNEXPECTED_SUBSCRIPTION_STATUS(1004),
	
	// for the "standardized" error codes, see: http://www.jsonrpc.org/specification
	
	/**
	 * The "params" field passed in a JSON-RPC 2.0 call was not in the right
	 * format.
	 */
	INVALID_PARAMS(32600),
	
	/**
	 * A one of the elements inside "params" was in a bad format
	 */
	PARAM_BAD_FORMAT(32600),
	
	/**
	 * A required parameter was not present
	 */
	PARAM_REQUIRED(-32602),
	
//	/**
//	 * Other unknown error - try not to use this!
//	 */
//	UNKNOWN_ERROR
	
	;
	
	protected int code;
	
	ServiceExceptionType(int aCode) {
		code = aCode;
	}
	
	public int getCode() {
		return code;
	}
	
	/**
	 * Right now this is just an alias for toString() - could use this
	 * to add human-readable descriptions, if it's worth it. 
	 * 
	 * @return
	 */
	public String getDescription() {
		return this.toString();
	}
	
}
