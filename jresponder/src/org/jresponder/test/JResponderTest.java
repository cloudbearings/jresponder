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
package org.jresponder.test;

import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;

/**
 * This is just temp - need to get some real unit/regression testing
 * happening...
 * 
 * @author bradpeabody
 *
 */
public class JResponderTest {

	public JResponderTest() {
		//System.out.println("TESTING");
	}
	
	public static void main(String[] args) throws Exception {
		
		//System.out.println(new Duration("PT1D").getMillis());
		
		PeriodFormatter myPeriodFormatter = ISOPeriodFormat.standard();
		System.out.println(myPeriodFormatter.parsePeriod("P1DT0M").toStandardDuration().getMillis());
		
	}
	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) throws Exception {
//		
//		System.out.println("JResponderTest");
//		
//		EntityManagerFactory entityManagerFactory =
//				Persistence.createEntityManagerFactory( "org.jresponder.PU" );
//		
//		System.out.println(entityManagerFactory);
//		
//		EntityManager myEntityManager = entityManagerFactory.createEntityManager();
//		
////		myEntityManager.getTransaction().begin();
//////		myEntityManager.persist(new Sub("test1@example.com"));
//////		myEntityManager.persist(new Sub("test2@example.com"));
////		myEntityManager.getTransaction().commit();
//		
//		myEntityManager.getTransaction().begin();
//		
////		CriteriaBuilder cb = myEntityManager.getCriteriaBuilder();
////		CriteriaQuery<Sub> q = cb.createQuery(Sub.class);
////		Root<Sub> s = q.from(Sub.class);
////		q.select(s);
////		
////		TypedQuery<Sub> tq = myEntityManager.createQuery(q);
////		Sub mySub = tq.getSingleResult();
//		
//		
//		TypedQuery<Subscriber> query = myEntityManager.createQuery("SELECT s FROM Sub s WHERE s.email = :m", Subscriber.class)
//									.setParameter("m", "test2@example.com");
//		Subscriber mySub = query.getSingleResult();
//		
//		System.out.println("ID: " + mySub.getId());
//		
//		myEntityManager.getTransaction().commit();
//		
//		myEntityManager.close();
//		
////		CriteriaQuery<Sub> myCriteriaQuery;
////		TypedQuery<Sub> myQuery = myEntityManager.createQuery(myCriteriaQuery);
////		
////		myEntityManager.close();
//		
//		entityManagerFactory.close();
//
//	}

}
