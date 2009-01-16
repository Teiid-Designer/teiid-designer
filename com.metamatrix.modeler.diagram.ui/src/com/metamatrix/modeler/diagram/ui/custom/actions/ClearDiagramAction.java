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

package com.metamatrix.modeler.diagram.ui.custom.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.DiagramAction;
import com.metamatrix.modeler.diagram.ui.custom.CustomDiagramModelFactory;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * ClearTransformation
 */
public class ClearDiagramAction extends DiagramAction {
	private static final String THIS_CLASS = "ClearDiagramAction"; //$NON-NLS-1$
    private DiagramEditor editor;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public ClearDiagramAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.CLEAR_DIAGRAM));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
	@Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        
        setEnabled(shouldEnable());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        if( editor != null ) {
    
            // Need to get the current diagram
            DiagramModelNode diagramNode = editor.getCurrentModel();
            // Need to get ahold of the CustomDiagramModelFactory
            CustomDiagramModelFactory modelFactory = (CustomDiagramModelFactory)editor.getModelFactory();
            // And call add(SelectionUtilities.getSelectedEObjects(getSelection())
            
            if( diagramNode != null && modelFactory != null ) {
				boolean requiredStart = false;
				boolean succeeded = false;
				try {
					//------------------------------------------------- 
					// Let's wrap this in a transaction!!! 
					//------------------------------------------------- 

					requiredStart = ModelerCore.startTxn(true, false, "Add To Custom Diagram", this); //$NON-NLS-1$$
    
					modelFactory.clear(diagramNode);
					
					succeeded = true;
				}  catch (Exception ex){
					DiagramUiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName() + ":" +  THIS_CLASS + ".doRun()"); //$NON-NLS-1$  //$NON-NLS-2$
				} finally {
					if(requiredStart){
						if ( succeeded ) {
							ModelerCore.commitTxn( );
						} else {
							ModelerCore.rollbackTxn( );
						}
					}
				}
			}
        }
    }
    
    
    private boolean shouldEnable() {
        return diagramNotEmpty() && isWritable();
    }
    
    public void setDiagramEditor(DiagramEditor editor) {
        this.editor = editor;
    }
    
    private boolean diagramNotEmpty() {
        if( editor != null ) {
            DiagramModelNode currentDiagram = editor.getCurrentModel();
            if( currentDiagram != null && currentDiagram.getChildren() != null && 
                ! currentDiagram.getChildren().isEmpty() &&
                currentDiagram.getChildren().size() > 0 )
                return true;
        }
            
        return false;
    }
    
    private boolean isWritable() {
        if( editor != null ) {
            DiagramModelNode currentDiagram = editor.getCurrentModel();
            if( currentDiagram != null ) {
                EObject diagram = currentDiagram.getModelObject();
                if( !ModelObjectUtilities.isReadOnly(diagram)) {
                    return true;
                }
            }
        }
        return false;
    }
}
