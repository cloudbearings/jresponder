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

import java.util.List;

import org.jresponder.domain.Subscriber;
import org.jresponder.domain.Subscription;
import org.jresponder.message.MessageGroup;
import org.jresponder.message.MessageRef;

/**
 * A group of SendConfig instances.  This is used as a holder, mainly so
 * other parts of the system know how to access the particular SendConfig
 * they want, and to facilitate Spring configuration.
 * 
 * @author bradpeabody
 *
 */
public class SendConfigGroup {

	private List<SendConfig> sendConfigList;

	/**
	 * @return the sendConfigList
	 */
	public List<SendConfig> getSendConfigList() {
		return sendConfigList;
	}

	/**
	 * @param sendConfigList the sendConfigList to set
	 */
	public void setSendConfigList(List<SendConfig> sendConfigList) {
		this.sendConfigList = sendConfigList;
	}
	
	/**
	 * Returns a matching send configuration.  Or null if none match
	 * (which should not happen in a properly configured setup).
	 * 
	 * @param aSubscriber
	 * @param aSubscription
	 * @param aMessageGroup
	 * @param aMessageRef
	 * @return
	 */
	public SendConfig lookupSendConfig(Subscriber aSubscriber, Subscription aSubscription, MessageGroup aMessageGroup, MessageRef aMessageRef) {
		
		for (SendConfig mySendConfig: sendConfigList) {
			if (mySendConfig.matches(aSubscriber, aSubscription, aMessageGroup, aMessageRef)) {
				return mySendConfig;
			}
		}
		
		return null;
		
	}

	
}
