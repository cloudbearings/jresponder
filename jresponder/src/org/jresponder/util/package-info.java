/**
 * Utility classes.  As a note on these - as much as possible,
 * the idea is that instead of using static utility methods, just use regular
 * methods and inject the utility class by name via Spring. In cases where
 * the bean in question is not Spring wired, a getInstance() method
 * can be used, which does the call to get the actual concrete implementation
 * class and effectively does the same thing.  This makes it so
 * that even though some key implementation
 * specifics are "hard coded" into these classes - you can still override
 * them without modifying the source. I'm hoping someone else will
 * read this and go "dude - yes! that's exactly right, thanks for doing that,
 * I just need to override the ...".
 */
package org.jresponder.util;