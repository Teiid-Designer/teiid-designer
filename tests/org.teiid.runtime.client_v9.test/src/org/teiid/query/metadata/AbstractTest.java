/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.metadata;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.QueryParser;

/**
 *
 */
public abstract class AbstractTest {

    private final ITeiidServerVersion teiidVersion;

    private final QueryParser queryParser;

    /**
     * @param teiidVersion
     */
    public AbstractTest(Version teiidVersion) {
        this.teiidVersion = teiidVersion.get();
        this.queryParser = new QueryParser(this.teiidVersion);
    }

    /**
     * @return the teiidVersion
     */
    public ITeiidServerVersion getTeiidVersion() {
        return this.teiidVersion;
    }

    /**
     * @return the queryParser
     */
    public QueryParser getQueryParser() {
        return this.queryParser;
    }

}
