/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v8124;

import org.junit.Test;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v810.Test810Validator;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class Test8124Validator extends Test810Validator {

    protected Test8124Validator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test8124Validator() {
        this(Version.TEIID_8_12_4);
    }

    @Test
    @Override
    public void testWindowFunctionWithNestedaggAllowed1() {
        helpValidate("SELECT max(min(e1)) over (order by max(e2)) from pm1.g1 group by e1",
                     new String[] {},
                     getMetadataFactory().example1Cached());
    }

    @Test
    @Override
    public void testInsertIntoVirtualWithQueryExpression() {
        IQueryMetadataInterface qmi = getMetadataFactory().example1Cached();

        String sql = "select * from vm1.g1 as x"; //$NON-NLS-1$

        sql = "insert into vm1.g1 (e1, e2, e3, e4) select * from pm1.g1"; //$NON-NLS-1$

        helpValidate(sql, new String[] {}, qmi);

    }
}
