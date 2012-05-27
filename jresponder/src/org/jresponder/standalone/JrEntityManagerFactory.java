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

import java.util.Map;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

/**
 * Extend Spring's LocalContainerEntityManagerFactoryBean to set some defaults,
 * to simplify config
 * @author bradpeabody
 *
 */
public class JrEntityManagerFactory extends LocalContainerEntityManagerFactoryBean implements PersistenceUnitPostProcessor {

	public JrEntityManagerFactory() {
		super();
		setPersistenceXmlLocation("classpath*:META-INF/persistence.xml");
		setPersistenceUnitName("org.jresponder.PU");
		setPersistenceUnitPostProcessors(this);
	}

	private Map<String,String> addProps;

	/**
	 * @return the addProps
	 */
	public Map<String,String> getAddProps() {
		return addProps;
	}

	/**
	 * @param addProps the addProps to set
	 */
	public void setAddProps(Map<String,String> addProps) {
		this.addProps = addProps;
	}

	@Override
	public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo arg0) {
		
		if (addProps != null) {
			for (String k: addProps.keySet()) {
				String v = addProps.get(k);
				//System.out.println("HibernatePropsHelper: " + k + " = " + v);
				arg0.addProperty(k, v);
			}
		}
		
	}
	
}
