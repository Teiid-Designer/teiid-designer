/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.actions.IDiagramHelper;


/**
 * DiagramHelper is a set of static methods that modeler components can reference to determine
 * how to deal with different types of diagrams.  The plan is to integrate this class into the
 * diagram framework and provide an interface that can allow plugins to answer these questions
 * for specific diagram types.
 */
public abstract class DiagramHelper implements IDiagramHelper {
	
	

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canClone(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
	public boolean canClone(Diagram diagram) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canCopy(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
	public boolean canCopy(Diagram diagram) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canCut(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
	public boolean canCut(Diagram diagram) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canDelete(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
	public boolean canDelete(Diagram diagram) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canRename(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
	public boolean canRename(Diagram diagram) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#clone(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
	public void clone(Diagram diagram) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#copy(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
	public void copy(Diagram diagram) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#cut(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
	public void cut(Diagram diagram) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#delete(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
	public void delete(Diagram diagram) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#rename(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
	public void rename(Diagram diagram) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canCreate(org.teiid.designer.metamodels.diagram.Diagram)
	 */
	@Override
	public boolean canCreate(Diagram diagram) {
		return false;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#canPaste(org.teiid.designer.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
	 */
	public boolean canPaste(List eObjects, EObject pasteParent) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.ui.actions.IDiagramHelper#paste(org.teiid.designer.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
	 */
	public void paste(List diagrams, EObject pasteParent) {
		// XXX Auto-generated method stub

	}

}
