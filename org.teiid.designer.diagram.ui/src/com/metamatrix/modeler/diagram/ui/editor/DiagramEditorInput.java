/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
//import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;

/**
 * PackageDiagramResource is a temporary interface for experimenting with
 * launching the GEF editor from a diagram object in a tree.  This interface
 * will be deleted as soon as the diagram metamodel is available.
 */
public class DiagramEditorInput implements IEditorInput, DiagramUiConstants {
    private Diagram diagram;
    
    public DiagramEditorInput(Diagram diagram) {
        this.diagram = diagram;
    }
    
    /**
     * Return the Diagram
     * @return an Diagram
     */
    public Diagram getDiagram() {
        return diagram;
    }
    
    
    public Object getAdapter(Class key) {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {
        return "Unknown Diagram"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText() {    
        return "Unknown Diagram"; //$NON-NLS-1$
    }
}

