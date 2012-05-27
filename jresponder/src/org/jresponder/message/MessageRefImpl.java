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
package org.jresponder.message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.jresponder.domain.Subscriber;
import org.jresponder.domain.Subscription;
import org.jresponder.engine.SendConfig;
import org.jresponder.util.TextRenderUtil;
import org.jresponder.util.TextUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Default MessageRef implementation - reads messages from a file on disk,
 * renders contents using Velocity
 * 
 * @author bradpeabody
 *
 */
public class MessageRefImpl implements MessageRef {
	
	/* ====================================================================== */
	/* Logger boiler plate                                                    */
	/* ====================================================================== */
	private static Logger l = null;
	private Logger logger() { if (l == null) l = LoggerFactory.getLogger(this.getClass()); return l; } 
	/* ====================================================================== */

	private String name;
	private File file;
	private String fileContents;
	private long fileContentsTimestamp = 0;
	private Document document;
	private Map<String,String> propMap;
	
	/**
	 * Default constructor - make sure to call setFile() and then refresh()
	 */
	public MessageRefImpl() {
		
	}
	
	/**
	 * Constructor which calls setFile() and refresh() for you
	 * @param aFile
	 * @throws InvalidMessageException
	 */
	public MessageRefImpl(File aFile) throws InvalidMessageException {
		setFile(aFile);
		refresh();
	}
	
	@Override
	public String getName() {
		return name;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		// id is set to file name without .html extension
		name = file.getName().replaceAll("[.]html$", "");
	}
	
	public String getFileContents() {
		return fileContents;
	}

	@Override
	public synchronized void refresh() throws InvalidMessageException {
		
		try {
			
			logger().debug("Starting refresh for: {}", file.getCanonicalPath());
			
			// set timestamp
			fileContentsTimestamp = file.lastModified();
			
			StringBuilder myStringBuilder = new StringBuilder();
			char[] buf = new char[4096];
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			int len;
			while ((len = r.read(buf)) > 0) {
				myStringBuilder.append(buf, 0, len);
			}
			r.close();
			
			fileContents = myStringBuilder.toString();
			
			document = Jsoup.parse(fileContents, "UTF-8");
			
			propMap = new HashMap<String,String>();
			
			Elements myMetaTagElements = document.select("meta");
			if (myMetaTagElements == null || myMetaTagElements.isEmpty()) {
				throw new InvalidMessageException("No meta tags found in file: "+file.getCanonicalPath());
			}
			
			for (Element myPropElement: myMetaTagElements) {
				String myName = myPropElement.attr("name");
				String myValue = myPropElement.attr("content");
				propMap.put(myName, myValue);
			}
			
			// bodies are not read at all until message generation time
			
		} catch (IOException e) {
			throw new InvalidMessageException(e);
		}

		// debug dump
		if (logger().isDebugEnabled()) {
			for (String myKey: propMap.keySet()) {
				logger().debug("  property -- {}: {}", myKey, (propMap.get(myKey)));
			}
		}
	}

	/**
	 * Get a property by string name
	 */
	@Override
	public String getProp(String aName) {
		return propMap.get(aName);
	}

	/**
	 * Type-safe version of getProp
	 */
	@Override
	public String getProp(MessageRefProp aName) {
		return getProp(aName.toString());
	}

	/**
	 * Get all property names
	 */
	@Override
	public List<String> getPropNames() {
		return new ArrayList<String>(propMap.keySet());
	}

	/**
	 * Gets the JR_WAIT_AFTER_LAST_MESSAGE property and parses it as an
	 * ISO8601 duration and returns the number of milliseconds it represents.
	 * Returns null if not set.
	 * @throws IllegalArgumentException if the value is in an invalid format
	 * @return
	 */
	@Override
	public Long getWaitAfterLastMessage() {
		
		// try to parse time from message
		String myWaitAfterLastMessageString =
				this.getProp(MessageRefProp.JR_WAIT_AFTER_LAST_MESSAGE);
		if (myWaitAfterLastMessageString == null) {
			logger().debug("No JR_WAIT_AFTER_LAST_MESSAGE property found on message (message={})", getName());
			return null;
		}
		
		PeriodFormatter myPeriodFormatter = ISOPeriodFormat.standard();
		try {
			long myDuration = myPeriodFormatter.parsePeriod(myWaitAfterLastMessageString).toStandardDuration().getMillis();
			return myDuration;
		}
		catch (IllegalArgumentException e) {
			logger().error("Unable to parse JR_WAIT_AFTER_LAST_MESSAGE value for (message={}), value was: \"{}\"  (this is a problem you need to fix!!! e.g. for one day, use \"P1D\")", new Object[] { getName(), myWaitAfterLastMessageString });
			throw new IllegalArgumentException("Error parsing JR_WAIT_AFTER_LAST_MESSAGE value: " + myWaitAfterLastMessageString, e);
		}
		
	}	

	
	/**
	 * Render a message in the context of a particular subscriber
	 * and subscription.
	 */
	@Override
	public boolean populateMessage(MimeMessage aMimeMessage, SendConfig aSendConfig, Subscriber aSubscriber, Subscription aSubscription) {
		
		try {
			
			// prepare context
			Map<String,Object> myRenderContext = new HashMap<String,Object>();
			myRenderContext.put("subscriber", aSubscriber);
			myRenderContext.put("subscription", aSubscription);
			myRenderContext.put("message", this);
			
			// render the whole file
			String myRenderedFileContents = TextRenderUtil.render(fileContents, myRenderContext);
			
			// now parse again with Jsoup
			Document myDocument = Jsoup.parse(myRenderedFileContents);
			
			String myHtmlBody = "";
			String myTextBody = "";
			
			// html body
			Elements myBodyElements = myDocument.select("#htmlbody");
			if (!myBodyElements.isEmpty()) {
				myHtmlBody = myBodyElements.html();
			}
			
			// text body
			Elements myJrTextBodyElements = myDocument.select("#textbody");
			if (!myJrTextBodyElements.isEmpty()) {
				myTextBody = TextUtil.getWholeText(myJrTextBodyElements.first());
			}
			
			// now build the actual message
			MimeMessage myMimeMessage = aMimeMessage;
			// wrap it in a MimeMessageHelper - since some things are easier with that
			MimeMessageHelper myMimeMessageHelper = new MimeMessageHelper(myMimeMessage);
			
			// set headers
			
			// subject
			myMimeMessageHelper.setSubject
								(
									TextRenderUtil.render
									(
										(String)propMap.get(MessageRefProp.JR_SUBJECT.toString()),
										myRenderContext
									)
								);

			// TODO: implement DKIM, figure out subetha
			
			String mySenderEmailPattern = aSendConfig.getSenderEmailPattern();
			String mySenderEmail = TextRenderUtil.render(mySenderEmailPattern, myRenderContext);
			myMimeMessage.setSender(new InternetAddress(mySenderEmail));
			
			myMimeMessageHelper.setTo(aSubscriber.getEmail());
			
			// from
			myMimeMessageHelper.setFrom
								(
									TextRenderUtil.render
									(
										(String)propMap.get(MessageRefProp.JR_FROM_EMAIL.toString()),
										myRenderContext
									),
									TextRenderUtil.render
									(
										(String)propMap.get(MessageRefProp.JR_FROM_NAME.toString()),
										myRenderContext
									)
								);
		
	
	        // see how to set body
			
			// if we have both text and html, then do multipart
			if (myTextBody.trim().length() > 0 && myHtmlBody.trim().length() > 0) {
				
				// create wrapper multipart/alternative part
				MimeMultipart ma = new MimeMultipart("alternative");
				myMimeMessage.setContent(ma);
				// create the plain text
				BodyPart plainText = new MimeBodyPart();
				plainText.setText(myTextBody);
				ma.addBodyPart(plainText);
				// create the html part
				BodyPart html = new MimeBodyPart();
				html.setContent(myHtmlBody, "text/html");
				ma.addBodyPart(html);
			}
			
			// if only HTML, then just use that
			else if (myHtmlBody.trim().length() > 0) {
				myMimeMessageHelper.setText(myHtmlBody, true);
			}
			
			// if only text, then just use that
			else if (myTextBody.trim().length() > 0) {
				myMimeMessageHelper.setText(myTextBody, false);
			}
			
			// if neither text nor HTML, then the message is being skipped,
			// so we just return null
			else {
				return false;
			}
			
			return true;
			
		}
		catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
			
	}

	@Override
	public synchronized boolean conditionalRefresh() throws InvalidMessageException {
		
		if (file.lastModified() != fileContentsTimestamp) {
			refresh();
			return true;
		}
		return false;
		
	}
	
	
}
