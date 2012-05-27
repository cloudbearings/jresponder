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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

/**
 * Default message group implementation.  Normally gets created via
 * Spring config.
 * 
 * @author bradpeabody
 *
 */
public class MessageGroupImpl implements MessageGroup, InitializingBean, BeanNameAware {

	/* ====================================================================== */
	/* Logger boiler plate                                                    */
	/* ====================================================================== */
	private static Logger l = null;
	private Logger logger() { if (l == null) l = LoggerFactory.getLogger(this.getClass()); return l; } 
	/* ====================================================================== */
	
	private String name;
	private String description;
	private List<MessageRef> messageRefList;
	private File directory;
	private MessageRef optInConfirmMessageRef;
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setId(String v) {
		name = v;
	}
	
	@Override
	public void setBeanName(String aBeanName) {
		name = aBeanName;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String v) {
		description = v;
	}

	@Override
	public List<MessageRef> getMessageRefList() {
		return messageRefList;
	}
	
	public void setMessageRefs(List<MessageRef> v) {
		messageRefList = v;
	}
	
	@Override
	public int indexOfName(String aName) {
		if (aName == null) { return -1; }
		int i = 0;
		for (MessageRef myMessageRef: messageRefList) {
			if (aName.equals(myMessageRef.getName())) {
				return i;
			}
			i++;
		}
		return -1;
	}

	@Override
	public MessageRef getMessageRefByName(String aId) {
		if (aId == null) return null;
		for (MessageRef myMessageRef: messageRefList) {
			if (aId.equals(myMessageRef.getName())) {
				return myMessageRef;
			}
		}
		return null;
	}
	
	@Override
	public MessageRef getOptInConfirmMessageRef() {
		return optInConfirmMessageRef;
	}

	
	public File getDirectory() {
		return directory;
	}

	/**
	 * The directory to read message refs from
	 * @param directory
	 */
	public void setDirectory(File directory) {
		this.directory = directory;
	}
	

	/**
	 * Call after set properties, performs the actual
	 * loading from the directory.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		
		refresh();
		
	}

	@Override
	public void refresh() {
		
		if (directory == null) {
			throw new IllegalStateException("directory cannot be null");
		}
		
		logger().debug("MessageGroupImpl refreshing with directory: "+directory.getAbsolutePath());

		File[] myFiles = directory.listFiles();
		
		messageRefList = new ArrayList<MessageRef>();
		
		for (File myFile: myFiles) {
			if (myFile.isFile()) {
				if (myFile.getName().endsWith(".html")) {
					try {
						MessageRefImpl myMessageRefImpl;
						myMessageRefImpl = new MessageRefImpl(myFile);
						
						// call this so that it errors now instead of later - 
						// this way the sending engine doesn't have to have
						// complicated logic to deal with a badly formatted
						//  message wait value
						myMessageRefImpl.getWaitAfterLastMessage();
						
						// the "opt-in-confirm" message is treated differently
						if (myMessageRefImpl.getName().equals("opt-in-confirm")) {
							optInConfirmMessageRef = myMessageRefImpl;
						}
						else {
							messageRefList.add(myMessageRefImpl);
						}
						
					} catch (Throwable e) {
						// if an error occurs parsing a message, don't
						// assume all is lost, log error and continue
						logger().error(e.getMessage(), e);
					} 
				}
			}
		}
		
		// done!
		
	}

	@Override
	public void conditionalRefresh() {
		// TODO Auto-generated method stub
		// should scan list of files, if names and/or count is different,
		// then reload everything, if the same, then call conditionalRefresh()
		// on all of our MessageRefs
		throw new UnsupportedOperationException("Not implemented yet");
	}


}
