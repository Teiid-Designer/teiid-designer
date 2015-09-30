/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v810;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.ValidatorReport;
import org.teiid.query.validator.v89.Test89Validator;

/**
 *
 */
@SuppressWarnings( {"javadoc"} )
public class Test810Validator extends Test89Validator {

    protected Test810Validator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test810Validator() {
        this(Version.TEIID_8_10);
    }

    @Test
    @Override
    public void testValidateObjectInComparison() throws Exception {
        String sql = "SELECT IntKey FROM BQT1.SmallA WHERE ObjectValue = 5"; //$NON-NLS-1$
        ValidatorReport report = helpValidate(sql, new String[] {"ObjectValue = 5"}, getMetadataFactory().exampleBQTCached()); //$NON-NLS-1$
        assertEquals("Expressions of type OBJECT, CLOB, BLOB, or XML cannot be used in comparison: ObjectValue = 5.", report.toString()); //$NON-NLS-1$
    }
}
