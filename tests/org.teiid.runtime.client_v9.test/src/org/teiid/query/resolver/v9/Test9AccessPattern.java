/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v9;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.AbstractTestAccessPattern;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.v9.Test9Factory;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test9AccessPattern extends AbstractTestAccessPattern {

    private Test9Factory factory;

    protected Test9AccessPattern(Version teiidVersion) {
        super(teiidVersion);
    }
   
    public Test9AccessPattern() {
        super(Version.TEIID_9_0);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test9Factory(getQueryParser());

        return factory;
    }
}
