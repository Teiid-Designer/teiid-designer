/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query;

import org.teiid.designer.query.proc.ITeiidMetadataFileInfo;
import org.teiid.designer.query.proc.ITeiidXmlFileInfo;

/**
 * Provides getters for sql syntax strings that differ between runtime client versions
 */
public interface IProcedureService {

    /**
     * Prepare an SQL statement for the given file-based metdata
     * 
     * @param metadataFileInfo
     * @param relationalModelName 
     * 
     * @return SQL statement based on the given metadata information
     */
    String getSQLStatement(ITeiidMetadataFileInfo metadataFileInfo, String relationalModelName);

    /**
     * Prepare an SQL statement for the given xml-based metdata
     * 
     * @param xmlFileInfo
     * @param relationalModelName 
     * 
     * @return SQL statement based on the given metadata information
     */
    String getSQLStatement(ITeiidXmlFileInfo xmlFileInfo, String relationalModelName);
}
