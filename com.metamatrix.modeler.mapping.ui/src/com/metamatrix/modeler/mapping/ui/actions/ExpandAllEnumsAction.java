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

package com.metamatrix.modeler.mapping.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.ExpandableNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramUtil;


/** 
 * @since 5.0
 */
public class ExpandAllEnumsAction extends MappingAction {

//    private static String EXPAND_ALL_ENUMERATIONS_TEXT 
//        = UiPlugin.Util.getString( "ExpandAllEnumsAction.text" );  //$NON-NLS-1$
    
    private boolean isLogicalModel = false;
    private boolean modelTypeChecked = false;
    /** 
     * 
     * @since 5.0
     */
    public ExpandAllEnumsAction() {
        super( UiPlugin.getDefault(), SWT.DEFAULT );
        
//        setText("Expand all Enumerations");
//        setToolTipText( "Expand all Enumerations"); //EXPAND_ALL_ENUMERATIONS_TEXT );
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
        determineEnablement();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        // Get Diagram Editor, get all "Enum" UmlClassifiers and then "Expand" them all
        
        DiagramModelNode[] allEnumNodes = MappingDiagramUtil.getEnumeratedTypeNodes();
        if( allEnumNodes.length > 0 ) {
            for( int i = 0; i<allEnumNodes.length; i++ ) {
                if( allEnumNodes[i] instanceof ExpandableNode ) {
                    if( ! ((UmlClassifierNode)allEnumNodes[i]).isExpanded() ) {
                    // Just call update(EXPAND) to notify the part to do the expand for each classifier
                        ((UmlClassifierNode)allEnumNodes[i]).expand();
                    }
                } 
            }
        }
    }
    
    private void checkModelType() {
        if( !modelTypeChecked ) {
            DiagramEditor dEditor = DiagramEditorUtil.getVisibleDiagramEditor();
            if( dEditor != null ) {
                isLogicalModel = ModelIdentifier.isLogicalModel(dEditor.getCurrentModelResource());
                modelTypeChecked = true;
            }
        }
    }

    private void determineEnablement() {
        // Only enable when the model is "Logical"
        checkModelType();
        
        boolean enable = isLogicalModel;

        setEnabled(enable);
    }
}
