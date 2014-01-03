/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang.parser;

import org.teiid.runtime.client.lang.TeiidNodeFactory.CommonNodes;
import org.teiid.runtime.client.types.DataTypeManagerService;

/**
 *
 */
public interface TeiidParser {

    /**
     * @return dataTypeManagerService
     */
    DataTypeManagerService getDataTypeService();

    /**
     * @param nodeType
     * 
     * @return instance of commonly used node
     */
    <T> T createCommonNode(CommonNodes nodeType);
    
}
