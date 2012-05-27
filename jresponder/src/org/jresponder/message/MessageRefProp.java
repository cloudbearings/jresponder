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

public enum MessageRefProp {

	/**
	 * Message subject line
	 */
	JR_SUBJECT,
	
	/**
	 * From email address
	 */
	JR_FROM_EMAIL,
	
	/**
	 * From display name
	 */
	JR_FROM_NAME,
	
	/**
	 * The time following the last message (or initial subscription if no message
	 * sent yet) to send this message.  Values a
	 */
	JR_WAIT_AFTER_LAST_MESSAGE,
	
	/**
	 * The main HTML body content
	 */
	JR_HTML_BODY,
	
	/**
	 * THe text body content
	 */
	JR_TEXT_BODY,
	
	
}
