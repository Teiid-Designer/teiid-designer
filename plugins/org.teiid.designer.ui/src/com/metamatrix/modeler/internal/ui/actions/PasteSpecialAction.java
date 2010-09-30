/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import java.util.ArrayList;
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
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.IPasteSpecialContributor;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * The <code>PasteSpecialAction</code> class is the action that handles the global PasteSpecial.
 * 
 * @since 4.0
 */
public class PasteSpecialAction extends ModelObjectAction implements UiConstants.ExtensionPoints.MetadataPasteSpecialExtension {

    private static List<IPasteSpecialContributor> extensionList;

    public static List<IPasteSpecialContributor> getPasteSpecialContributors() {
        if (PasteSpecialAction.extensionList == null) {
            PasteSpecialAction.extensionList = new ArrayList<IPasteSpecialContributor>();

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
                            IPasteSpecialContributor action = (IPasteSpecialContributor)extension;
                            String label = elements[j].getAttribute(LABEL);
                            String description = elements[j].getAttribute(UiConstants.ExtensionPoints.MetadataPasteSpecialExtension.DESCRIPTION);
                            PasteSpecialAction.extensionList.add(new PasteSpecialDescriptor(action, label, description));
                        }
                    } catch (Exception e) {
                        // catch any Exception that occurs initializing the contributions so that
                        // it can be removed and others function normally
                        UiConstants.Util.log(IStatus.ERROR, e, e.getClass().getName());
                    }
                }
            }
        }

        return PasteSpecialAction.extensionList;
    }

    private boolean editorIsOpening = false;
    private List<IPasteSpecialContributor> enabledExtensionList;
    private IFile modelFile;
    private EObject selectedEObject;
    private ISelection tempSelection;

    public PasteSpecialAction() {
        super(UiPlugin.getDefault());
    }

    /**
     * determine if there are any extensions that can handle the selection and, if so, enable.
     */
    private void determineEnablement( IWorkbenchPart thePart,
                                      ISelection theSelection ) {
        boolean enable = false;
        this.selectedEObject = null;
        this.modelFile = null;

        if (SelectionUtilities.isSingleSelection(theSelection) && !PasteSpecialAction.getPasteSpecialContributors().isEmpty()) {
            this.enabledExtensionList = new ArrayList<IPasteSpecialContributor>();
            Object o = SelectionUtilities.getSelectedObject(getSelection());

            if (o instanceof EObject) {
                this.selectedEObject = (EObject)o;
            } else if ((o instanceof IFile) && ModelUtilities.isModelFile((IFile)o)) {
                this.modelFile = (IFile)o;
            }

            // see if we have a valid selection before checking the contributors
            if ((this.selectedEObject != null) || (this.modelFile != null)) {
                // set the selection on the contributors build a list of the ones that can paste
                for (IPasteSpecialContributor action : PasteSpecialAction.getPasteSpecialContributors()) {
                    action.selectionChanged(thePart, theSelection);

                    if (action.canPaste()) {
                        enable = true;
                        this.enabledExtensionList.add(action);
                    }
                }

                if (!enable) {
                    this.modelFile = null;
                }
            }
        }

        setEnabled(enable);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#doRun()
     */
    @Override
    protected void doRun() {
        if (this.enabledExtensionList.size() == 1) {
            ((IPasteSpecialContributor)this.enabledExtensionList.get(0)).run(this);
        }

        // swjTODO; handle multiple enabled contributors
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.actions.AbstractAction#getSelection()
     */
    @Override
    public ISelection getSelection() {
        if (this.editorIsOpening && this.tempSelection != null) return this.tempSelection;
        return super.getSelection();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#preRun()
     */
    @Override
    protected boolean preRun() {
        if (requiresEditorForRun()) {
            this.editorIsOpening = true;
            this.tempSelection = getSelection(); // cache current selection as opening editor may change selection

            if (this.selectedEObject != null) {
                if (!ModelEditorManager.isOpen(this.selectedEObject)) {
                    ModelEditorManager.open(this.selectedEObject, true);
                }
            } else if (this.modelFile != null) {
                ModelEditorManager.activate(this.modelFile, true);
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart thePart,
                                  ISelection theSelection ) {
        super.selectionChanged(thePart, theSelection);
        determineEnablement(thePart, theSelection);
    }

}

class PasteSpecialDescriptor implements IPasteSpecialContributor {

    public IPasteSpecialContributor delegate;
    public String description;
    public String label;

    public PasteSpecialDescriptor( IPasteSpecialContributor contributor,
                                   String label,
                                   String description ) {
        this.label = label;
        this.description = description;
        this.delegate = contributor;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.actions.IPasteSpecialContributor#canPaste()
     */
    @Override
    public boolean canPaste() {
        return this.delegate.canPaste();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    @Override
    public void dispose() {
        this.delegate.dispose();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    @Override
    public void init( IWorkbenchWindow window ) {
        this.delegate.init(window);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run( IAction action ) {
        this.delegate.run(action);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IAction action,
                                  ISelection selection ) {
        this.delegate.selectionChanged(action, selection);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        this.delegate.selectionChanged(part, selection);
    }

}
