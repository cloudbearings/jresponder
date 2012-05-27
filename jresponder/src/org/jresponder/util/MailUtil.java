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
package org.jresponder.util;


/**
 * Utilities related to mail and messages
 * @author bradpeabody
 *
 */
public class MailUtil {

//	/**
//	 * Create a new empty message.
//	 * 
//	 * FIXME: The implementation of this is pretty hackish right now - 
//	 * we use Spring to find the current web app context, then find
//	 * the JavaMailSender implementation and use it to create our message.
//	 * Should work in all cases for JResponder, but is ugly as sin.
//	 * @return
//	 */
//	public static MimeMessage createEmptyMimeMessage() {
//		
//		// use some Spring magic to get the current request
//		HttpServletRequest req = 
//				((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//				.getRequest();
//		ServletContext myServletContext = req.getServletContext();
//
//		WebApplicationContext myWebAppContext =
//				WebApplicationContextUtils.getRequiredWebApplicationContext(myServletContext);
//		
//		JavaMailSender myJavaMailSender =
//				myWebAppContext.getBean(JavaMailSender.class);
//		
//		MimeMessage myMimeMessage = myJavaMailSender.createMimeMessage();
//		
////		// I'm not so pumped about having JavaMail and the domain name
////		// in the message ID - I think using UUIDs is just as well.
////		
////		String myMessageId = UUID.randomUUID().toString() + "@" + UUID.randomUUID().toString(); 
////		try {
////			myMimeMessage.setHeader("Message-ID", myMessageId);
////		} catch (MessagingException e) {
////			throw new RuntimeException(e);
////		}
//		
//		return myMimeMessage;
//		
//	}
	
}

