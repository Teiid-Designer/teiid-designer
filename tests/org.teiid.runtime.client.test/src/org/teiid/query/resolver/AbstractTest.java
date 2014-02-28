/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.unittest.RealMetadataFactory;

/**
 *
 */
public abstract class AbstractTest {

    private final ITeiidServerVersion teiidVersion;

    private final QueryParser queryParser;

    private final RealMetadataFactory metadataFactory;

    /**
     * @param teiidVersion
     */
    public AbstractTest(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
        this.queryParser = new QueryParser(teiidVersion);
        this.metadataFactory = new RealMetadataFactory(teiidVersion);
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

    /**
     * @return the metadataFactory
     */
    public RealMetadataFactory getMetadataFactory() {
        return this.metadataFactory;
    }

    protected abstract AbstractTestFactory getFactory();

}
