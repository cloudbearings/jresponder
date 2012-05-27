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
package org.jresponder.standalone;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Adds a standalone config file to list of context paths, if it exists.
 * Also sets the jresponder.basedir system property appropriately if
 * external config is found.  It also sets the active Spring
 * profile to be either "standalone" or "webapp" appropriately.
 * <p>
 * TODO: make it so a context param can specify a list of other 
 * possible jresponder base dirs, e.g. "/usr/local/jresponder",
 * "/etc/jresponder", etc. - and put those in by default - makes
 * the config and templates be easily locatable where people will
 * actually want them.
 */
public class JrXmlWebApplicationContext extends XmlWebApplicationContext {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setConfigLocations(String[] locations) {
		List myLocations = new ArrayList(Arrays.asList(locations));
		File myConfigFile = JrContextLoaderListener.getExternalConfigFile();
		if (myConfigFile != null) {
			//System.out.println(myConfigFile.toURI().toString());
			myLocations.add(myConfigFile.toURI().toString());
		}
		
		// HACK: this isn't the greatest, but works for now - set the 
		// Spring profile, so we can make certain Spring context files
		// conditional depending on the deployment mode
		if (JrContextLoaderListener.isStandaloneMode()) {
			System.out.println("JrXmlWebApplication - using 'standalone' Spring profile");
			getEnvironment().setActiveProfiles("standalone");
		}
		else {
			System.out.println("JrXmlWebApplication - using 'webapp' Spring profile");
			getEnvironment().setActiveProfiles("webapp");
		}
		
		super.setConfigLocations((String[])myLocations.toArray(new String[0]));
	}
	
	
}