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
package org.jresponder.service;

import java.util.Map;

import javax.annotation.Resource;

import org.jresponder.dao.MainDao;
import org.jresponder.domain.Subscriber;
import org.jresponder.domain.SubscriberStatus;
import org.jresponder.domain.Subscription;
import org.jresponder.domain.SubscriptionStatus;
import org.jresponder.util.PropUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.subethamail.wiser.Wiser;

/**
 * @author bradpeabody
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {
	"../common-logging.xml",
	"../common-database.xml",
	"../common-messages.xml",
	"../common-mail.xml",
	"SubscriberServiceTestContext.xml"
})
public class SubscriberServiceTest {
	
	@Resource
	private PropUtil propUtil;
	
	@Resource
	private MainDao mainDao;
	
	@Resource
	private SubscriberService subscriberService;
	
	@Resource(name="jrWiser")
	private Wiser wiser;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Do the basic sequence of subscribing someone, confirming,
	 * and then unsubscribing and removing.  Make sure that that
	 * works.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimpleSubscribeSequence() throws Exception {
		
		Map<String,Object> m = propUtil.mkprops("first_name", "Joey");
		
		// try subscribe
		subscriberService.subscribe("joey@example.com", m, "list1");
		
		// make sure a message was sent
		Assert.assertEquals(1, wiser.getMessages().size());
		
		wiser.dumpMessages(System.out);
		
		Subscriber mySubscriber = mainDao.getSubscriberByEmail("joey@example.com");
		Subscription mySubscription = mainDao.getSubscriptionBySubscriberAndMessageGroupName(mySubscriber, "list1");
		
		String myToken = mySubscription.getToken();
		
		System.out.println("Token: " + myToken);
		
		Assert.assertEquals(SubscriptionStatus.CONFIRM_WAIT, mySubscription.getSubscriptionStatus());
		
		// try confirm
		subscriberService.confirmFromToken(myToken);
		
		// get the subscription again directly from the db and check the status
		mySubscription = mainDao.getSubscriptionBySubscriberAndMessageGroupName(mySubscriber, "list1");
		Assert.assertEquals(SubscriptionStatus.ACTIVE, mySubscription.getSubscriptionStatus());
		
		// now try to unsubscribe
		subscriberService.unsubscribeFromToken(myToken);
		
		// check database
		mySubscription = mainDao.getSubscriptionBySubscriberAndMessageGroupName(mySubscriber, "list1");
		Assert.assertEquals(SubscriptionStatus.INACTIVE, mySubscription.getSubscriptionStatus());
		
		// now try remove
		subscriberService.removeFromToken(myToken);
		
		// check database
		mySubscriber = mainDao.getSubscriberByEmail("joey@example.com");
		Assert.assertEquals(SubscriberStatus.REMOVED, mySubscriber.getSubscriberStatus());
		
	}

}
