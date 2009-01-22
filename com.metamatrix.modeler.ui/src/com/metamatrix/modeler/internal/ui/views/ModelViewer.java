/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.views;

import org.eclipse.jface.viewers.IDoubleClickListener;

/**
 * ModelViewer is a common interface for all ViewParts and ViewPages that show 
 * model contents and wish to be double-click enabled with the ModelEditor.  
 * ModelViewer may be implemented by any IViewPart or any IPage.
 */
public interface ModelViewer {

    /**
     * Add an IDoubleClickListener to this Part so that double-click of model objects can be sent
     * to the appropriate ModelEditor and ModelEditorPage.
     * @param listener a DoubleClickListener
     */
    void addModelObjectDoubleClickListener(IDoubleClickListener listener);

    /**
     * Remove the specified IDoubleClickListener from this Part.
     * @param listener a DoubleClickListener
     */
    void removeModelObjectDoubleClickListener(IDoubleClickListener listener);

}
