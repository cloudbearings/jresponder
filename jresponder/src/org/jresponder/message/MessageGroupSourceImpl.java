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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

/**
 * Default MessageGroupSource implementation
 * @author bradpeabody
 *
 */
public class MessageGroupSourceImpl implements MessageGroupSource, InitializingBean {

	private List<MessageGroup> messageGroupList = null;
	private File directory = null;
	
	@Override
	public List<MessageGroup> getMessageGroupList() {
		return messageGroupList;
	}


	/**
	 * @return the directory
	 */
	public File getDirectory() {
		return directory;
	}


	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(File directory) {
		this.directory = directory;
	}
	
	/**
	 * Get message group by id, or null if not found
	 */
	@Override
	public MessageGroup getMessageGroupByName(String aName) {
		// null begets null
		if (messageGroupList == null) return null;
		if (aName == null) return null;
		
		// inefficient but simple implementation
		for (MessageGroup myMessageGroup: messageGroupList) {
			if (aName.equals(myMessageGroup.getName())) {
				return myMessageGroup;
			}
		}
		return null;
	}


	/**
	 * Call after directory is set to actually create the message groups
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		
		if (directory == null) throw new IllegalStateException("directory cannot be null - you must call setDirectory() before initializing");
		
		if (!directory.isDirectory()) throw new IllegalStateException("directory provided is not a directory: "+directory.getAbsolutePath());
		
		List<File> myGroupDirList = new ArrayList<File>();
		
		// build list of sub folders
		String[] mySubEntries = directory.list();
		for (String mySubEntry: mySubEntries) {
			File mySubDirFile = new File(directory, mySubEntry);
			// only look at subfolders, not files
			if (mySubDirFile.isDirectory()) {
				myGroupDirList.add(mySubDirFile);
			}
		}
		
		// now create a MessageGroupImpl for each
		messageGroupList = new ArrayList<MessageGroup>();
		
		for (File myGroupDir: myGroupDirList) {
			MessageGroupImpl myMessageGroupImpl = new MessageGroupImpl();
			myMessageGroupImpl.setId(myGroupDir.getName());
			myMessageGroupImpl.setDirectory(myGroupDir);
			// initialize
			myMessageGroupImpl.afterPropertiesSet();
			// add to list
			messageGroupList.add(myMessageGroupImpl);
		}
		
		// done!
	}


}
