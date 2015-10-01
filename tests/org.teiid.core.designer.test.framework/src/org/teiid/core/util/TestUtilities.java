/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.core.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

/**
 *
 */
public class TestUtilities {

    /**
     * Sets the default teiid server version to the given version
     *
     * @param version
     */
    public static void setDefaultServerVersion(ITeiidServerVersion version) {
        ITeiidServer teiidServer = mock(ITeiidServer.class);
        when(teiidServer.getServerVersion()).thenReturn(version);

        ITeiidServerManager teiidServerManager = mock(ITeiidServerManager.class);
        when(teiidServerManager.getDefaultServer()).thenReturn(teiidServer);
        when(teiidServerManager.getDefaultServerVersion()).thenReturn(version);

        ModelerCore.setTeiidServerManager(teiidServerManager);
    }

    /**
     * Convenience function for setting the default teiid server version
     * to the Designer supported Teiid Default value
     */
    public static void setDefaultTeiidVersion() {
        setDefaultServerVersion(Version.TEIID_DEFAULT.get());
    }

    /**
     * Unregister any teiid server manager previously assigned to ModelerCore
     */
    public static void unregisterTeiidServerManager() {
        ModelerCore.setTeiidServerManager(null);
    }
}
