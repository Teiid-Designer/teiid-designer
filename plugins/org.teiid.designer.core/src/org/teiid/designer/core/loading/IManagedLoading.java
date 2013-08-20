/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.loading;

import java.util.Properties;
import org.teiid.designer.runtime.spi.ITeiidServerManager;

/**
 * Interface for components that need to await the loading of the {@link ITeiidServerManager}.
 * @see ComponentLoadingManager
 */
public interface IManagedLoading {

    /**
     * Method to complete the population of the component once the {@link ITeiidServerManager}
     * is fully restored. Passes back to the component the set of properties provided to the
     * {@link ComponentLoadingManager} when originally called.
     *
     * @param args
     */
    void manageLoad(Properties args);

}
