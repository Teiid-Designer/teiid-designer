/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.ui.actions.IDiagramHelper;

/**
 * DiagramHelper is a set of static methods that modeler components can reference to determine
 * how to deal with different types of diagrams.  The plan is to integrate this class into the
 * diagram framework and provide an interface that can allow plugins to answer these questions
 * for specific diagram types.
 */
public abstract class DiagramHelper implements IDiagramHelper {
	
	

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canClone(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public boolean canClone(Diagram diagram) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canCopy(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public boolean canCopy(Diagram diagram) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canCut(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public boolean canCut(Diagram diagram) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canDelete(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public boolean canDelete(Diagram diagram) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canRename(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public boolean canRename(Diagram diagram) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#clone(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public void clone(Diagram diagram) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#copy(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public void copy(Diagram diagram) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#cut(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public void cut(Diagram diagram) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#delete(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public void delete(Diagram diagram) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#rename(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public void rename(Diagram diagram) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canCreate(com.metamatrix.metamodels.diagram.Diagram)
	 */
	public boolean canCreate(Diagram diagram) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#canPaste(com.metamatrix.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
	 */
	public boolean canPaste(List eObjects, EObject pasteParent) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IDiagramHelper#paste(com.metamatrix.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
	 */
	public void paste(List diagrams, EObject pasteParent) {
		// XXX Auto-generated method stub

	}

}
