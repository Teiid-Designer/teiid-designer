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

package com.metamatrix.modeler.internal.ui.editors;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.NavigationLocation;

/**<p>
 * </p>
 * @since 4.0
 */
public final class ModelEditorNavigationLocation extends NavigationLocation {
    //============================================================================================================================
    // Variables
    
    private Object obj;

    //============================================================================================================================
    // Constructors

    /**<p>
     * </p>
     * @param editorPart
     * @since 4.0
     */
    public ModelEditorNavigationLocation(final ModelEditor editor, final Object object) {
        super(editor);
        this.obj = (object == null ? editor.getModelResource() : object);
    }

    //============================================================================================================================
    // Implemented Methods

    /**<p>
     * Does nothing.
     * </p>
     * @return False.
     * @see org.eclipse.ui.INavigationLocation#mergeInto(org.eclipse.ui.INavigationLocation)
     * @since 4.0
     */
    public boolean mergeInto(final INavigationLocation location) {
        return false;
    }

    /**<p>
     * Does nothing.
     * </p>
     * @see org.eclipse.ui.INavigationLocation#restoreLocation()
     * @since 4.0
     */
    public void restoreLocation() {
        ((ModelEditor)getEditorPart()).openModelObject(this.obj);
    }

    /**<p>
     * Does nothing.
     * </p>
     * @see org.eclipse.ui.INavigationLocation#restoreState(org.eclipse.ui.IMemento)
     * @since 4.0
     */
    public void restoreState(final IMemento memento) {
    }

    /**<p>
     * Does nothing.
     * </p>
     * @see org.eclipse.ui.INavigationLocation#saveState(org.eclipse.ui.IMemento)
     * @since 4.0
     */
    public void saveState(final IMemento memento) {
    }

    /**<p>
     * Does nothing.
     * </p>
     * @see org.eclipse.ui.INavigationLocation#update()
     * @since 4.0
     */
    public void update() {
    }
}
