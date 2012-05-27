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
import java.io.IOException;

import javax.servlet.ServletContextEvent;

import org.springframework.util.Log4jConfigurer;
import org.springframework.web.context.ContextLoaderListener;

/**
 * Automatically adds external config file and enables logging with
 * external log4j.properties - if those files exists.  Climbs up 
 * the tree looking for "conf/jresponder.xml" and "conf/log4j.properties"
 * Meant for use in standalone mode, falls back to regular Spring
 * behavior if not found.
 * 
 * @author bradpeabody
 *
 */
public class JrContextLoaderListener extends ContextLoaderListener {

	private static File externalConfigFile = null;
	private static boolean standaloneMode = false;
	
	private boolean didStartLogging = false;

	/**
	 * Gets the path to the auto-detected external configuration file
	 * (conf/jresponder.xml) if it exists.
	 * @return
	 */
	public static File getExternalConfigFile() {
		return externalConfigFile;
	}
	
	public static boolean isStandaloneMode() {
		return standaloneMode;
	}
	
	/**
	 * Finds the external configuration file and sets it so that
	 * getExternalConfigFile() will return it.  If found it also sets
	 * the system property jresponder.basedir so this can be used in
	 * the Spring configuration.
	 */
	@Override
	public void contextInitialized(ServletContextEvent aServletContextEvent) {

		File myDir = new File(aServletContextEvent.getServletContext().getRealPath("/"));
		try {
			for (int i = 0; i < 20; i++) {
				File myConfFile = new File(myDir, "conf/jresponder.xml");
				if (myConfFile.exists()) {
					externalConfigFile = myConfFile.getCanonicalFile();
					System.setProperty("jresponder.basedir", myDir.getCanonicalPath());
					standaloneMode = true;
					
					File myLog4jPropertiesFile = new File(myDir, "conf/log4j.properties");
					if (myLog4jPropertiesFile.exists()) {
						Log4jConfigurer.initLogging(myLog4jPropertiesFile.getCanonicalPath(), 1000);
						didStartLogging = true;
					}
					
					break;
				}
				myDir = new File(myDir, "..");
			}
		} catch (IOException e) {
			// should not happen
			e.printStackTrace();
		}

		// the rest is over to the Spring default
		super.contextInitialized(aServletContextEvent);
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// if we started logging, then turn it off
		if (didStartLogging) {
			Log4jConfigurer.shutdownLogging();
			didStartLogging = false;
		}
		super.contextDestroyed(event);
	}

}
