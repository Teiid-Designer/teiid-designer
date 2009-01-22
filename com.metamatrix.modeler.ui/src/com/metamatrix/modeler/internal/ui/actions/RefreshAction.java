/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.IRefreshContributor;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * The <code>RefreshAction</code> class is the action that handles the global Refresh of metadata models.
 * @since 4.0
 */
public class RefreshAction 
    implements IWorkbenchWindowActionDelegate, UiConstants.ExtensionPoints.MetadataRefreshExtension {


    //============================================================================================================================
    // Fields

    /** The child type descriptor. */
    private ModelResource modelResource;
    private List extensionList;
    private List enabledExtensionList;

    //============================================================================================================================
    // Constructors

    public RefreshAction() {
        buildExtensionList();
    }

    //============================================================================================================================
    // Methods

    /**
     * determine if there are any extensions that can handle the selection and, if so, enable. 
     */
    private void determineEnablement(IAction theAction, ISelection theSelection) {
        boolean enable = false;
        modelResource = null;
        if ( extensionList != null && !extensionList.isEmpty() ) {
            enabledExtensionList.clear();
            if ( SelectionUtilities.isSingleSelection(theSelection) ) {
                Object o = SelectionUtilities.getSelectedObject(theSelection);
                if ( (o instanceof IFile) && ModelUtilities.isModelFile((IFile) o)) {
                    try {
                        modelResource = ModelUtilities.getModelResource((IFile) o, false);
                    } catch (ModelWorkspaceException e) {
                        UiConstants.Util.log(e);
                    }
                }
                
                // see if we have a valid selection before checking the contributors
                if ( modelResource != null ) {

                    // set the selection on the contributors build a list of the ones that can paste
                    Iterator iter = extensionList.iterator();
                    while (iter.hasNext()) {
                        IRefreshContributor action = (IRefreshContributor)iter.next();
                        action.selectionChanged(theAction, theSelection);
                        if ( action.canRefresh() ) {
                            enable = true;
                            enabledExtensionList.add(action);
                        }
                    }
                }
            }
        }
        
        theAction.setEnabled(enable);
    }

    /**
     * Populate this object's list of contributions to the Metadata Paste Special 
     * extension point.
     */
    private void buildExtensionList() {
        extensionList = new ArrayList();
        enabledExtensionList = new ArrayList(3);
        
        // get the ModelEditorPage extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(ID);
        // get the all extensions to the ModelEditorPage extension point
        IExtension[] extensions = extensionPoint.getExtensions();

        // make executable extensions for every CLASSNAME
        for (int i = extensions.length - 1; i >= 0; --i) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            Object extension = null;
            for (int j = 0; j < elements.length; ++j) {
                try {
                    extension = elements[j].createExecutableExtension(CLASSNAME);
                    if (extension instanceof IRefreshContributor) {
                        IRefreshContributor action = (IRefreshContributor) extension;
                        String label = elements[j].getAttribute(LABEL);
                        String description = elements[j].getAttribute(UiConstants.ExtensionPoints.MetadataPasteSpecialExtension.DESCRIPTION);
                        extensionList.add(new RefreshDescriptor(action, label, description));
                    }
                } catch (Exception e) {
                    // catch any Exception that occurs initializing the contributions so that
                    //    it can be removed and others function normally
                    UiConstants.Util.log(IStatus.ERROR, e, e.getClass().getName());
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
        Iterator iter = extensionList.iterator();
        while ( iter.hasNext() ) {
            IRefreshContributor irc = (IRefreshContributor) iter.next();
            irc.init(window);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
        Iterator iter = extensionList.iterator();
        while ( iter.hasNext() ) {
            IRefreshContributor irc = (IRefreshContributor) iter.next();
            irc.dispose();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        if ( enabledExtensionList.size() == 1 ) {
            ((IRefreshContributor) enabledExtensionList.get(0)).run(action);
        }
        //swjTODO; handle multiple enabled contributors
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        determineEnablement(action, selection);
    }

}

class RefreshDescriptor implements IRefreshContributor {
    
    public IRefreshContributor delegate;
    public String label;
    public String description;
    
    public RefreshDescriptor(IRefreshContributor contributor, String label, String description) {
        this.label = label;
        this.description = description;
        this.delegate = contributor;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IPasteSpecialContributor#canPaste()
     */
    public boolean canRefresh() {
        return delegate.canRefresh();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
        delegate.dispose();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
        delegate.init(window);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        delegate.run(action);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        delegate.selectionChanged(action, selection);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        delegate.selectionChanged(part, selection);
    }

}
