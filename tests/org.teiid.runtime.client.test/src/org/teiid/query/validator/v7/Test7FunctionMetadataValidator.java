/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v7;

import org.junit.Test;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.v7.Test7Factory;
import org.teiid.query.validator.AbstractTestFunctionMetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test7FunctionMetadataValidator extends AbstractTestFunctionMetadataValidator {

    private Test7Factory factory;

    /**
     *
     */
    public Test7FunctionMetadataValidator() {
        super(Version.TEIID_7_7);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test7Factory(getQueryParser());

        return factory;
    }

    @Test
    public void testValidateNameFail3() {
        helpTestValidateNameFail("a.b"); //$NON-NLS-1$
    }
}
