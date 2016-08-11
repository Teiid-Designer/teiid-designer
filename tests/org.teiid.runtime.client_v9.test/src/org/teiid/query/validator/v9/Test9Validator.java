/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v9;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.metadata.Table;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.v9.Test9Factory;
import org.teiid.query.validator.AbstractTestValidator;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class Test9Validator extends AbstractTestValidator {

    private Test9Factory factory;

    protected Test9Validator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test9Validator() {
        this(Version.TEIID_9_0);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test9Factory(getQueryParser());

        return factory;
    }

    // valid variable declared
    @Test
    public void testDoNothing() {

    }
}
