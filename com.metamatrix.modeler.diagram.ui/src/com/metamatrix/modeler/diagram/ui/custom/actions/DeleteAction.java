/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;


import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.actions.DiagramAction;

/**
 * DeleteAction
 */
public class DeleteAction extends DiagramAction implements DiagramUiConstants {
    //============================================================================================================================
    // Constants
    //============================================================================================================================
    // Constructors

    /**
     * Construct an instance of DeleteAction.
     * 
     */
    public DeleteAction() {
        super();
        final ISharedImages imgs = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
    }

    //============================================================================================================================
    // ISelectionListener Methods

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        // sample code:
        super.selectionChanged(part, selection);
        
        determineEnablement();
    }

    //============================================================================================================================
    // Action Methods

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
    }
    
//    private void delete(List deleteList) throws ModelerCoreException {
//        // Walk through all objects.  Treat Staging tables and mapping classes differently
//        EObject eObj = null;
//        boolean hasMPF = (getMappingClassFactory() != null);
//        Iterator iter = deleteList.iterator();
//        while( iter.hasNext() ) {
//            eObj = (EObject)iter.next();
//            
//            if ( isStagingTable(eObj) ) {
//                if( hasMPF )
//                    getMappingClassFactory().deleteStagingTable((StagingTable)eObj);
//            } else if( isMappingClass(eObj) ) {
//                if( hasMPF )
//                    getMappingClassFactory().deleteMappingClass((MappingClass)eObj);
//            } else if( isMappingClassColumn(eObj) ) {
//                if( hasMPF )
//                    getMappingClassFactory().deleteMappingClassColumn((MappingClassColumn)eObj);
//            } else {
//                ModelerCore.getModelEditor().delete((EObject)eObj);
//            }
//        }
//    }


    private void determineEnablement() {
        boolean enable = false;

        setEnabled(enable);
    }
}
