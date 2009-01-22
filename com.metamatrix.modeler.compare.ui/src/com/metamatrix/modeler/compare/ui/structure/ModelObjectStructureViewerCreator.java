/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.ui.structure;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;


/**
 * A factory object for the <code>JavaStructureDiffViewer</code>.
 * This indirection is necessary because only objects with a default
 * constructor can be created via an extension point
 * (this precludes Viewers).
 */
public class ModelObjectStructureViewerCreator implements IViewerCreator {
    
    public Viewer createViewer( Composite parent, CompareConfiguration cc ) {
        return new ModelObjectStructureViewer( parent, cc );
    }
}

