/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v810;

import org.junit.Test;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v89.Test89Resolver;

@SuppressWarnings( {"javadoc"} )
public class Test810Resolver extends Test89Resolver {

    protected Test810Resolver(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test810Resolver() {
        this(Version.TEIID_8_10);
    }

    @Override
    @Test
    @Removed(Version.TEIID_8_10)
    public void testUnaliasedOrderByFails() {
        // No  longer used
    }
    
    @Override
    @Test
    @Removed(Version.TEIID_8_10)
    public void testUnaliasedOrderByFails1() {
        // No longer used
    }

    @Override
    @Test
    @Removed(Version.TEIID_8_10)
    public void testOrderByUnrelated2() {
     // No longer used
    }
}
