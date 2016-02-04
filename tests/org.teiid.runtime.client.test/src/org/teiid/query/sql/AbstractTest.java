/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.runtime.client.admin.StringConstants;

/**
 * @param <T>
 */
public abstract class AbstractTest<T extends LanguageObject> implements StringConstants {

    protected ITeiidServerVersion teiidVersion;

    protected QueryParser parser;

    /**
     * @param teiidVersion
     */
    public AbstractTest(Version teiidVersion) {
        this.teiidVersion = teiidVersion.get();
        this.parser = new QueryParser(this.teiidVersion);
    }

    protected abstract AbstractTestFactory getFactory();
}
