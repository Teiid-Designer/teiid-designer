/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.editors;

import org.eclipse.core.resources.IMarker;

import com.metamatrix.modeler.internal.ui.editors.ModelEditor;

/**
 * INavigationSupported
 */
public interface INavigationSupported {
    
    IMarker createMarker();

    void setParent( ModelEditor meParent );

}
