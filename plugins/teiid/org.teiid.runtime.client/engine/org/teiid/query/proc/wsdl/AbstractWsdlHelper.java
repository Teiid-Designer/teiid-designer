/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.proc.wsdl;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.language.SQLConstants;

/**
 *
 */
public abstract class AbstractWsdlHelper {

    private final ITeiidServerVersion teiidVersion;

    /**
     * @param teiidVersion
     */
    public AbstractWsdlHelper(ITeiidServerVersion teiidVersion) {
        super();
        this.teiidVersion = teiidVersion;
    }

    public ITeiidServerVersion getTeiidVersion() {
        return teiidVersion;
    }

    /**
     * Converts any name string to a valid SQL symbol segment
     * Basically looks to see if name is a reserved word and if so, returns the name in double-quotes
     * 
     * @param name
     * @return
     */
    protected String convertSqlNameSegment(String name) {       
        if( SQLConstants.isReservedWord(teiidVersion, name) ) {
            return '\"' + name + '\"';
        }
        
        return name;
    }

}
