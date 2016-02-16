/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl.importer.node.teiid;

import org.teiid.designer.ddl.DdlImporterManager;
import org.teiid.designer.ddl.TeiidDdlNodeImporter;
import org.teiid.designer.relational.model.RelationalReferenceFactory;
import org.teiid.modeshape.sequencer.ddl.node.AstNode;

public abstract class TeiidAbstractImporter  implements TeiidDdlNodeImporter {

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

    private DdlImporterManager importManager;

    /**
     * @param importerManager
     */
    protected void setImporterManager(DdlImporterManager importerManager) {
        this.importManager = importerManager;
    }

    /**
     * @return the importerManager
     */
    protected DdlImporterManager getImporterManager() {
        return this.importManager;
    }

    /**
    * @return relational factory
    */
    protected RelationalReferenceFactory getFactory() {
        return RelationalReferenceFactory.INSTANCE;
    }

    /**
     * @param message
     */
    protected void addProgressMessage(String message) {
        importManager.getImportMessages().addProgressMessage(message);
    }
    
    /**
     * Increments count of unhandled instances of a particular type
     * @param typeStr
     */
    protected void incrementUnhandledNodeType(String typeStr) {
    	importManager.getImportMessages().incrementUnhandledNodeType(typeStr);
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
