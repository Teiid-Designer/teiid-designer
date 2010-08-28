/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.connection;

/**
 * Simple interface provides DQP preview manager ability to ask for user password
 * during Preview action setup.
 */
public interface IPasswordProvider {

	/**
	 * 
	 * @return the password
	 */
	String getPassword(String modleName, String profileName);
}
