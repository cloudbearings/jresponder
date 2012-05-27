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

/**
 * A group of messages which can be subscribed to.
 * 
 * @author bradpeabody
 *
 */
public interface MessageGroup {

	/**
	 * The id of this message group
	 * @return
	 */
	public String getName();
	
	/**
	 * The text description of this message group (shows in the admin interface)
	 * @return
	 */
	public String getDescription();
	
	/**
	 * Get the MessageRef instances associated with this group.
	 */
	public List<MessageRef> getMessageRefList();
	
	/**
	 * Get a MessageRef by it's id/name
	 * @param aName
	 * @return
	 */
	public MessageRef getMessageRefByName(String aName);
	
	/**
	 * Get the MessageRef instance to use for opt-in confirmation email.
	 * @return
	 */
	public MessageRef getOptInConfirmMessageRef();

	/**
	 * Get the index of the MessageRef.
	 * The index returned can be used directly on the result of
	 * {@link #getMessageRefList()}.
	 * 
	 * @param aName
	 * @return the index or -1 if not found
	 */
	public int indexOfName(String aName);
	
	/**
	 * Refresh/update - may change MessageRefList (always refreshes, does
	 * not try to optimize avoid reloading if not needed)
	 */
	public void refresh();
	
	/**
	 * Refresh/update if needed - avoids the work if it doesn't look like
	 * things have changed.
	 */
	public void conditionalRefresh();
	
}
