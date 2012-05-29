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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.jresponder.dao.MainDao;
import org.jresponder.domain.LogEntryType;
import org.jresponder.domain.Subscriber;
import org.jresponder.domain.SubscriberStatus;
import org.jresponder.domain.Subscription;
import org.jresponder.domain.SubscriptionStatus;
import org.jresponder.engine.SendingEngine;
import org.jresponder.message.MessageGroup;
import org.jresponder.message.MessageGroupSource;
import org.jresponder.message.MessageRef;
import org.jresponder.util.PropUtil;
import org.jresponder.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Perform actions related to subscribing.
 * 
 * @author bradpeabody
 *
 */
@Service("jrSubscriberService")
public class SubscriberService {
	
	/* ====================================================================== */
	/* Logger boiler plate                                                    */
	/* ====================================================================== */
	private static Logger l = null;
	private Logger logger() { if (l == null) l = LoggerFactory.getLogger(this.getClass()); return l; } 
	/* ====================================================================== */

	@Resource(name="jrTokenUtil")
	private TokenUtil tokenUtil;
	
	@Resource(name="jrMainDao")
	private MainDao mainDao;
	
	@Resource(name="jrMessageGroupSource")
	private MessageGroupSource messageGroupSource;
	
	@Resource(name="jrSendingEngine")
	private SendingEngine sendingEngine;
	
	
	/**
	 * Default log entry props
	 * @return
	 */
	private static Map<String,Object> defaultLogEntryProps() {
		
		// use some Spring magic to get the current request
		HttpServletRequest req = 
				((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		
		Map<String,Object> myRet = new HashMap<String,Object>();
		myRet.put("ip_address", req.getRemoteAddr());
		return myRet;
	}
	
	/**
	 * Subscribes someone, returns the Subscriber object that corresponds
	 * 
	 * @param aEmail
	 * @param aSubscriberPropsMap
	 * @param aMessageGroupName
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public Subscriber subscribe(String aEmail, Map<String,Object> aSubscriberPropsMap, String aMessageGroupName) {
    	
		logger().debug("Starting subscribe()");
		
    	try {
	    	// FIXME: should package these up into util calls so they are not so verbose,
	    	// do that before too many more of these functions are written
	    	
	    	// sanity checks
	    	if (aEmail == null || aEmail.length() < 1) throw new IllegalArgumentException("email cannot be null or zero length");
	    	// TODO: configurable?
	    	aEmail = aEmail.toLowerCase();
	    	
	    	// make sure that the message group is not empty and actually exists
	    	if (aMessageGroupName == null || aMessageGroupName.length() < 1) throw new IllegalArgumentException("messageGroupName cannot be null or zero length");
	    	if (messageGroupSource.getMessageGroupByName(aMessageGroupName) == null) {
	    		throw new IllegalArgumentException("message group does not exist");
	    	}
	    	
	    	// get the message group
	    	MessageGroup myMessageGroup = messageGroupSource.getMessageGroupByName(aMessageGroupName);
			logger().debug("Using MessageGroup: {}", myMessageGroup);
			
	    	// the opt-in-confirm message, if it exists
	    	MessageRef myOptInConfirmMessageRef = myMessageGroup.getOptInConfirmMessageRef();
			logger().debug("myOptInConfirmMessageRef: {}", myOptInConfirmMessageRef);
	    	
	    	// make sure we have an attribute map, even if it's empty
	    	if (aSubscriberPropsMap == null) {
	    		aSubscriberPropsMap = new HashMap<String,Object>();
	    	}
	    	
			logger().debug("aSubscriberPropsMap: {}", aSubscriberPropsMap);

	    	
    		// find existing subscriber
    		Subscriber mySubscriber = mainDao.getSubscriberByEmail(aEmail);
			logger().debug("Looked up subscriber with email ({}) and got: {}", aEmail, mySubscriber);
    		
			Subscription mySubscription = null;
			
    		// doesn't exist, create it
    		if (mySubscriber == null) {
    			
    			logger().debug("No subscriber for this email, making a new record");
    			
    			// create subscriber
    			mySubscriber = new Subscriber();
    			mySubscriber.setEmail(aEmail);
    			mySubscriber.setPropsMap(aSubscriberPropsMap);
    			mySubscriber.setSubscriberStatus(SubscriberStatus.OK);
    			mainDao.persist(mySubscriber);
    			
    			logger().debug("Now making a new subscription record");

        		// create subscription
        		mySubscription = new Subscription(mySubscriber, aMessageGroupName);
        		mySubscription.setNextSendDate(new Date());
        		mySubscription.setToken(tokenUtil.generateToken());
        		
        		// send opt in confirm message if applicable
        		if (myOptInConfirmMessageRef != null) {
        			logger().debug("About to send opt-in confirm message...");
        			doOptInConfirmMessage(myOptInConfirmMessageRef, myMessageGroup, mySubscriber, mySubscription);
        		}
        		// if no opt in, then just mark active
        		else {
        			logger().debug("No opt-in confirm message, just marking as active");
        			mySubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        		}
        		
    			logger().debug("Saving subscription");
        		mainDao.persist(mySubscription);
        		
				/* ========================================================== */
				/* Make LogEntry                                              */
				/* ========================================================== */
        		mainDao.logEntry
        							(
        									LogEntryType.SUBSCRIBED,
        									mySubscriber,
        									aMessageGroupName,
        									defaultLogEntryProps()
        							);
        		
    		}
    		
    		// already there, merge
    		else {
    			
    			logger().debug("Already have a Subscriber object for email address ({}): {}", aEmail, mySubscriber);
    			
    			// update attributes
    			mySubscriber.setPropsMap(
    					(Map<String,Object>)PropUtil.getInstance().propMerge(mySubscriber.getPropsMap(), aSubscriberPropsMap)
    					);
    			
    			if (logger().isDebugEnabled()) {
    				logger().debug("Saving updated properties: {}", mySubscriber.getPropsMap());
    			}
    			
        		mainDao.persist(mySubscriber);
    			
        		// see if the subscription is there
        		mySubscription =
        				mainDao.getSubscriptionBySubscriberAndMessageGroupName
        					(
        							mySubscriber,
        							myMessageGroup.getName()
        					);
        		
				logger().debug("Looking for corresponding Subscription record for subscriber and message group ({}) found: {}", myMessageGroup.getName(), mySubscription);
        		
        		// no subscription, create it
        		if (mySubscription == null) {
        			
        			mySubscription = new Subscription(mySubscriber, aMessageGroupName);
            		mySubscription.setNextSendDate(new Date());
            		mySubscription.setToken(tokenUtil.generateToken());

            		// send opt in confirm message if applicable
            		if (myOptInConfirmMessageRef != null) {
            			logger().debug("About to send opt-in confirm message...");
            			doOptInConfirmMessage(myOptInConfirmMessageRef, myMessageGroup, mySubscriber, mySubscription);
            		}
            		// if no opt in, then just mark active
            		else {
            			logger().debug("No opt-in confirm message, just marking as active");
            			mySubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
            		}

        			logger().debug("Saving subscription");
            		mainDao.persist(mySubscription);
            		
					/* ========================================================== */
					/* Make LogEntry                                              */
					/* ========================================================== */
	        		mainDao.logEntry
	        							(
	        									LogEntryType.SUBSCRIBED,
	        									mySubscriber,
	        									aMessageGroupName,
	        									defaultLogEntryProps()
	        							);
        		}
        		
        		// we do already have a subscription
        		else {
        			
        			// see if it's active
        			if (mySubscription.getSubscriptionStatus() != SubscriptionStatus.ACTIVE) {
        				
                		// send opt in confirm message if applicable
                		if (myOptInConfirmMessageRef != null) {
                			doOptInConfirmMessage(myOptInConfirmMessageRef, myMessageGroup, mySubscriber, mySubscription);
                		}
                		// if no opt in, then just mark active
                		else {
                			mySubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
                		}
                		
        			}
        			
        			// save subscription
        			mainDao.persist(mySubscription);
        			

					/* ========================================================== */
					/* Make LogEntry                                              */
					/* ========================================================== */
	        		mainDao.logEntry
	        							(
	        									LogEntryType.RESUBSCRIBED,
	        									mySubscriber,
	        									aMessageGroupName,
	        									defaultLogEntryProps()
	        							);
        		}
        		
    		}
    		
    		// now that we've done all the work -
    		// re-read subscriber, so we get the latest (probably not
    		// necessary, but it makes it me feel better ;)
    		mySubscriber = mainDao.getSubscriberById(mySubscriber.getId());
    		
    		// log an info, just for politeness
			logger().info("User '{}' subscribed to message group '{}' ({} opt-in confirm)",
							new Object[] { mySubscriber.getEmail(), mySubscription.getMessageGroupName(),
									(myOptInConfirmMessageRef != null ? "with" : "without")});

    		return mySubscriber;
    		
    	}
    	catch (Throwable t) {
    		throw new RuntimeException(t);
    	}
		
	}
	
	/**
	 * Unsubscribe from token.  Sets this individual Subscription as inactive.
	 * 
	 * @param aToken
	 * @return true if found and marked, false if not found 
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public boolean unsubscribeFromToken(String aToken) {
		
		Subscription mySubscription = mainDao.getSubscriptionByToken(aToken);
		if (mySubscription == null) { return false; }
		
		mySubscription.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
		mySubscription.setNextSendDate(null);
		
		mainDao.persist(mySubscription);
		
		Subscriber mySubscriber = mySubscription.getSubscriber();
		if (mySubscriber == null) {
			throw new IllegalStateException("Subscription record found but no corresponding subscriber record - this is an internal database error");
		}
		
		logger().info("Subscription corresponding to (email={} messageGroupName={}) was marked as INACTIVE (this particular email/message group combination is now not active)", mySubscriber.getEmail(), mySubscription.getMessageGroupName());

		return true;
	}
	
	
	/**
	 * Sets the subscriber status to REMOVE, meaning he won't
	 * get any more mail at all from this system.
	 * 
	 * @param aToken
	 * @return true if found and marked, false if not found 
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public boolean removeFromToken(String aToken) {
		
		Subscription mySubscription = mainDao.getSubscriptionByToken(aToken);
		if (mySubscription == null) { return false; }
		
		Subscriber mySubscriber = mySubscription.getSubscriber();
		if (mySubscriber == null) {
			throw new IllegalStateException("Subscription record found but no corresponding subscriber record - this is an internal database error");
		}
		
		mySubscriber.setSubscriberStatus(SubscriberStatus.REMOVED);
		mainDao.persist(mySubscriber);
		
		logger().info("Subscriber with email ({}) was REMOVED (no more emails from an message groups)", mySubscriber.getEmail());
		
		return true;

	}
	
	/**
	 * Send an opt-in confirmation message
	 * 
	 * @param aOptInConfirmMessageRef
	 * @param aSubscriber
	 * @param aSubscription
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	protected void doOptInConfirmMessage
						(
								MessageRef aOptInConfirmMessageRef,
								MessageGroup aMessageGroup,
								Subscriber aSubscriber,
								Subscription aSubscription
						) {

		// send message
		if (sendingEngine.sendMessage(aOptInConfirmMessageRef, aMessageGroup, aSubscriber, aSubscription, LogEntryType.MESSAGE_SENT)) {
			logger().debug("Sent opt-in confirm message");
			aSubscription.setSubscriptionStatus(SubscriptionStatus.CONFIRM_WAIT);
		}
		
		// if opt-in message skipped then we mark as active right away
		else {
			logger().debug("Opt-in confirm message was skipped (due to no body)");
			aSubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
		}
		
	}

	/**
	 * Gets a subscriber and all of his Subscriptions
	 * 
	 * @param aEmail
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public Subscriber lookupSubscriber(String aEmail) {
    	
    	try {
    		
    		if (aEmail == null) { return null; }
    		
	    	aEmail = aEmail.toLowerCase();

    		// find existing subscriber
    		Subscriber mySubscriber = mainDao.getSubscriberByEmail(aEmail);
    		
    		// force Hibernate to populate the Subscriptions list
    		if (mySubscriber != null) {
    			mySubscriber.getSubscriptions();
    		}
    		
    		return mySubscriber;
    		
    	}
    	catch (Throwable t) {
    		throw new RuntimeException(t);
    	}
		
	}

	
}
