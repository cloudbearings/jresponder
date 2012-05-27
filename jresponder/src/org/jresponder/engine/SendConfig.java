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

import java.util.Map;

import org.jresponder.domain.Subscriber;
import org.jresponder.domain.Subscription;
import org.jresponder.message.MessageGroup;
import org.jresponder.message.MessageRef;

/**
 * A configuration which describes how a message is sent, and what messages it
 * applies to.
 * 
 * @author bradpeabody
 */
public class SendConfig {

	private String senderEmailPattern;
	private String filterMessageGroup;
	// TODO: add dkim stuff here, once we figure it out 
	private Map<String,Object> additional;


	/**
	 * Regular expression to match against message group.  If not set, this
	 * config matches all message groups (i.e. null and ".*" would be 
	 * functionality equivalent).
	 * 
	 * @return the filterMessageGroup
	 */
	public String getFilterMessageGroup() {
		return filterMessageGroup;
	}

	/**
	 * @param filterMessageGroup the filterMessageGroup to set
	 */
	public void setFilterMessageGroup(String filterMessageGroup) {
		this.filterMessageGroup = filterMessageGroup;
	}

	/**
	 * The pattern used as the sender email address.
	 * @return the senderEmailPattern
	 */
	public String getSenderEmailPattern() {
		return senderEmailPattern;
	}

	/**
	 * @param senderEmailPattern the senderEmailPattern to set
	 */
	public void setSenderEmailPattern(String senderEmailPattern) {
		this.senderEmailPattern = senderEmailPattern;
	}

	/**
	 * Generic additional properties - can be accessed from messages or
	 * is also available for used by extensions/customizations.
	 * @return the additional
	 */
	public Map<String,Object> getAdditional() {
		return additional;
	}

	/**
	 * @param additional the additional to set
	 */
	public void setAdditional(Map<String,Object> additional) {
		this.additional = additional;
	}
	
	/**
	 * Check if this instances applies.
	 * 
	 * @param aSubscriber
	 * @param aSubscription
	 * @param aMessageGroup
	 * @param aMessageRef
	 * @return
	 */
	public boolean matches(Subscriber aSubscriber, Subscription aSubscription, MessageGroup aMessageGroup, MessageRef aMessageRef) {
		
		if (filterMessageGroup == null) {
			return true;
		}
		
		String myMessageGroupName = aMessageGroup.getName();
		
		// sanity check
		if (myMessageGroupName == null) { return false; }
		
		// return true if it matches the regex
		return myMessageGroupName.matches(filterMessageGroup);
		
	}
	
}
