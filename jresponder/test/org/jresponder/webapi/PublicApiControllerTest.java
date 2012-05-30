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

import javax.annotation.Resource;

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
	"PublicApiControllerTestContext.xml"
})
public class PublicApiControllerTest {

	//@Resource
	//private MainDao mainDao;
	
	@Resource
	private PublicApiController publicApiController;
	
	@Resource(name="jrWiser")
	private Wiser wiser;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * Test method for {@link org.jresponder.webapi.PublicApiController#subscribe(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testSubscribe() throws Exception {
		
		System.out.println(
			publicApiController.subscribe("1", "{}", null).getBody()
		);
		
		wiser.dumpMessages(System.out);
		
	}

//	/**
//	 * Test method for {@link org.jresponder.webapi.PublicApiController#unsubscribe(java.lang.String, java.lang.String, java.lang.String)}.
//	 */
//	@Test
//	public void testUnsubscribe() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link org.jresponder.webapi.PublicApiController#remove(java.lang.String, java.lang.String, java.lang.String)}.
//	 */
//	@Test
//	public void testRemove() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link org.jresponder.webapi.PublicApiController#confirm(java.lang.String, java.lang.String, java.lang.String)}.
//	 */
//	@Test
//	public void testConfirm() {
//		fail("Not yet implemented");
//	}

}
