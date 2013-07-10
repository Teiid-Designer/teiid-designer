/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.connection.spi;

import org.teiid.designer.DesignerSPIPlugin;

/**
 * Simple interface provides DQP preview manager ability to ask for user password
 * during Preview action setup.
 *
 * @since 8.0
 */
public interface IPasswordProvider {

    /**
     * Extension Point ID
     */
    String PASSWORD_PROVIDER_EXTENSION_POINT_ID = DesignerSPIPlugin.PLUGIN_ID + ".teiidPasswordProvider"; //$NON-NLS-1$

    /**
     * Extension Point Element ID
     */
    String PASSWORD_PROVIDER_ELEMENT_ID = "passwordProvider"; //$NON-NLS-1$

	/**
	 * Get the password for the given criteria
	 *
	 * @param modleName
	 * @param profileName
	 * @return the password
	 */
	String getPassword(String modleName, String profileName);
}
