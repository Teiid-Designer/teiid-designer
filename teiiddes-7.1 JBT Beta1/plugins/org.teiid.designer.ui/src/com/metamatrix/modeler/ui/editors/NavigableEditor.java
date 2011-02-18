/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.INavigationHistory;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.INavigationLocationProvider;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.EditorPart;
import com.metamatrix.modeler.internal.ui.editors.DefaultModelEditorNavigationLocation;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * NavigableEditor
 */
public class NavigableEditor extends EditorPart implements INavigationLocationProvider, IGotoMarker {

    private IEditorPart iepEditor;

    /**
     * Construct an instance of NavigableEditor.
     */
    public NavigableEditor( IEditorPart iepEditor ) {
        super();
        this.iepEditor = iepEditor;
    }

    /**
     * Construct an instance of NavigableEditor.
     */
    public NavigableEditor() {
        super();
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( IProgressMonitor monitor ) {

    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    @Override
    public void doSaveAs() {

    }

    /**
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     */
    public void gotoMarker( IMarker marker ) {

        System.out.println("[NavigableEditor.gotoMarker] TOP"); //$NON-NLS-1$

        if (iepEditor != null) {
            //            System.out.println("[NavigableEditor.gotoMarker] HAS editor case, class / input: " + iepEditor.getClass().getName() + " / " + iepEditor.getEditorInput().getName() ); //$NON-NLS-1$
            IDE.gotoMarker(iepEditor, marker);
        } else {
            //            System.out.println("[NavigableEditor.gotoMarker] IS editor case, class / input: " + getClass().getName() + " / " + getEditorInput().getName() ); //$NON-NLS-1$
            gotoMarker(marker);
        }
    }

    /**
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    @SuppressWarnings( "unused" )
    public void init( IEditorSite site,
                      IEditorInput input ) throws PartInitException {

    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return false;
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {

    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {

    }

    /**
     * @see org.eclipse.ui.INavigationLocationProvider#createEmptyNavigationLocation()
     */
    public INavigationLocation createEmptyNavigationLocation() {
        return null;
    }

    /**
     * @see org.eclipse.ui.INavigationLocationProvider#createNavigationLocation()
     */
    public INavigationLocation createNavigationLocation() {
        if (iepEditor != null && iepEditor instanceof INavigationSupported) {

            IMarker imMarker = ((INavigationSupported)iepEditor).createMarker();
            return new DefaultModelEditorNavigationLocation(iepEditor, imMarker);
        }
        if (this instanceof INavigationSupported) {

            IMarker imMarker = ((INavigationSupported)this).createMarker();
            return new DefaultModelEditorNavigationLocation(this, imMarker);
        }
        return null;
    }

    public int getNavHistoryCount() {
        int iCount = 0;

        INavigationHistory inh = UiUtil.getWorkbenchPage().getNavigationHistory();

        INavigationLocation[] nheHistoryEntries = inh.getLocations();
        iCount = nheHistoryEntries.length;

        return iCount;
    }

}
