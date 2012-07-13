/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.custom;


//import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.uml.UmlPackage;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.PluginConstants;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.viewsupport.DiagramHelper;


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
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canClone(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canClone(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canCopy(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canCopy(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canCut(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canCut(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canDelete(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canDelete(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canRename(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canRename(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canCreate(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean canCreate(Diagram diagram) {
		return true;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canPaste(org.teiid.designer.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
	 */
	@Override
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
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#paste(org.teiid.designer.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
	 */
	@Override
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
