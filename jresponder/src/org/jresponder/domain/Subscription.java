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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.jresponder.util.PropUtil;

/**
 * Links a subscriber to a subscription to a MessageGroup (by name).
 * 
 * @author bradpeabody
 *
 */
@Entity
@Table
	(
	name="jr_subscription",
	uniqueConstraints=@UniqueConstraint
		(
		columnNames={"jr_subscriber_id", "message_group_name"}
		)
	)
public class Subscription {
	
	private Long id;
	private Subscriber subscriber;
	private String messageGroupName;
	private String lastMessageName;
	private Date lastSendDate;
	private Date nextSendDate;
	private String status;
	private String token;
	
	public Subscription() {
		setLastSendDate(new Date());
	}
	
	public Subscription(Subscriber aSubscriber, String aMessageGroupName) {
		this();
		setSubscriber(aSubscriber);
		setMessageGroupName(aMessageGroupName);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Subscription)) return false;
		Subscription v = (Subscription)o;
		// same id means equal
		if (getId() != null && getId().equals(v.getId())) {
			return true;
		}
		// same subscriber id and message group is also equal
		if (getSubscriber() != null && getMessageGroupName() != null && 
				getSubscriber().equals(v.getSubscriber()) && getMessageGroupName().equals(v.getMessageGroupName())) {
			return true;
		}
		return false;
			
	}
	
	@Override
	public int hashCode() {
		if (getSubscriber() != null && getMessageGroupName() != null) {
			return getSubscriber().hashCode() + getMessageGroupName().hashCode();
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
	@Column(name="jr_subcription_id")
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
	public Map<String,Object> getPropsMap() { return PropUtil.getInstance().propsToMap(props); }
	public void setPropsMap(Map<String,Object> v) { this.props = PropUtil.getInstance().propsToString(v); }

	/* ====================================================================== */
	/* Fields                                                                 */
	/* ====================================================================== */

	/**
	 * @return the subscriber
	 */
	@JoinColumn(name="jr_subscriber_id")
	@ManyToOne(/*cascade=CascadeType.ALL*/)
	public Subscriber getSubscriber() {
		return subscriber;
	}

	/**
	 * @param subscriber the subscriber to set
	 */
	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	/**
	 * @return the messageGroupName
	 */
	@Column(name="message_group_name")
	public String getMessageGroupName() {
		return messageGroupName;
	}

	/**
	 * @param messageGroupName the messageGroupName to set
	 */
	public void setMessageGroupName(String messageGroupName) {
		this.messageGroupName = messageGroupName;
	}

	@Column(name="last_message_name")
	public String getLastMessageName() {
		return lastMessageName;
	}

	public void setLastMessageName(String lastMessageName) {
		this.lastMessageName = lastMessageName;
	}
	
	@Column(name="last_send_date")
	public Date getLastSendDate() {
		return lastSendDate;
	}

	public void setLastSendDate(Date lastSendDate) {
		this.lastSendDate = lastSendDate;
	}

	@Column(name="next_send_date")
	@Index(name="subscription_next_send_date")
	public Date getNextSendDate() {
		return nextSendDate;
	}

	public void setNextSendDate(Date nextSendDate) {
		this.nextSendDate = nextSendDate;
	}

	/**
	 * The status of this subscription - as a string
	 * @return the status
	 */
	@Column(name="status", nullable=false)
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
	 * Get the status of this subscription as a ScriptionStatus enum value
	 * @return
	 */
	@Transient
	public SubscriptionStatus getSubscriptionStatus() {
		if (status == null) {
			return null;
		}
		else {
			return SubscriptionStatus.valueOf(status);
		}
	}
	
	/**
	 * @param v the subscription status value
	 */
	public void setSubscriptionStatus(SubscriptionStatus v) {
		if (v == null) {
			status = null;
		}
		else {
			status = v.toString();
		}
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	@Column(name="token",nullable=false)
	@Index(name="subscription_token")
	public void setToken(String token) {
		this.token = token;
	}


}
