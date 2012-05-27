/**
 * Provides main services related the various database objects.  I.e. if you
 * want to subscribe someone, you use one of these objects to do that - since
 * it encapsulates that process.  The idea is that these services focus on
 * the higher level concepts like subscribing a person, etc., as opposed to
 * the DAO objects (see package org.jresponder.dao) which deal with the lower
 * level calls to read and write individual objects from the database.
 * Each of these objects is automatically instantiated in the Spring container
 * using the <code>Component</code> annotation.
 */
package org.jresponder.service;