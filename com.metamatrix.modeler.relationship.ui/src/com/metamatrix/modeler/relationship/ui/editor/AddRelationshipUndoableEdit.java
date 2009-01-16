/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.relationship.ui.editor;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.command.CompoundCommand;

import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.internal.core.transaction.UndoableImpl;

/**
 * AddRelationshipUndoableEdit
 */
public class AddRelationshipUndoableEdit extends UndoableImpl {


    /**
     */
    private static final long serialVersionUID = 1L;
    private Relationship relationship;

    /**
     * Construct an instance of AddRelationshipUndoableEdit.
     * @param container
     * @param command
     * @param resources
     * @param relationship
     */

    public AddRelationshipUndoableEdit( Container container,
                                        CompoundCommand command,
                                        Collection colResources,
                                        Relationship relationship ) {
                                            
        super( container, command, colResources, new Object() );
        this.relationship = relationship;        
    }

    @Override
    public void undo() {
        // delete the Relationship
        try {        
            ModelerCore.getModelEditor().delete( relationship/*, false*/ );
        } catch ( ModelerCoreException mce ) {
            ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());                                                   
        }
    }
    
    // ===================
    //  Inner Classes
    // ===================

    static class AddRelationshipCommand extends CompoundCommand {


        /**
         * This constructs an instance from the domain, which provides access the clipboard collection 
         * via {@link EditingDomain#getCommandStack}.
         */
        public AddRelationshipCommand() {
          
        }
        
        @Override
        public String getLabel() {
            return "Add Relationship"; //$NON-NLS-1$
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
