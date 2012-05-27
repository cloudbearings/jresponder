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
package org.jresponder.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.jresponder.domain.LogEntry;
import org.jresponder.domain.LogEntryType;
import org.jresponder.domain.Subscriber;
import org.jresponder.domain.Subscription;
import org.jresponder.domain.SubscriptionStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Main DAO.  Deals with all of the DAO stuff.  May split into multiple
 * classes later, but initially just keeping it simple.
 * 
 * @author bradpeabody
 *
 */
@Component("jrMainDao")
//by default all methods require a transaction
@Transactional(propagation=Propagation.REQUIRED)
public class MainDao {

	@PersistenceContext
    private EntityManager em;
	
	
	/* ====================================================================== */
	/* Generic calls - apply to all domain object types                       */
	/* ====================================================================== */

	/**
	 * Calls entityManager.persist() - create/update an object
	 * @param aObject
	 */
	public void persist(Object aObject) {
		em.persist(aObject);
	}
	
	
	/* ====================================================================== */
	/* Subscriber                                                             */
	/* ====================================================================== */

	/**
	 * Get a subscriber, null if not found
	 * @param aId
	 * @return
	 */
	@Transactional(readOnly=true)
	public Subscriber getSubscriberById(Long aId) {
		try {
			return em.createQuery("SELECT o FROM Subscriber o WHERE o.id = :i",
					Subscriber.class)
					.setParameter("i", aId)
					.getSingleResult();
		}
		catch (NoResultException e) { return null; }
	}
	
	/**
	 * Get a subscriber by email, null if not found
	 * @param aEmail
	 * @return
	 */
	@Transactional(readOnly=true)
	public Subscriber getSubscriberByEmail(String aEmail) {
		if (aEmail == null) { return null; }
		aEmail = aEmail.toLowerCase();
		try {
			return em.createQuery("SELECT o FROM Subscriber o WHERE o.email = :e",
					Subscriber.class)
					.setParameter("e", aEmail)
					.getSingleResult();
		}
		catch (NoResultException e) { return null; }
	}
	
	
	/* ====================================================================== */
	/* Subscription                                                           */
	/* ====================================================================== */

	/**
	 * Get a subscription, null if not found
	 * @param aId
	 * @return
	 */
	@Transactional(readOnly=true)
	public Subscription getSubscriptionById(Long aId) {
		try {
			return em.createQuery("SELECT o FROM Subscription o WHERE o.id = :i",
					Subscription.class)
					.setParameter("i", aId)
					.getSingleResult();
		}
		catch (NoResultException e) { return null; }
	}
	
	/**
	 * Get by subscription by subscriber and message group name
	 * @return
	 */
	@Transactional(readOnly=true)
	public Subscription getSubscriptionBySubscriberAndMessageGroupName(Subscriber aSubscriber, String aMessageGroupName) {
		try {
			return em.createQuery("SELECT sion FROM Subscription sion WHERE sion.subscriber.id = :sberid AND messageGroupName = :mgn", Subscription.class)
					.setParameter("sberid", aSubscriber.getId())
					.setParameter("mgn", aMessageGroupName)
					.getSingleResult();
		}
		catch (NoResultException e) { return null; }
	}
	
	/**
	 * Get a subscription by token, null if not found
	 * @return
	 */
	@Transactional(readOnly=true)
	public Subscription getSubscriptionByToken(String aToken) {
		try {
			return em.createQuery("SELECT o FROM Subscription o WHERE o.token = :t",
					Subscription.class)
					.setParameter("t", aToken)
					.getSingleResult();
		}
		catch (NoResultException e) { return null; }
	}

	
	/**
	 * Get the next Subscription record where the next_send_date is greater
	 * than or equal to now and it has one of the statuses provided.  Passing
	 * no statuses will result in that restriction not being applied to the
	 * query (i.e. all statuses are considered). 
	 * 
	 * @param aSubscriptionStatusArray
	 * @return
	 */
	@Transactional(readOnly=true)
	public Subscription getNextSendableSubscription(SubscriptionStatus...aSubscriptionStatusArray) {
	
		String myStatusSuffix = 
				aSubscriptionStatusArray != null && aSubscriptionStatusArray.length > 0 ?
						" AND sion.status IN :statuses " : "";
		
		TypedQuery<Subscription> q = em
				.createQuery("SELECT sion FROM Subscription sion WHERE " +
								"sion.nextSendDate < :d AND " +
								"sion.nextSendDate IS NOT NULL " +
								myStatusSuffix,
								Subscription.class)
				.setParameter("d", new Date())
				.setMaxResults(1);
		
		if (myStatusSuffix.length() > 0) {
			List<String> myStringList = new ArrayList<String>();
			for (SubscriptionStatus s: aSubscriptionStatusArray) {
				myStringList.add(s.toString());
			}
			q.setParameter("statuses", myStringList);
		}
		
		try {
			return q.getSingleResult();
		}
		catch (NoResultException e) { return null; }
		
	}

	
	/* ====================================================================== */
	/* LogEntry                                                               */
	/* ====================================================================== */
	
	/**
	 * Create a log entry
	 * @param aLogEntryType
	 * @param aSubscriber
	 * @param aSubscription
	 * @param aDataMap
	 * @return
	 */
	public LogEntry logEntry
							(
									LogEntryType aLogEntryType,
									Subscriber aSubscriber,
									String aMessageGroupName,
									Map<String,Object> aPropsMap
							) {
		  		
		LogEntry myLogEntry = new LogEntry();
  		myLogEntry.setLogEntryType(aLogEntryType);
  		myLogEntry.setMessageGroupName(aMessageGroupName);
  		myLogEntry.setPropsMap(aPropsMap);
  		myLogEntry.setSubscriber(aSubscriber);
  		
		em.persist(myLogEntry);
		
		return myLogEntry;
		
	}
	
	/**
	 * Like namesake but takes a Subscription object instead of a message
	 * group name.
	 */
	public LogEntry logEntry
							(
									LogEntryType aLogEntryType,
									Subscriber aSubscriber,
									Subscription aSubscription,
									Map<String,Object> aPropsMap
							) {
		
		return logEntry
					(
							aLogEntryType,
							aSubscriber,
							aSubscription != null ? aSubscription.getMessageGroupName() : null,
							aPropsMap
					);
	}

}
