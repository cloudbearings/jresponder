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

import javax.persistence.CascadeType;
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

import org.hibernate.annotations.GenericGenerator;
import org.jresponder.util.PropUtil;

/**
 * A log entry record - indicates that some sort of event occurred for
 * a subscriber.  Subscriptions, unsubscriptions, messages being sent,
 * bounces, etc. are all recorded as log entries.  
 * 
 * @author bradpeabody
 *
 */
@Entity
@Table(name="jr_log_entry")
public class LogEntry {
	
	private Long id;
	
	/**
	 * The PK
	 * @return
	 */
	@Id
	@GeneratedValue(generator="native")
	@GenericGenerator(name="native", strategy = "native")
	@Column(name="jr_log_entry_id")
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

	private Subscriber subscriber;
	
	@JoinColumn(name="jr_subscriber_id")
	@ManyToOne(cascade=CascadeType.ALL)
	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}
	
	private String messageGroupName;

	@Column(name="message_group_name")
	public String getMessageGroupName() {
		return messageGroupName;
	}

	public void setMessageGroupName(String messageGroupName) {
		this.messageGroupName = messageGroupName;
	}
	
	private String type;

	/**
	 * The type of this log entry.  See {@link LogEntryType}
	 * @return
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Return the 'type' field as a LogEntryType 
	 * @return
	 */
	@Transient
	public LogEntryType getLogEntryType() {
		return LogEntryType.valueOf(type);
	}
	
	public void setLogEntryType(LogEntryType aLogEntryType) {
		type = aLogEntryType.toString();
	}
	
}
