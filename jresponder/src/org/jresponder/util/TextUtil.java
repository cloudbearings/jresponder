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

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * Text-related utilities 
 * 
 * @author bradpeabody
 *
 */
public class TextUtil {
	
	/**
	 * @param cell element that contains whitespace formatting
	 * @return
	 */
	public static String getWholeText(Element cell) {
	    String text = null;
	    List<Node> childNodes = cell.childNodes();
	    if (childNodes.size() > 0) {
	        Node childNode = childNodes.get(0);
	        if (childNode instanceof TextNode) {
	            text = ((TextNode)childNode).getWholeText();
	        }
	    }
	    if (text == null) {
	        text = cell.text();
	    }
	    return text;
	}
	
}
