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
package org.jresponder.message;

/**
 * Something about a message was invalid (invalid format, file doesn't
 * exist, etc.)  Can be used to wrap lower level exceptions (such as
 * IOException).
 * 
 * @author bradpeabody
 *
 */
public class InvalidMessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -56312488826623701L;
	
	public InvalidMessageException(Throwable t) {
		super(t);
	}
	
	public InvalidMessageException(String m) {
		super(m);
	}
	
	public InvalidMessageException(String m, Throwable t) {
		super(m, t);
	}

}
