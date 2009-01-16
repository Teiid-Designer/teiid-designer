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

package com.metamatrix.modeler.diagram.ui.custom;


//import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
//import com.metamatrix.modeler.core.ModelerCore;
//import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;
//import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.DiagramHelper;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CustomDiagramHelper extends DiagramHelper {
//	private static final String PROBLEM = "CustomDiagramHelper.paste.problem"; //$NON-NLS-1$
//	private static final String UNDO_TEXT = "CustomDiagramHelper.paste.undoText"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canClone(com.metamatrix.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canClone(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canCopy(com.metamatrix.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canCopy(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canCut(com.metamatrix.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canCut(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canDelete(com.metamatrix.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canDelete(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canRename(com.metamatrix.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canRename(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canCreate(com.metamatrix.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canCreate(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canPaste(com.metamatrix.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
	 */
	public boolean canPaste(Diagram diagram, EObject pasteParent) {
		boolean pasteOK = true;
		
//		If paste parent is a UmlPackageType, then we can paste
		MetamodelAspect mmAspect = DiagramUiPlugin.getDiagramAspectManager().getUmlAspect(pasteParent);
		if( !(mmAspect instanceof UmlPackage))
			pasteOK = false;

		// Now we need to find out if all eObjects are of this custom diagram types

		String thisType = diagram.getType();
		if( thisType == null || !(thisType.equals(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID)) )
			pasteOK = false;

		return pasteOK;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#paste(com.metamatrix.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
	 */
	public void paste(Diagram diagram, EObject pasteParent) {
		// This is a little tricky.
		
		// 1) Paste will require this method to "Clone" the diagram into the diagram container, but change the 
		// target to this paste parent

//		String description = null;
//          
//		String path = ModelerCore.getModelEditor().getModelRelativatePath(diagram).toString();
//		description = DiagramUiConstants.Util.getString(UNDO_TEXT, path);
//        
//		boolean started = ModelerCore.startTxn(description, this);
//		boolean succeeded = false;
//		try {
//			Diagram clonedDiagram = null;
//			try {
//				clonedDiagram = (Diagram)ModelerCore.getModelEditor().clone(diagram);
//				clonedDiagram.setTarget(pasteParent);
//			} catch (ModelerCoreException theException) {
//				Object objPath = ModelerCore.getModelEditor().getModelRelativatePathIncludingModel(diagram);
//				String msg = DiagramUiConstants.Util.getString(PROBLEM,  objPath);
//				DiagramUiConstants.Util.log(IStatus.ERROR, theException, msg);
//			}
//			succeeded = true;
//		} finally {
//			if (started) {
//				if ( succeeded ) {
//					ModelerCore.commitTxn();
//				} else {
//					ModelerCore.rollbackTxn();
//				}
//			}
//		}
		
	}
}
