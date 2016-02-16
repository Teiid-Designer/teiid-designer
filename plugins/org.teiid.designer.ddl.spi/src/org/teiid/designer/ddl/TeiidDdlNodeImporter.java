/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl;

import java.util.Properties;

import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.modeshape.sequencer.ddl.node.AstNode;

public interface TeiidDdlNodeImporter {

    /**
     * Import the contents of the given {@link AstNode}
     *
     * @param rootNode
     * @param importManager
     * @param properties
     * @return the RelationalModel
     * @throws Exception 
     */
    RelationalModel importNode(AstNode rootNode, DdlImporterManager importManager, Properties props) throws Exception;

}
