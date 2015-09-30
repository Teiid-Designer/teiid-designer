/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v87;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v86.Test86Resolver;
import org.teiid.query.sql.lang.Query;

@SuppressWarnings( {"nls", "javadoc"} )
public class Test87Resolver extends Test86Resolver {

    protected Test87Resolver(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test87Resolver() {
        this(Version.TEIID_8_7);
    }

    @Test
    public void testResolveInputParameters() throws Exception {
        List bindings = new ArrayList();
        bindings.add("pm1.g2.e1 as x"); //$NON-NLS-1$

        Query resolvedQuery = (Query)helpResolveWithBindings("SELECT pm1.g1.e1, input.x FROM pm1.g1", metadata, bindings); //$NON-NLS-1$

        helpCheckFrom(resolvedQuery, new String[] {"pm1.g1"}); //$NON-NLS-1$
        assertEquals("SELECT pm1.g1.e1, pm1.g2.e1 AS x FROM pm1.g1", resolvedQuery.toString());
    }
}
