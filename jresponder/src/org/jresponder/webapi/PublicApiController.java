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

import java.util.Map;

import javax.annotation.Resource;

import org.jresponder.domain.Subscriber;
import org.jresponder.service.ServiceException;
import org.jresponder.service.SubscriberService;
import org.jresponder.util.JsonParamsHolder;
import org.jresponder.util.PropUtil;
import org.jresponder.util.WebApiUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * Handles requests for the public API
 * 
 * @author bradpeabody
 */
@Controller
@RequestMapping("/public/api")
public class PublicApiController {
	
	@Resource(name="jrSubscriberService")
	private SubscriberService subscriberService;
	
	@Resource(name="jrWebApiUtil")
	private WebApiUtil webApiUtil;

	/**
	 * JSON-RPC call to subscribe someone
	 * <p>
	 * Example request: /public/api.action?method=subscribe&jsonrpc=2.0&id=1&params={"email":"test@example.com","props":{"first_name":"Joe"},"message_group_name":"list1"}
	 * 
	 * @param aId
	 * @param aParams
	 * @return
	 */
	@RequestMapping(params={"method=subscribe","jsonrpc=2.0"})
    public ResponseEntity<String> 	subscribe
    								(
    									@RequestParam("id") String aId,
    									@RequestParam("params") String aParams,
    									@RequestParam(value="callback", required=false) String aCallback
    								) {
    	
    	try {
    		
    		JsonParamsHolder p = new JsonParamsHolder(aParams);

	    	String myEmail = p.getString("email");
	    	Map<String,Object> myAttributesMap = p.getProps("props", null);
	    	String myMessageGroupName = p.getString("message_group_name");
	    	
	    	Subscriber mySubscriber =
	    			subscriberService.subscribe(myEmail, myAttributesMap, myMessageGroupName);
	    	
	    	return webApiUtil.jsonRpcResult(aId, aCallback, PropUtil.getInstance().mkprops("jr_subscriber_id", mySubscriber.getId()));
    	}
    	catch (ServiceException e) {
    		return webApiUtil.jsonRpcError(aId, aCallback, e);
    	}

    }

    /**
     * Unsubscribe someone - marks their subscripton as invalid, but they
     * can still subscribe back again if they want.
     */
	@RequestMapping(params={"method=unsubscribe","jsonrpc=2.0"})
    public ResponseEntity<String> 	unsubscribe
    								(
    									@RequestParam("id") String aId,
    									@RequestParam("params") String aParams,
    									@RequestParam(value="callback", required=false) String aCallback
    								) {
    	
    	try {
    		
    		JsonParamsHolder p = new JsonParamsHolder(aParams);
    	
	    	String myToken = p.getString("token");
    	
	    	subscriberService.unsubscribeFromToken(myToken);
	    	
	    	return webApiUtil.jsonRpcResult(aId, aCallback, PropUtil.getInstance().mkprops("success", true));
    	}
    	catch (ServiceException e) {
    		return webApiUtil.jsonRpcError(aId, aCallback, e);
    	}

	}
	
    /**
     * Remove someone - marks their subscriber record as invalid - means
     * we won't send them any more messages.  (i.e. SendingEngine
     * refuses to send messages to someone who has a status of anything
     * other than "OK")
     */
	@RequestMapping(params={"method=remove","jsonrpc=2.0"})
    public ResponseEntity<String> 	remove
    								(
    									@RequestParam("id") String aId,
    									@RequestParam("params") String aParams,
    									@RequestParam(value="callback", required=false) String aCallback
    								) {
    	try {
    		
    		JsonParamsHolder p = new JsonParamsHolder(aParams);
    	
	    	String myToken = p.getString("token");
	    	
	    	subscriberService.removeFromToken(myToken);
	    	
	    	return webApiUtil.jsonRpcResult(aId, aCallback, PropUtil.getInstance().mkprops("success", true));
    	}
    	catch (ServiceException e) {
    		return webApiUtil.jsonRpcError(aId, aCallback, e);
    	}

	}
	
    /**
     * Confirm someone (as in opt-in-confirm).
     */
	@RequestMapping(params={"method=confirm","jsonrpc=2.0"})
    public ResponseEntity<String> 	confirm
    								(
    									@RequestParam("id") String aId,
    									@RequestParam("params") String aParams,
    									@RequestParam(value="callback", required=false) String aCallback
    								) {
    	
    	try {
    		
    		JsonParamsHolder p = new JsonParamsHolder(aParams);
    	
    		String myToken = p.getString("token");
    		
    		subscriberService.confirmFromToken(myToken);
    	
    		return webApiUtil.jsonRpcResult(aId, aCallback, PropUtil.getInstance().mkprops("success", true));
    	}
    	catch (ServiceException e) {
    		return webApiUtil.jsonRpcError(aId, aCallback, e);
    	}
    	
	}
	
}
