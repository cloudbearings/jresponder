/**
 * Lower level data access.  Used by the services (see org.jresponder.service
 * package) as a simple way to look up or do other basic operations with
 * individual objects.  The idea is that these objects do most of the
 * SQL work so the services can focus on larger tasks like each of the
 * steps that go into subscribing or unsubscribing someone, etc.
 * Each of these objects is automatically instantiated in the Spring container
 * using the <code>Component</code> annotation.
 */
package org.jresponder.dao;