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

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import org.joda.time.DateTime;
import org.jresponder.dao.MainDao;
import org.jresponder.domain.LogEntryType;
import org.jresponder.domain.Subscriber;
import org.jresponder.domain.SubscriberStatus;
import org.jresponder.domain.Subscription;
import org.jresponder.domain.SubscriptionStatus;
import org.jresponder.message.MessageGroup;
import org.jresponder.message.MessageGroupSource;
import org.jresponder.message.MessageRef;
import org.jresponder.util.PropUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gets called on a timer to poll for new messages to send, and if found
 * then sends them.  Has low overhead unless messages need to be sent, at
 * which it comes to life and burns through whatever messages are now
 * ready in order to catch up.
 * 
 * @author bradpeabody
 */
@Component("jrSendingEngine")
public class SendingEngineImpl implements SendingEngine, BeanFactoryAware {
	
	/* ====================================================================== */
	/* Logger boiler plate                                                    */
	/* ====================================================================== */
	private static Logger l = null;
	private Logger logger() { if (l == null) l = LoggerFactory.getLogger(this.getClass()); return l; } 
	/* ====================================================================== */
	
	/**
	 * How many milliseconds do we intend our main loop to take.
	 */
	private static final long TARGET_LOOP_TIME = 15000;
	
	/**
	 * How many milliseconds wait should there be between loops that
	 * occur back to back (i.e. when the loop has work to do the entire
	 * time). This is approximate, and the wait will usually be a bit less
	 * than this.
	 */
	private static final long LOOP_WAIT = 100; 
	

	@Resource(name="jrJavaMailSender")
	private JavaMailSender javaMailSender;
	
//	@Resource(name="jrEntityManagerFactory")
//    private EntityManagerFactory entityManagerFactory;
	
	@Resource(name="jrMessageGroupSource")
	private MessageGroupSource messageGroupSource;
	
	@Resource(name="jrSendConfigGroup")
	private SendConfigGroup sendConfigGroup;
	
	@Resource(name="jrMainDao")
	private MainDao mainDao;
	
	@Resource(name="jrPropUtil")
	private PropUtil propUtil;

	/** the Spring context */
	private BeanFactory beanFactory;
	
	private volatile boolean engineLoopActive = false;
	
	/**
	 * Listen for our context.  We need this as part of the main loop
	 * to be able to do method calls via our AOP proxy and thus allow
	 * the transaction manager to do it's thing.  See the source of
	 * {@link #engineLoop()}.
	 */
	@Override
	public void setBeanFactory(BeanFactory aBeanFactory) {
		beanFactory = aBeanFactory;
	}

	/**
	 * Called to poll the overall state and examine what needs to be sent.
	 * If nothing needs to be sent, this function exits immediately.
	 * NOTE: In testing, it seems like calls meant for this function 
	 * "stack up" if the function doesn't return in time for the next
	 * invocation.  Not sure if this will become an issue or not
	 * NOTE2: Main engine loop is not transactional - instead, each
	 * record processed is done in a separate transaction, to keep
	 * things simple.  May need to rework this so it does it in
	 * batches, but not just yet.  Trying to make it just simple send without
	 * also being a giant pile of sloppy crap.
	 */
	@Scheduled(fixedRate=TARGET_LOOP_TIME+LOOP_WAIT)
	public void engineLoop() {
		
		// don't re-enter
		synchronized(this) {
			if (engineLoopActive) { return; }
			engineLoopActive = true;
		}

		
    	try {
			
			long myStartTime = System.currentTimeMillis();
			logger().debug("engineLoopIteration() - starting at {} in thread {}", myStartTime, Thread.currentThread().getName());
			
			// do a conditional refresh on the message stuff
			messageGroupSource.conditionalRefresh();
			
			boolean myShouldContinue = true;
			
			while (myShouldContinue) {
			
				// Process one inside it's own transaction - 
				// We call via the Spring context so that we invoke
				// engineLoopProcessOne() via the AOP proxy, which is what
				// takes care of the transaction.  Just calling
				// this.engineLoopProcessOne() won't open any transaction.
				myShouldContinue = beanFactory.getBean("jrSendingEngine", SendingEngine.class)
								.engineLoopProcessOne();
				

				// check if time to bail out
				if (myStartTime + TARGET_LOOP_TIME < System.currentTimeMillis()) {
					return;
				}
				
			}
		
		}
    	
    	// just propagate the exception - should not be a normal occurence
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
    	
		finally {
			// make sure we always mark the loop as done
			engineLoopActive = false;
		}

	}
	
	/**
	 * Used internally to process one record inside a transaction.
	 * @return true if the loop calling this method should continue,
	 * or false if we're done for now.
	 */
	// must have it's own transaction
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public boolean engineLoopProcessOne() {
		
		// 1. select next batch size (maybe just 1 for now) where next
		//    consideration date is older than now
		
		Subscription mySubscription = mainDao.getNextSendableSubscription(SubscriptionStatus.ACTIVE); 
		
		// if no result, then bail out, nothing to do
		if (mySubscription == null) {
			logger().debug("No more results, exiting loop");
			return false;
		}
		
		// the subscription we are now processing
		logger().debug("Got subscription with id: {}", mySubscription.getId());
		
		// look up the subscriber too
		Subscriber mySubscriber = mySubscription.getSubscriber();
		
		if (mySubscriber == null) {
			throw new RuntimeException("Data error, subscription with id " + mySubscription.getId() + " has null subscriber, should not be possible");
		}
		
		// Make sure we don't send to people who are asked off
		
		// if status is not okay...
		if (mySubscriber.getSubscriberStatus() != SubscriberStatus.OK) {
			// end this subscription
			endSubscription(mySubscription);
			// log for posterity
			logger().info("Was going to send to subscriber (email={}) but status is not OK, it's '{}', not sending and ending subscription.", mySubscriber.getEmail(), mySubscriber.getSubscriberStatus());
			// bail out
			return true;
		}
		
		// 2. for this Subscription, find last message processed in
		//    the list for that group
		
		// first make sure we have a corresponding message group
		MessageGroup myMessageGroup = messageGroupSource.getMessageGroupByName(mySubscription.getMessageGroupName());
		
		// if no such message group, then we just get rid of the
		// subscription (lose the date so it doesn't get pulled back up);
		// also catch the case where no subscriber record, should never
		// happen, but adding scenario for robustness)
		if (myMessageGroup == null || mySubscriber == null) {
			
			logger().warn(
					"Either message group or subscriber was null, " +
					"bailing out (messageGroup: {}, subscriber: {})",
					myMessageGroup, mySubscriber);
			
			/* ====================================================== */
			/* clear next send date of subscription                   */
			/* ====================================================== */
			endSubscription(mySubscription);
			return true;
		}
		
		// get the name of the last message
		String myLastMessageName = mySubscription.getLastMessageName();
		logger().debug("myLastMessageName: {}", myLastMessageName);
		
		// if last message exists
		int mySendMessageRefIndex = myMessageGroup.indexOfName(myLastMessageName);
		if (mySendMessageRefIndex >= 0) {
			// increment to next one to get the message we should send
			mySendMessageRefIndex++;
		}
		// if last message name was null, then start at zero
		if (myLastMessageName == null) {
			mySendMessageRefIndex = 0;
		}
		
		logger().debug("myLastMessageRefIndex: {}", mySendMessageRefIndex);
		
		
		// 3. get the next message
		
		List<MessageRef> myMessageRefList = myMessageGroup.getMessageRefList();
		
		// the MessageRef that we are to send
		MessageRef mySendMessageRef = null;
		if (mySendMessageRefIndex >= 0) {
			// make sure we haven't gone beyond the end of the messages
			if (myMessageRefList.size() > mySendMessageRefIndex) {
				mySendMessageRef = myMessageRefList.get(mySendMessageRefIndex);
			}
		}
		logger().debug("about to send mySendMessageRef: {}", mySendMessageRef);

		
		// no message to send (either couldn't find last message or
		// there is no next message); also check for the case where
		// the last send date is not set (should never happen, but just
		// to keep it from breaking the system we treat it like end
		// of list)
		if (mySendMessageRef == null || mySubscription.getLastSendDate() == null) {
			logger().debug("no more messages to send for this subscription");
			/* ====================================================== */
			/* clear next send date of subscription                   */
			/* ====================================================== */
			endSubscription(mySubscription);
			return true;
		}
		

		if (mySubscription.getLastSendDate() == null) {
			logger().warn("last_send_date was null on subscription, should never happen");
		}
		
		// 3a.read the time on it and recalculate, confirm that
		//    time is now past
		
		Long myWait = mySendMessageRef.getWaitAfterLastMessage();
		if (myWait == null) {
			/* ====================================================== */
			/* clear next send date of subscription                   */
			/* ====================================================== */
			logger().warn("'wait after last message' value was not set for this message, ending the subscription");
			endSubscription(mySubscription);
			return true;
		}
		
		// 3b.if not past then set next consideration date to recalced date
		//    and kick back
		
		// see if it's time to send yet
		long myLastSendDate = mySubscription.getLastSendDate().getTime();
		if (myLastSendDate + myWait > new Date().getTime()) {
			logger().debug("it is not yet time to send this message, putting it back for later");
			// not time to send (possibly message changed since
			// this subscription record was last edited - also first
			// subscription looks like this);
			// reset the nextSendDate and move on
			mySubscription.setNextSendDate(new Date(myLastSendDate + myWait));
			mainDao.persist(mySubscription);
			return true;
		}
		
		
		// 4. render and send message
		
		boolean mySendWorked = sendMessage(
						mySendMessageRef,
						myMessageGroup,
						mySubscriber,
						mySubscription,
						LogEntryType.MESSAGE_SENT
				);
		
		// if the sending was skipped...
		if (!mySendWorked) {
			
			// write a log entry for the skipping
			mainDao.logEntry
						(
								 LogEntryType.MESSAGE_SKIPPED,
								 mySubscriber,
								 myMessageGroup.getName(),
								 propUtil.mkprops(
										 "message_name", mySendMessageRef.getName()
										 )
						);

		}
		
		
		
		
		// 5. get next message in the list and recalculate date based on that,
		//    update last message name and save
		
		// update the last sent info
		mySubscription.setLastMessageName(mySendMessageRef.getName());
		mySubscription.setLastSendDate(new Date());
		
		// index of next message
		int myNextMessageRefIndex = mySendMessageRefIndex + 1;
		
		
		// 5a.if no more messages, then next consideration date is set to null
		
		if (myNextMessageRefIndex >= myMessageRefList.size()) {
			mySubscription.setNextSendDate(null);
		}
		
		// if we have a next message, then calc when it fires
		else {
			
			MessageRef myNextMessageRef = myMessageRefList.get(myNextMessageRefIndex);
			Long myNextWait = myNextMessageRef.getWaitAfterLastMessage();
			mySubscription.setNextSendDate(
						new Date(mySubscription.getLastSendDate().getTime() + myNextWait)
					);

		}
		
		// save the changes to the subscription
		mainDao.persist(mySubscription);
		
		if (mySubscription.getNextSendDate() == null) {
			
			// write a log entry for the completion of the subscription
			mainDao.logEntry
					(
							 LogEntryType.SUBSCRIPTION_DONE,
							 mySubscriber,
							 myMessageGroup.getName(),
							 propUtil.mkprops(
									 "message_name", mySendMessageRef.getName()
									 )
					);
		}

		
		// as far as we know, there should be more to process...
		return true;
		
	}
	
	/**
	 * Ends a subscription by setting the nextSendDate to null
	 * 
	 * @param aEntityManager
	 * @param aSubscription
	 */
	protected void endSubscription(Subscription aSubscription) {
		aSubscription.setNextSendDate(null);
		aSubscription.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
		mainDao.persist(aSubscription);
	}
	
	/**
	 * Delays a subscription by setting the nextSendDate to a day from now
	 * 
	 * @param aEntityManager
	 * @param aSubscription
	 */
	protected void delaySubscription(Subscription aSubscription) {
		aSubscription.setNextSendDate(DateTime.now().plusMinutes(10).toDate());
		mainDao.persist(aSubscription);
	}
	
	/**
	 * Perform an individual message send.
	 * 
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public boolean sendMessage
						(
								MessageRef aSendMessageRef,
								MessageGroup aMessageGroup,
								Subscriber aSubscriber,
								Subscription aSubscription,
								LogEntryType aLogEntryType
						) {
		
		logger().debug("Rendering message...");
		long t1 = System.currentTimeMillis();
		MimeMessage myMimeMessage = javaMailSender.createMimeMessage();
		
		// look up the send config
		SendConfig mySendConfig = sendConfigGroup.lookupSendConfig
				(
						aSubscriber,
						aSubscription,
						aMessageGroup,
						aSendMessageRef
				);
		
		if (mySendConfig == null) {
			throw new IllegalStateException("No SendConfig, cannot send mail like this!  Please define at least a default...");
		}
		
		// populate message
		if (aSendMessageRef.populateMessage(myMimeMessage, mySendConfig, aSubscriber, aSubscription)) {
		
			// if population worked, then send
			logger().debug("Message rendered, that took {} milliseconds", System.currentTimeMillis() - t1);
			
			// do the message sending
			// note - if this errors, fine, let it error.  it should spew
			// out in the logs and the admin can fix it - we haven't marked
			// message as sent yet...
			logger().debug("Transmitting message (using JavaMailSender)...");
			long t2 = System.currentTimeMillis();
			javaMailSender.send(myMimeMessage);
			logger().debug("Message transmitted, that took {} milliseconds", System.currentTimeMillis() - t2);

			
			// write a log entry for the sending
			mainDao.logEntry
						(
								 aLogEntryType,
								 aSubscriber,
								 aMessageGroup.getName(),
								 propUtil.mkprops(
										 "message_name", aSendMessageRef.getName()
										 )
						);
			
			return true;
			
		}
		
		// message told us to skip
		else {
			
			logger().debug("Message rendered (took {} milliseconds), but result is to skip sending the message (html and text body both empty); skipping", System.currentTimeMillis() - t1);
			
			return false;
		}

		
	}
	
}
