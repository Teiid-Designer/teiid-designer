/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.editor;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.command.CompoundCommand;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.transaction.UndoableImpl;
import org.teiid.designer.metamodels.relationship.RelationshipType;


/**
 * AddRelationshipUndoableEdit
 */
public class AddRelationshipTypeUndoableEdit extends UndoableImpl {


    /**
     */
    private static final long serialVersionUID = 1L;
    private RelationshipType relationshipType;

    /**
     * Construct an instance of AddRelationshipTypeUndoableEdit.
     * @param container
     * @param command
     * @param resources
     * @param relationshipType
     */

    public AddRelationshipTypeUndoableEdit( Container container,
                                            CompoundCommand command,
                                            Collection colResources,
                                            RelationshipType relationshipType ) {
                                                
        super( container, command, colResources, new Object() );
        this.relationshipType = relationshipType;        
    }

    @Override
    public void undo() {
        // delete the RelationshipType
        try {        
            ModelerCore.getModelEditor().delete( relationshipType/*, false*/ );
        } catch ( ModelerCoreException mce ) {
            ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());                                                   
        }
    }
    
    // ===================
    //  Inner Classes
    // ===================

    static class AddRelationshipTypeCommand extends CompoundCommand {


        /**
         * This constructs an instance from the domain, which provides access the clipboard collection 
         * via {@link EditingDomain#getCommandStack}.
         */
        public AddRelationshipTypeCommand() {
          
        }
        
        @Override
        public String getLabel() {
            return "Add Relationship Type"; //$NON-NLS-1$
        }

        /**
         * @see org.eclipse.emf.common.command.CompoundCommand#execute()
         */
        @Override
        public void execute() {
//            // Execute any existing commands ...
//            super.execute();
        }
   
    }

    
}
