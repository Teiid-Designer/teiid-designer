/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v8;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.v8.Test8Factory;
import org.teiid.query.validator.AbstractTestUpdateValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test8UpdateValidator extends AbstractTestUpdateValidator {

    private Test8Factory factory;

    protected Test8UpdateValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test8UpdateValidator() {
        this(Version.TEIID_8_0);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test8Factory(getQueryParser());

        return factory;
    }
}
