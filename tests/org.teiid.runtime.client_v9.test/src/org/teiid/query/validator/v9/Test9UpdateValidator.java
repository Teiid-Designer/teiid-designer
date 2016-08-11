/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v9;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.v9.Test9Factory;
import org.teiid.query.validator.AbstractTestUpdateValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test9UpdateValidator extends AbstractTestUpdateValidator {

    private Test9Factory factory;

    protected Test9UpdateValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test9UpdateValidator() {
        this(Version.TEIID_9_0);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test9Factory(getQueryParser());

        return factory;
    }
}
