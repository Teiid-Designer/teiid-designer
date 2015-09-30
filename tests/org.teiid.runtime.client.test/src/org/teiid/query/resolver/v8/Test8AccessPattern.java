/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v8;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.AbstractTestAccessPattern;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.v8.Test8Factory;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test8AccessPattern extends AbstractTestAccessPattern {

    private Test8Factory factory;

    protected Test8AccessPattern(Version teiidVersion) {
        super(teiidVersion);
    }
   
    public Test8AccessPattern() {
        super(Version.TEIID_8_0);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test8Factory(getQueryParser());

        return factory;
    }
}
