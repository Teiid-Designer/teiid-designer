/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
