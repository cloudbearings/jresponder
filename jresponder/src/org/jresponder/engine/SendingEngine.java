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
package org.jresponder.engine;

import org.jresponder.domain.LogEntryType;
import org.jresponder.domain.Subscriber;
import org.jresponder.domain.Subscription;
import org.jresponder.message.MessageGroup;
import org.jresponder.message.MessageRef;

/**
 * Interface for SendingEngine.  Mainly here to make the AOP proxy stuff
 * happy.
 * 
 * @author bradpeabody
 *
 */
public interface SendingEngine {
	
	public void engineLoop();
	
	public boolean engineLoopProcessOne();
	
	/**
	 * Send a single message.  Also makes a LogEntry with the given type.
	 * 
	 * @param aOptInConfirmMessageRef
	 * @param aMessageGroup
	 * @param aSubscriber
	 * @param aSubscription
	 * @param aLogEntryType
	 * @return true if the message was sent, false if the message opted to
	 *           be skipped (by having no body)
	 */
	public boolean sendMessage (
					MessageRef aOptInConfirmMessageRef,
					MessageGroup aMessageGroup,
					Subscriber aSubscriber,
					Subscription aSubscription,
					LogEntryType aLogEntryType
				);

}
