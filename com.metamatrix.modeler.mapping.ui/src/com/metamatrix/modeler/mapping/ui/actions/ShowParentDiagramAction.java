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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.drawing.actions.DrawingAction;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramUtil;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * ShowParentDiagramAction
 */
public class ShowParentDiagramAction extends DrawingAction {
    private static final String textString = "com.metamatrix.modeler.mapping.ui.actions.ShowParentDiagramAction.text";  //$NON-NLS-1$
    private static final String toolTipString = "com.metamatrix.modeler.mapping.ui.actions.ShowParentDiagramAction.toolTip";  //$NON-NLS-1$
    /**
     * Construct an instance of ShowParentDiagramAction.
     * 
     */
    public ShowParentDiagramAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.UP_PACKAGE_DIAGRAM));
        setToolTipText(UiConstants.Util.getString(toolTipString));
        setText(UiConstants.Util.getString(textString));
        setEnabled(false);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractAction#doRun()
     */
    @Override
    protected void doRun() {
        if( editor != null ) {
            // CLose object editor
            ModelEditorManager.closeObjectEditor();
            
            Diagram diagram = getParentDiagram();
            if( diagram != null ) {
                // Mark current navigation location using current open object
                UiUtil.getWorkbenchPage().getNavigationHistory().markLocation(editor);
                ModelEditorManager.closeObjectEditor();
                editor.openContext(diagram);
            }
        }
        determineEnablement();
    }
    
    public void determineEnablement() {
        if( editor != null ) {
            Diagram diagram = getParentDiagram();
            if( diagram != null )
                setEnabled(true);
            else
                setEnabled(false);
        }

    }
    
    private Diagram getParentDiagram() {
        Diagram parentDiagram = null;
        
        DiagramModelNode diagramNode = editor.getCurrentModel();
        
        if( diagramNode != null ) {
            Diagram currentDiagram = (Diagram)diagramNode.getModelObject();
            
            if( currentDiagram != null &&
                currentDiagram.getType() != null ) {
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(currentDiagram);
                if( modelResource != null ) {
                    if( currentDiagram.getType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID) ) {
                        Object diagramTarget = currentDiagram.getTarget();
                        if( diagramTarget != null && 
                            diagramTarget instanceof EObject ) {
                              // This is a package under a model, so..... get the diagram for the resource?
                              try {
                                  // get diagrams and find package diagram.
                                  List diagramList = modelResource.getModelDiagrams().getDiagrams(null);
                                  Iterator iter = diagramList.iterator();
                                  Diagram nextDiagram = null;
                                  while( iter.hasNext() && parentDiagram == null ) {
                                      nextDiagram = (Diagram)iter.next();
                                      if( nextDiagram.getType() != null 
                                       && nextDiagram.getType().equals(com.metamatrix.modeler.internal.diagram.ui.PluginConstants.PACKAGE_DIAGRAM_TYPE_ID) ) {
                                          parentDiagram = nextDiagram;
                                      } // endif
                                  } // endwhile

                                  // defect 16988 - create the parent if it doesn't exist yet:
                                  if (parentDiagram == null) {
                                      // we need to create the package diagram, since it has not existed before.
                                      // Doing this will make the model dirty.
                                      parentDiagram = DiagramUiPlugin.getDiagramTypeManager().getDiagramForContext(modelResource);
                                  } // endif
                              } catch (ModelWorkspaceException e) {
                                  String message = "ShowParentDiagramAction cannot find parent package diagram";  //$NON-NLS-1$
                                  DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
                              }
                        }
                    } else if( currentDiagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID) ) {
                        parentDiagram = MappingDiagramUtil.getCoarseMappingDiagram(currentDiagram);
                    }
                }
            }
        }
        
        return parentDiagram;
    }
    
}
