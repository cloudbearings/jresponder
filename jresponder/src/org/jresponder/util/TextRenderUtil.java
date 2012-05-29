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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Utility stuff to render text via Velocity
 * 
 * @author bradpeabody
 */
@Component("jrTextRenderUtil")
public class TextRenderUtil implements InitializingBean {

	/* ====================================================================== */
	/* singleton support with override - boiler plate (see package desc)      */
	private static TextRenderUtil instance;
	public static TextRenderUtil getInstance() { return instance; }
	public static void setInstance(TextRenderUtil inst) { instance = inst; }
	public void afterPropertiesSet() { setInstance(this); }
	/* ====================================================================== */
	
	private volatile VelocityEngine velocityEngine = null;
	
	/**
	 * Render some text through Velocity
	 * 
	 * @param aInput
	 * @param aContextProps
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String render(String aInput, Map aContextProps) {
		
		// set up Velocity on first use
		if (velocityEngine == null) {
			synchronized(TextRenderUtil.class) {
				if (velocityEngine == null) {
					velocityEngine = new VelocityEngine();
					velocityEngine.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
						      "org.apache.velocity.runtime.log.Log4JLogChute" );
					velocityEngine.setProperty("runtime.log.logsystem.log4j.logger",
						      TextRenderUtil.class.getName());
					
					velocityEngine.init();
				}
			}
		}
		
		// null begets null
		if (aInput == null) {
			return null;
		}
		
		// consider null as an empty context
		if (aContextProps == null) {
			aContextProps = new HashMap<String,Object>();
		}
		
		StringWriter myStringWriter = new StringWriter();
		
		velocityEngine.evaluate(
				new VelocityContext(aContextProps),
				myStringWriter,
				"[eval input]",
				new StringReader(aInput)
				);
		
		return myStringWriter.toString();
		
	}
	
}
