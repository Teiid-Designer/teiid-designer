/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.drawing.actions.DrawingAction;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * ShowParentDiagramAction
 */
public class ShowParentDiagramAction extends DrawingAction {

	/**
	 * Construct an instance of ShowParentDiagramAction.
	 * 
	 */
	public ShowParentDiagramAction() {
		super();
		setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.UP_PACKAGE_DIAGRAM));
		setEnabled(false);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.ui.actions.AbstractAction#doRun()
	 */
	@Override
    protected void doRun() {
		if( editor != null ) {
			Diagram diagram = getParentPackageDiagram();
			if( diagram != null ) {
				// Mark current navigation location using current open object
				UiUtil.getWorkbenchPage().getNavigationHistory().markLocation(editor);
				editor.openContext(diagram);
			}
		}
		determineEnablement();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
		super.selectionChanged(thePart, theSelection);
		determineEnablement();   
	}
    
	public void determineEnablement() {
		if( editor != null ) {
			Diagram diagram = getParentPackageDiagram();
			if( diagram != null )
				setEnabled(true);
			else
				setEnabled(false);
		}

	}
    
	private Diagram getParentPackageDiagram() {
		Diagram parentPackageDiagram = null;
		DiagramModelNode diagramNode = editor.getCurrentModel();
		if( diagramNode != null ) {
			Diagram currentDiagram = (Diagram)diagramNode.getModelObject();
			if( currentDiagram != null &&
				currentDiagram.getType() != null && 
				currentDiagram.getType().equals(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID) ) {
				Object diagramTarget = currentDiagram.getTarget();
				ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(currentDiagram);
				if( diagramTarget != null && 
					diagramTarget instanceof EObject && 
					modelResource != null &&
					!(diagramTarget instanceof ModelAnnotation)) {
					EObject packageObject = (EObject)diagramTarget;
					Object parentObject = packageObject.eContainer();
					if( parentObject != null && parentObject instanceof EObject ) {
						try {
							// get diagrams and find package diagram.
							List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams((EObject)parentObject));
							Iterator iter = diagramList.iterator();
							Diagram nextDiagram = null;
							while( iter.hasNext() && parentPackageDiagram == null ) {
								nextDiagram = (Diagram)iter.next();
								if( nextDiagram.getType() != null &&
									nextDiagram.getType().equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID) )
								parentPackageDiagram = nextDiagram;
							}
						} catch (ModelWorkspaceException e) {
							String message = "ShowParentDiagramAction cannot find parent package diagram";  //$NON-NLS-1$
							DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
						}
					} else if( parentObject == null ) {
						// This is a package under a model, so..... get the diagram for the resource?
						try {
							// get diagrams and find package diagram.
							List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams(null));
							Iterator iter = diagramList.iterator();
							Diagram nextDiagram = null;
							while( iter.hasNext() && parentPackageDiagram == null ) {
								nextDiagram = (Diagram)iter.next();
								if( nextDiagram.getType() != null &&
									nextDiagram.getType().equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID) )
								parentPackageDiagram = nextDiagram;
							}
						} catch (ModelWorkspaceException e) {
							String message = "ShowParentDiagramAction cannot find parent package diagram";  //$NON-NLS-1$
							DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
						}
					}
				}
                
                
			}
		}
        
		return parentPackageDiagram;
	}

}
