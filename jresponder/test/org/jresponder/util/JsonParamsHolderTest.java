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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jresponder.service.ServiceException;
import org.jresponder.service.ServiceExceptionType;
import org.junit.Test;

/**
 * Unit test for JsonParamsHolder
 * @author bradpeabody
 *
 */
public class JsonParamsHolderTest {

	@Test
	public void testStrings() throws Exception {
		
		new PropUtil().afterPropertiesSet();
		
		JsonParamsHolder h = new JsonParamsHolder("{\"test\":\"blah\"}");
		assertEquals("blah", h.getString("test"));
		
		h = new JsonParamsHolder("{\"test\":\"blah\"}");
		try {
			h.getString("this_doesnt_exist");
			fail("should have thrown exception here");
		}
		catch (ServiceException e) {
			assertEquals(ServiceExceptionType.PARAM_REQUIRED, e.getServiceExceptionType());
		}
		
		
	}

}
