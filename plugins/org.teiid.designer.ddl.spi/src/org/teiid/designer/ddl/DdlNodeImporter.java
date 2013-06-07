/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl;

import org.modeshape.sequencer.ddl.node.AstNode;


/**
 *
 */
public interface DdlNodeImporter {

    /**
     * Import the contents of the given {@link AstNode}
     *
     * @param importerModel
     * @param rootNode
     * @throws Exception 
     */
    void importNode(DdlImporterModel importerModel, AstNode rootNode) throws Exception;

    /**
     * Perform any final processes upon completion of the import
     *
     * @throws Exception 
     */
    void importFinalize() throws Exception;

}
