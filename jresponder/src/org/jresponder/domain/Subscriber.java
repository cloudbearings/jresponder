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
package org.jresponder.domain;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.jresponder.util.PropUtil;

/**
 * Corresponds to a unique email address.  Contains the attributes (specified
 * upon subscription) of this person.
 * 
 * @author bradpeabody
 *
 */
@Entity
@Table(name="jr_subscriber")
public class Subscriber {

	private Long id;
	private String email;
	private String status;
	private Set<Subscription> subscriptions;
	
	public Subscriber() {
		
	}
	
	public Subscriber(String aEmail) {
		setEmail(aEmail);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Subscription)) return false;
		Subscriber v = (Subscriber)o;
		// same id means equal
		if (getId() != null && getId().equals(v.getId())) {
			return true;
		}
		// same subscriber id and message group is also equal
		if (getEmail() != null && getEmail().equals(v.getEmail())) {
			return true;
		}
		return false;
			
	}
	
	@Override
	public int hashCode() {
		if (getEmail() != null) {
			return getEmail().hashCode();
		}
		return super.hashCode();
	}

	/**
	 * The PK
	 * @return
	 */
	@Id
	@GeneratedValue(generator="native")
	@GenericGenerator(name="native", strategy = "native")
	@Column(name="jr_subcriber_id")
	public Long getId() {
		return id;
	}
	
	public void setId(Long aId) {
		id = aId;
	}
	
	/* ====================================================================== */
	/* Date created/update boiler plate                                       */
	/* ====================================================================== */
	
    private Date dateCreated;
    private Date dateUpdated;

    @PrePersist protected void onCreate() { dateUpdated = dateCreated = new Date(); }
    @PreUpdate  protected void onUpdate() { dateUpdated = new Date(); }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date_created", nullable=false)
	public Date getDateCreated() { return dateCreated; }
	public void setDateCreated(Date v) { dateCreated = v; }
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date_updated", nullable=false)
	public Date getDateUpdated() { return dateUpdated; }
	public void setDateUpdated(Date v) { dateUpdated = v; }
	
	/* ====================================================================== */
	/* Props boiler plate                                                     */
	/* ====================================================================== */

	private String props;
	@Column(name="props",length=65536)
	public String getProps() { return props; }
	public void setProps(String v) { this.props = v; }
	@Transient
	public Map<String,Object> getPropsMap() { return PropUtil.propsToMap(props); }
	public void setPropsMap(Map<String,Object> v) { this.props = PropUtil.propsToString(v); }

	/* ====================================================================== */
	/* Fields                                                                 */
	/* ====================================================================== */

	/**
	 * The email address for this subscriber (unique)
	 * @return the email
	 */
	@Column(unique=true)
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

//	/**
//	 * @return the attributes
//	 */
//	@Column(length=65536)
//	public String getAttributes() {
//		return attributes;
//	}
//
//	/**
//	 * @param attributes the attributes to set
//	 */
//	public void setAttributes(String attributes) {
//		this.attributes = attributes;
//	}
//	
//	@Transient
//	@SuppressWarnings("unchecked")
//	public Map<String,Object> getAttributesMap() {
//		if (attributes == null) return null;
//		return (Map<String,Object>)JSONValue.parse(attributes);
//	}
//	
//	public void setAttributesMap(Map<String,Object> v) {
//		attributes = JSONValue.toJSONString(v);
//	}

	/**
	 * @return the subscriptions
	 */
	@OneToMany(fetch=FetchType.LAZY, mappedBy="subscriber")
	public Set<Subscription> getSubscriptions() {
		return subscriptions;
	}

	/**
	 * @param subscriptions the subscriptions to set
	 */
	public void setSubscriptions(Set<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	/**
	 * Status as string
	 * @return the status
	 */
	@Column(name="status",nullable=false)
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Status as SubscriberStatus instance (for type safety)
	 * @return the subscriberStatus
	 */
	@Transient
	public SubscriberStatus getSubscriberStatus() {
		if (status == null) {
			return null;
		}
		else {
			return SubscriberStatus.valueOf(status);
		}
	}

	/**
	 * @param subscriberStatus the subscriberStatus to set
	 */
	public void setSubscriberStatus(SubscriberStatus subscriberStatus) {
		if (subscriberStatus == null) {
			status = null;
		}
		else {
			status = subscriberStatus.toString();
		}
	}
	
}
