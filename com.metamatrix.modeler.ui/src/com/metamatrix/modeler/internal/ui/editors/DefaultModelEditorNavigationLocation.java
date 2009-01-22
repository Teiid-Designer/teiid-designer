/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.NavigationLocation;
import org.eclipse.ui.ide.IDE;

/**
 * DefaultModelEditorNavigationLocation
 */
public class DefaultModelEditorNavigationLocation extends NavigationLocation {

    // ==============================
    //  Variables
    // ==============================    
    private IMarker imMarker;
    private IEditorPart editor;
    private String delegateText;

    /**
     * Construct an instance of DefaultModelEditorNavigationLocation.
     * @param editorPart
     */
    public DefaultModelEditorNavigationLocation( final IEditorPart editor, final IMarker imMarker ) {
        super( editor );
        this.editor = editor;
        this.imMarker = imMarker;
    }

    public void setMarker( IMarker imMarker ) {
        this.imMarker = imMarker;
    }

    public IMarker getMarker() {
        return imMarker;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.INavigationLocation#restoreLocation()
     */
    public void restoreLocation() {
        
        if ( editor == null ) {
//            System.out.println("[DefaultModelEditorNavigationLocation.restoreLocation] Editor is NULL, will NOT call gotoMarker"); //$NON-NLS-1$
        } else {        
//            System.out.println("[DefaultModelEditorNavigationLocation.restoreLocation] About to call gotoMarker on editor: " + editor.getClass().getName() ); //$NON-NLS-1$
			IDE.gotoMarker(editor, imMarker);
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.INavigationLocation#saveState(org.eclipse.ui.IMemento)
     */
    public void saveState(IMemento arg0) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.INavigationLocation#restoreState(org.eclipse.ui.IMemento)
     */
    public void restoreState(IMemento arg0) {

    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.INavigationLocation#mergeInto(org.eclipse.ui.INavigationLocation)
     */
    public boolean mergeInto(INavigationLocation arg0) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.INavigationLocation#update()
     */
    public void update() {

    }

    @Override
    public String getText() {
        if( this.delegateText == null )
            return super.getText();
        
        return this.delegateText;
    }
    

    public void setText(String text) {
        this.delegateText = text;
    }
}
