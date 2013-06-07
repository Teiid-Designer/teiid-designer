/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl.importer.node;

import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.designer.ddl.DdlImporterModel;
import org.teiid.designer.ddl.DdlNodeImporter;
import org.teiid.designer.metamodels.relational.RelationalFactory;

/**
 *
 */
public abstract class AbstractImporter implements DdlNodeImporter {

    /**
     * Entity Not Found Exception
     */
    protected class EntityNotFoundException extends Exception {

        private static final long serialVersionUID = 1L;

        /**
         * @param message
         */
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    private DdlImporterModel importerModel;

    /**
     * @param importerModel
     */
    protected void setImporterModel(DdlImporterModel importerModel) {
        this.importerModel = importerModel;
    }

    /**
     * @return the importerModel
     */
    protected DdlImporterModel getImporterModel() {
        return this.importerModel;
    }

    /**
    * @return relational factory
    */
    protected RelationalFactory getFactory() {
        return importerModel.getFactory();
    }

    /**
     * @param message
     */
    protected void addProgressMessage(String message) {
        importerModel.getProgressMessages().add(message);
    }

    /**
     * Gets boolean value for the provided text string
     *
     * @param text a text string
     * @return 'true' if provided string is "true", otherwise 'false'
     */
    protected boolean isTrue(String text) {
        return Boolean.valueOf(text);
    }

    /**
     * Is given node of the given dialect
     *
     * @param node
     * @param dialectType
     * @return true if node has dialect
     */
    protected boolean is(AstNode node, String dialectType) {
        return node.hasMixin(dialectType);
    }
}
