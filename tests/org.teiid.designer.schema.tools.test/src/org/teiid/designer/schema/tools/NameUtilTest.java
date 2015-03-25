/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collection;
import junit.framework.TestCase;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.registry.TeiidRuntimeRegistry;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;

public class NameUtilTest extends TestCase {

    private static String dots;
    private static String parens;
    private static String trailingUnderscore;
    private static String duplicate;

    private Collection<ITeiidServerVersion> serverVersions;
    
    public NameUtilTest() throws Exception {
        serverVersions = TeiidRuntimeRegistry.getInstance().getSupportedVersions();
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dots = new String("foo.bar"); //$NON-NLS-1$
        parens = new String("foo(bar)"); //$NON-NLS-1$
        trailingUnderscore = new String("foo_bar_"); //$NON-NLS-1$
        duplicate = new String("foo_bar(foo_bar)"); //$NON-NLS-1$
    }
    
    /**
     * @param version
     */
    private void setDefaultServerVersion(ITeiidServerVersion version) {
        ITeiidServer teiidServer = mock(ITeiidServer.class);
        when(teiidServer.getServerVersion()).thenReturn(version);

        ITeiidServerManager teiidServerManager = mock(ITeiidServerManager.class);
        when(teiidServerManager.getDefaultServer()).thenReturn(teiidServer);
        when(teiidServerManager.getDefaultServerVersion()).thenReturn(version);

        ModelerCore.setTeiidServerManager(teiidServerManager);
    }

    /*
     * Test method for 'org.teiid.designer.schema.tools.NameUtil.normalizeNameForRelationalTable(String)'
     */
    public void testDots() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            assertEquals("foo_bar", NameUtil.normalizeName(dots)); //$NON-NLS-1$
        }
    }

    public void testParens() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            assertEquals("foo_bar", NameUtil.normalizeName(parens)); //$NON-NLS-1$
        }
    }

    public void testUnderscore() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            assertEquals("foo_bar", NameUtil.normalizeName(trailingUnderscore)); //$NON-NLS-1$
        }
    }

    public void testDuplicate() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            assertEquals("foo_bar", NameUtil.normalizeName(duplicate)); //$NON-NLS-1$
        }
    }

}
