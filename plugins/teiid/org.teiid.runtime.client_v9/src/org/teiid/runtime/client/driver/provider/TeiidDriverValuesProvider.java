/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.driver.provider;

import org.eclipse.datatools.connectivity.drivers.DefaultDriverValuesProvider;
import org.eclipse.datatools.connectivity.drivers.IDriverValuesProvider;
import org.teiid.runtime.client.TeiidRuntimePlugin;

/**
 * Provides values for the default instance of the Teiid Connection Driver profile
 */
public class TeiidDriverValuesProvider extends DefaultDriverValuesProvider {

    @Override
    public String createDefaultValue(String key) {
        if (key.equals(IDriverValuesProvider.VALUE_CREATE_DEFAULT))
            return Boolean.TRUE.toString();

        if (key.equals(IDriverValuesProvider.VALUE_JARLIST)) {
            return TeiidRuntimePlugin.getPluginPath();
        }

        return super.createDefaultValue(key);
    }
}
