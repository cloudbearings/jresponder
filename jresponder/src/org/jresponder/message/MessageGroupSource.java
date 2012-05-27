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
 * A source for multiple message groups.  The basic idea is that this
 * is an overall folder, each subfolder in which corresponds to a MessageGroup,
 * and each file inside those corresponds to a MessageRef.
 * 
 * @author bradpeabody
 *
 */
public interface MessageGroupSource {

	/**
	 * Get a list of message groups
	 * @return
	 */
	public List<MessageGroup> getMessageGroupList();
	
	/**
	 * Get message group by it's id, or null if no such message group.
	 * @param aName
	 * @return
	 */
	public MessageGroup getMessageGroupByName(String aName);
	
}
