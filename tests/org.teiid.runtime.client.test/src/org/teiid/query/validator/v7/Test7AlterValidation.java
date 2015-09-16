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
import org.teiid.query.validator.AbstractTestAlterValidation;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class Test7AlterValidation extends AbstractTestAlterValidation {

    private Test7Factory factory;

    /**
     *
     */
    public Test7AlterValidation() {
        super(Version.TEIID_7_7);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test7Factory(getQueryParser());

        return factory;
    }

    @Test
    public void testValidateAlterProcedure() {
        helpValidate("alter procedure spTest8a as begin select 1; end",
                     new String[] {"spTest8a"},
                     getMetadataFactory().exampleBQTCached());
        helpValidate("alter procedure MMSP1 as begin select 1; end",
                     new String[] {"BEGIN\nSELECT 1;\nEND"},
                     getMetadataFactory().exampleBQTCached());
    }
}
