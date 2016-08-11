/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver;

import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.unittest.RealMetadataFactory;

/**
 *
 */
public abstract class AbstractTest {

    private final ITeiidServerVersion teiidVersion;

    private final DataTypeManagerService dataTypeManager;

    private final QueryParser queryParser;

    private final RealMetadataFactory metadataFactory;

    /**
     * @param teiidVersion
     */
    public AbstractTest(Version teiidVersion) {
        this.teiidVersion = teiidVersion.get();
        this.dataTypeManager = DataTypeManagerService.getInstance(this.teiidVersion);
        this.queryParser = new QueryParser(this.teiidVersion);
        this.metadataFactory = new RealMetadataFactory(this.teiidVersion);
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

    /**
     * @return the dataTypeManager
     */
    public DataTypeManagerService getDataTypeManager() {
        return this.dataTypeManager;
    }

    protected abstract AbstractTestFactory getFactory();

}
