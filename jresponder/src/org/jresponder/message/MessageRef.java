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

import java.util.List;

import javax.mail.internet.MimeMessage;

import org.jresponder.domain.Subscriber;
import org.jresponder.domain.Subscription;
import org.jresponder.engine.SendConfig;

/**
 * A message reference - this corresponds to an individual message that
 * can be sent to a subscriber.
 * 
 * @author bradpeabody
 */
public interface MessageRef {
	
	/**
	 * Refresh the contents of this MessageRef (always refreshes, does
	 * not try to optimize avoid reloading if not needed) 
	 */
	public void refresh() throws InvalidMessageException;

	/**
	 * Refresh only if needed
	 * @return true if it did do a refresh, false if it wasn't needed
	 * @throws InvalidMessageException
	 */
	public boolean conditionalRefresh() throws InvalidMessageException;
	
	/**
	 * Get the ID of this message.  Unique within it's MessageGroup.
	 * @return
	 */
	public String getName();
	
	/**
	 * Get a property by string name
	 * @param aName
	 * @return
	 */
	public String getProp(String aName);
	
	/**
	 * Get a property by enum value
	 */
	public String getProp(MessageRefProp aName);

	/**
	 * Get names of all properties available
	 * @return
	 */
	public List<String> getPropNames();
	
	/**
	 * Gets the number of milliseconds that this message should be sent
	 * after the last message. 
	 * 
	 * @return the number of milliseconds, or null to indicate that this
	 * property was not set
	 */
	public Long getWaitAfterLastMessage();
	
	/**
	 * Create a message that should be sent to this subscriber.
	 * @return true if message should be sent, false if message was not created
	 * (i.e. should be aborted)
	 */
	public boolean populateMessage(MimeMessage aMimeMessage, SendConfig aSendConfig, Subscriber aSub, Subscription aSubscription);

	
}
