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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.IPasteSpecialContributor;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * The <code>PasteSpecialAction</code> class is the action that handles the global PasteSpecial.
 * @since 4.0
 */
public class PasteSpecialAction
    extends ModelObjectAction
    implements UiConstants.ExtensionPoints.MetadataPasteSpecialExtension {

    //============================================================================================================================
    // Constants

//    private static final String PROBLEM = "PasteSpecialAction.problem"; //$NON-NLS-1$
//    private static final String UNDO_TEXT = "PasteSpecialAction.undoText"; //$NON-NLS-1$
//    private static final String PLURAL_UNDO_TEXT = "PasteSpecialAction.pluralUndoText"; //$NON-NLS-1$

    //============================================================================================================================
    // Fields

    /** The child type descriptor. */
    private ModelResource modelResource;
    private boolean editorIsOpening = false;
    private EObject selectedEObject;
    private ISelection tempSelection;
    private List extensionList;
    private List enabledExtensionList;

    //============================================================================================================================
    // Constructors

    public PasteSpecialAction() {
        super(UiPlugin.getDefault());
        buildExtensionList();
    }

    //============================================================================================================================
    // Methods

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        determineEnablement(thePart, theSelection);
    }

    @Override
    protected void doRun() {
        if ( enabledExtensionList.size() == 1 ) {
            ((IPasteSpecialContributor) enabledExtensionList.get(0)).run(this);
        }
        
        //swjTODO; handle multiple enabled contributors
    }

    /**
     * determine if there are any extensions that can handle the selection and, if so, enable. 
     */
    private void determineEnablement(IWorkbenchPart thePart, ISelection theSelection) {
        boolean enable = false;
        selectedEObject = null;
        modelResource = null;
        if ( extensionList != null && !extensionList.isEmpty() ) {
            enabledExtensionList.clear();
            if ( !isEmptySelection() && !isMultiSelection() ) {
                Object o = SelectionUtilities.getSelectedObject(getSelection());
                if ( o instanceof EObject ) {
                    if ( isReadOnly() || canLegallyEditResource() ) {
                        selectedEObject = (EObject) o;
                    }
                        
                } else if ( (o instanceof IFile) && ModelUtilities.isModelFile((IFile) o)) {
                    try {
                        modelResource = ModelUtilities.getModelResource((IFile) o, false);
                    } catch (ModelWorkspaceException e) {
                        UiConstants.Util.log(e);
                    }
                }
                
                // see if we have a valid selection before checking the contributors
                if ( selectedEObject != null || modelResource != null ) {

                    // set the selection on the contributors build a list of the ones that can paste
                    Iterator iter = extensionList.iterator();
                    while (iter.hasNext()) {
                        IPasteSpecialContributor action = (IPasteSpecialContributor)iter.next();
                        action.selectionChanged(thePart, theSelection);
                        if ( action.canPaste() ) {
                            enable = true;
                            enabledExtensionList.add(action);
                        }
                    }
                }
            }
        }
        
        if (!enable) {
            modelResource = null;
        }

        setEnabled(enable);
    }

    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling
     * the run at the last minute.
     * This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        if (requiresEditorForRun()) {
            if (selectedEObject != null) {
                editorIsOpening = true;
                tempSelection = getSelection();
                if (!ModelEditorManager.isOpen(selectedEObject))
                    ModelEditorManager.open(selectedEObject, true);
            } else if (modelResource != null) {
                editorIsOpening = true;
                tempSelection = getSelection();
                ModelEditorManager.activate(modelResource, true);
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }

    /**<p>
     * </p>
     * @see com.metamatrix.ui.actions.AbstractAction#getSelection()
     * @since 4.0
     */
    @Override
    public ISelection getSelection() {
        if (editorIsOpening && tempSelection != null)
            return tempSelection;
        return super.getSelection();
    }

    /**
     * Populate this object's list of contributions to the Metadata Paste Special 
     * extension point.
     */
    private void buildExtensionList() {
        extensionList = new ArrayList();
        enabledExtensionList = new ArrayList(3);
        
        // get the ModelEditorPage extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, ID);
        // get the all extensions to the ModelEditorPage extension point
        IExtension[] extensions = extensionPoint.getExtensions();

        // make executable extensions for every CLASSNAME
        for (int i = extensions.length - 1; i >= 0; --i) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            Object extension = null;
            for (int j = 0; j < elements.length; ++j) {
                try {
                    extension = elements[j].createExecutableExtension(CLASSNAME);
                    if (extension instanceof IPasteSpecialContributor) {
                        IPasteSpecialContributor action = (IPasteSpecialContributor) extension;
                        String label = elements[j].getAttribute(LABEL);
                        String description = elements[j].getAttribute(UiConstants.ExtensionPoints.MetadataPasteSpecialExtension.DESCRIPTION);
                        extensionList.add(new PasteSpecialDescriptor(action, label, description));
                    }
                } catch (Exception e) {
                    // catch any Exception that occurs initializing the contributions so that
                    //    it can be removed and others function normally
                    UiConstants.Util.log(IStatus.ERROR, e, e.getClass().getName());
                }
            }
        }
    }

}

class PasteSpecialDescriptor implements IPasteSpecialContributor {
    
    public IPasteSpecialContributor delegate;
    public String label;
    public String description;
    
    public PasteSpecialDescriptor(IPasteSpecialContributor contributor, String label, String description) {
        this.label = label;
        this.description = description;
        this.delegate = contributor;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IPasteSpecialContributor#canPaste()
     */
    public boolean canPaste() {
        return delegate.canPaste();
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
