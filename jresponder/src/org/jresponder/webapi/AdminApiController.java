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
package org.jresponder.webapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.jresponder.domain.Subscriber;
import org.jresponder.domain.Subscription;
import org.jresponder.engine.SendConfig;
import org.jresponder.engine.SendConfigGroup;
import org.jresponder.message.MessageGroup;
import org.jresponder.message.MessageGroupSource;
import org.jresponder.message.MessageRef;
import org.jresponder.service.SubscriberService;
import org.jresponder.util.WebApiUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for the admin web API.
 * 
 * @author bradpeabody
 */
@Controller
@RequestMapping("/admin/api")
public class AdminApiController {
	
	@Resource(name="jrSubscriberService")
	private SubscriberService subscriberService;
	
	@Resource(name="jrMessageGroupSource")
	private MessageGroupSource messageGroupSource;
	
	@Resource(name="jrJavaMailSender")
	private JavaMailSender javaMailSender;
	
	@Resource(name="jrSendConfigGroup")
	private SendConfigGroup sendConfigGroup;

	/**
	 * REST-style call to preview a message in the context of a particular
	 * subscriber and subscription (specified by message group name).
	 * <p>
	 * Example request: /admin/api/message-preview.action?email=test@example.com&message_group_name=list1&message_name=example1
	 * 
	 * @param aId
	 * @param aParams
	 * @return
	 * @throws MessagingException 
	 * @throws IOException 
	 */
	@RequestMapping("message-preview")
    public ResponseEntity<String> 	subscribe
    								(
    									@RequestParam("email") String aEmail,
    									@RequestParam("message_group_name") String aMessageGroupName,
    									@RequestParam("message_name") String aMessageName
    								) throws Exception {

    	Subscriber mySubscriber = subscriberService.lookupSubscriber(aEmail);
    	
    	if (mySubscriber == null) return WebApiUtil.result404("Couldn't find subscriber");
    	
    	Subscription mySubscription = null;
    	for (Subscription myTempSubscription: mySubscriber.getSubscriptions()) {
    		if (myTempSubscription.getMessageGroupName().equals(aMessageGroupName)) {
    			mySubscription = myTempSubscription;
    		}
    	}
    	
    	if (mySubscription == null) return WebApiUtil.result404("Couldn't find subscription");
    	
    	MessageGroup myMessageGroup = messageGroupSource.getMessageGroupByName(aMessageGroupName);
    	
    	if (myMessageGroup == null) return WebApiUtil.result404("Couldn't find message group");
    	
    	MessageRef myMessageRef = myMessageGroup.getMessageRefByName(aMessageName);
    	
    	MimeMessage myMimeMessage = javaMailSender.createMimeMessage();
    	
    	SendConfig mySendConfig = 
    			sendConfigGroup.lookupSendConfig(mySubscriber, mySubscription, myMessageGroup, myMessageRef);
    	
    	if (mySendConfig == null) {
    		throw new IllegalStateException("No SendConfig for this message, cannot continue!");
    	}
    	
    	myMessageRef.populateMessage(myMimeMessage, mySendConfig, mySubscriber, mySubscription);
    	
    	ByteArrayOutputStream myByteArrayOutputStream =  new ByteArrayOutputStream();
    	myMimeMessage.writeTo(myByteArrayOutputStream);
    	
    	return WebApiUtil.plainTextResult(myByteArrayOutputStream.toString("UTF-8"));
    	
    }

	
}
