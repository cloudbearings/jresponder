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
package org.jresponder.domain;

/**
 * Enumerates types of log entries
 * 
 * @author bradpeabody
 *
 */
public enum LogEntryType {

	/** someone was subscribed */
	SUBSCRIBED,
	/** a subscribe call was made for an email/message group that already existed */
	RESUBSCRIBED,
	/** a message was transmitted */
	MESSAGE_SENT,
	/** a message was skipped (because it rendered to empty content) */
	MESSAGE_SKIPPED,
	/** a test message was transmitted */
	TEST_MESSAGE_SENT,
	/** a subscription ran out - got to the end of the list */
	SUBSCRIPTION_DONE,
	/** subscription ended prematurely due to some misconfiguration or error */
	SUBSCRIPTION_ABORTED,
	
}