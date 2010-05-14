/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.editors;

import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBookView;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator;
import com.metamatrix.modeler.internal.ui.outline.ModelOutlinePage;
import com.metamatrix.modeler.internal.ui.views.ModelViewer;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * <p>
 * ModelEditorSelectionSynchronizer monitors selection in the WorkbenchWindow to synchronize selection of Model Objects (EObjects)
 * with the appropriate ModelEditor and it's ModelEditorPages. This class also provides double-click control of ModelEditor for
 * model objects.
 * </p>
 * <p>
 * The Eclipse UI handles launching editors from IResource objects such as model files and selection synchronization between the
 * editor and it's corresponding IResource. It is up to this class to handle selection and double-click functionality for objects
 * within the model IResource.
 * </p>
 * <p>
 * Every ModelEditor has a SelecitonSynchronizer, and synchronization is performed only by the instance whose ModelEditor contains
 * the appropriate model.
 * </p>
 */
public class ModelEditorSelectionSynchronizer implements
                                             IPartListener,
                                             ISelectionListener,
                                             ISelectionProvider,
                                             IDoubleClickListener,
                                             UiConstants {

    // =========================================================
    // Static

    /**
     * Static method for ModelViewers to kick off editors from outside the internal editors framework.
     */
    public static void handleDoubleClick(DoubleClickEvent event) {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        Object object = selection.getFirstElement();
        if (object != null) {
            IFile file = null;

            if (object instanceof EObject) {
                // get the model file corresponding to the target
                ModelResource mdlRsrc = ModelUtilities.getModelResourceForModelObject((EObject)object);
                if (mdlRsrc != null) {
                    file = (IFile)mdlRsrc.getResource();
                }
            }

            if (file != null) {
                IWorkbenchPage page = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
                if (page != null) {
                    // look through the open editors and see if there is one available for this model file.
                    IEditorReference[] editors = page.getEditorReferences();
                    for (int i = 0; i < editors.length; ++i) {
                        IEditorPart editor = editors[i].getEditor(false);
                        if (editor != null) {
                            IEditorInput input = editor.getEditorInput();
                            if (input instanceof IFileEditorInput) {
                                if (file.equals(((IFileEditorInput)input).getFile())) {
                                    // found the correct editor - no need to do anything, it's synchronizer will handle
                                    return;
                                }
                            }
                        }
                    }

                    // there is no editor open for this object. Open one and hand it the double-click target.
                    try {

                        IEditorPart editor = IDE.openEditor(page, file);
                        if (editor instanceof ModelEditor) {
                            // pass on the double-click, since it's synchronizer wasn't alive to hear it.
                            ((ModelEditor)editor).openModelObject(object);
                        }

                    } catch (PartInitException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Static method to determine if there are any ModelEditorSelectionSynchronzer instances available to handle the double-click
     * of an arbitrary EObject from a ModelViewer.
     * 
     * @param target
     *            the EObject that was double-clicked
     * @param instance
     *            the instance of this class that wants to know if it should process the double- click through it's editor.
     * @return true if the instance should handle the double-click
     */
    private static boolean shouldHandleDoubleClick(Object target,
                                                   ModelEditorSelectionSynchronizer instance) {
        // get the model file corresponding to the target
        IFile file = null;
        ModelResource modelResrc = null;

        if (target instanceof EObject) {
            modelResrc = ModelUtilities.getModelResourceForModelObject((EObject)target);
        } else if (target instanceof Resource) {
            modelResrc = ModelerCore.getModelWorkspace().findModelResource((Resource)target);
        } else if (target instanceof ModelResource) {
            modelResrc = (ModelResource)target;
        }
        if (modelResrc == null) {
            Util.log(IStatus.WARNING, Util.getString("ModelEditorSelectionSynchronizer.invalidTarget")); //$NON-NLS-1$
            return false;
        }
        file = (IFile)modelResrc.getResource();

        // if the target is inside the synchronizer instance's model file, then handle double-click
        if (instance.modelFile.equals(file)) {

            // make sure the instance part is the active editor; if not, activate it
            IWorkbenchPage page = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
            if (page != null && page.getActiveEditor() != instance.modelEditor) {
                page.bringToTop(instance.modelEditor);
            }

            // tell the calling instance to go ahead and handle the double-click
            return true;
        }
        // look through the open editors and see if there is one available for this model file.
        IEditorReference[] editors = instance.modelEditor.getEditorSite().getPage().getEditorReferences();
        for (int i = 0; i < editors.length; ++i) {
            IEditorPart editor = editors[i].getEditor(false);
            if (editor != null) {
                IEditorInput input = editor.getEditorInput();
                if (input instanceof IFileEditorInput) {
                    if (file.equals(((IFileEditorInput)input).getFile())) {
                        // found the correct editor - no need to do anything, it's synchronizer will handle
                        return false;
                    }
                }
            }
        }

        // there is no editor open for this object. Open one and hand it the double-click target.
        try {

            IEditorPart editor = IDE.openEditor(instance.modelEditor.getEditorSite().getPage(), file);

            if (editor instanceof ModelEditor) {
                // pass on the double-click, since it's synchronizer wasn't alive to hear it.
                ((ModelEditor)editor).openModelObject(target);
            }

        } catch (PartInitException e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================================================
    // Variables

    private ModelEditor modelEditor;
    private IFile modelFile;
    private ISelection lastSelection;
    private ArrayList registeredViewers = new ArrayList();

    // =========================================================
    // Constructors

    /**
     * Construct an instance of ModelEditorSelectionSynchronizer.
     * 
     * @param editor
     *            the ModelEditor that this instance will be synchronizing across pages and views.
     * @param model
     *            the Resource that corresponds to the model that this editor is editing.
     */
    ModelEditorSelectionSynchronizer(ModelEditor editor,
                                     IFile editorInput) {
        this.modelEditor = editor;
        this.modelFile = editorInput;
        editor.getSite().getWorkbenchWindow().getPartService().addPartListener(this);
    }

    // =========================================================
    // Methods

    /**
     * When an IWorkbenchPart is activated, this method checks to see if the part is, or contains a ModelViewer. If so, it hooks
     * up the ModelViewer to enable it to control the correct ModelEditor and be synchronized with other ModelViewers in the
     * workbench.
     * 
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    public synchronized void partActivated(IWorkbenchPart part) {
        if (part instanceof ModelViewer) {
            register((ModelViewer)part);
        } else if (part instanceof PageBookView) {
            PageBookView pbv = (PageBookView)part;
            IPage page = pbv.getCurrentPage();
            if (page instanceof ModelViewer) {
                register((ModelViewer)page);
            } else if (page instanceof ModelOutlinePage) {
                // go one more level to pick up any contributed outline pages
                Object innerPage = ((ModelOutlinePage)page).getCurrentViewer();
                if (innerPage instanceof ModelViewer) {
                    register((ModelViewer)innerPage);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     */
    public void partBroughtToTop(IWorkbenchPart part) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     */
    public void partClosed(IWorkbenchPart part) {
        if (part instanceof ModelViewer) {
            unregister((ModelViewer)part);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partDeactivated(IWorkbenchPart part) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     */
    public void partOpened(IWorkbenchPart part) {
        if (part instanceof ModelViewer) {
            register((ModelViewer)part);
        } else if (part instanceof PageBookView) {
            PageBookView pbv = (PageBookView)part;
            IPage page = pbv.getCurrentPage();
            if (page instanceof ModelViewer) {
                register((ModelViewer)page);
            }
        }
    }

    /**
     * Called by the SelectionService to indicate that a selection has changed in the workbench. Responds by determining if the
     * selection is an EObject and, if so, sends the selection to this synchronizer's ModelEditor
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part,
                                 ISelection selection) {
        final boolean startedTxn = ModelerCore.startTxn(false, false, null, this);
        try {
            // selection changed is only sent to the ModelEditorPage source IWorkbenchPart
            //   the active part in the workbench, and the part is not the ModelEditor itself.
            if (isActivePart(part) && !(part instanceof ModelEditor)) {

                // if the selection is a single EObject...
                if (selection instanceof IStructuredSelection) {
                    if (((IStructuredSelection)selection).size() == 1) {
                        Object o = ((IStructuredSelection)selection).getFirstElement();
                        if (o instanceof EObject) {

                            lastSelection = selection;
                            // ... then send it to the current editor page's SelectionChangedListener
                            ModelEditorPage page = (ModelEditorPage)this.modelEditor.getCurrentPage();
                            if (page != null && page.getModelObjectSelectionChangedListener() != null) {
                                page.getModelObjectSelectionChangedListener()
                                    .selectionChanged(new SelectionChangedEvent(this, selection));
                            }
                            modelEditor.setSelection(selection);
                        }
                    } else if( SelectionUtilities.isMultiSelection(selection)) {
                        int nObjects = SelectionUtilities.getSelectedObjects(selection).size(); 
                        int nEObjects = SelectionUtilities.getSelectedEObjects(selection).size();
                        if( nObjects == nEObjects ) {
                            lastSelection = selection;
                            // ... then send it to the current editor page's SelectionChangedListener
                            ModelEditorPage page = (ModelEditorPage)this.modelEditor.getCurrentPage();
                            if (page != null && page.getModelObjectSelectionChangedListener() != null) {
                                page.getModelObjectSelectionChangedListener()
                                    .selectionChanged(new SelectionChangedEvent(this, selection));
                            }
                            modelEditor.setSelection(selection);
                        }
                    } else if( SelectionUtilities.isEmptySelection(selection)) {
                        ModelEditorPage page = (ModelEditorPage)this.modelEditor.getCurrentPage();
                        if (page != null && page.getModelObjectSelectionChangedListener() != null) {
                            page.getModelObjectSelectionChangedListener()
                                .selectionChanged(new SelectionChangedEvent(this, selection));
                        }
                        modelEditor.setSelection(selection);
                    }
                }
            }
        } finally {
            if (startedTxn) {
                ModelerCore.commitTxn();
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        return lastSelection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        // functionality not provided -
        //    this class implements ISelectionProvider only to be the source of SelectionChangeEvent
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        // functionality not provided -
        //    this class implements ISelectionProvider only to be the source of SelectionChangeEvent
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {
        lastSelection = selection;
        selectionChanged(this.modelEditor, selection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     */
    public void doubleClick(DoubleClickEvent event) {
        // first, get the object out of the event and make sure this is the correct editor
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        Object object = selection.getFirstElement();
        if (object != null) {
            if (object instanceof EObject) {
                // check with the static handler to make sure this is the right editor
                if (shouldHandleDoubleClick(object, this)) {
                    // Changed per defect 16869  Need to force a change in the
                    // object in the t-editor if open already
                    // Replaced the call below with a ModelEditorManager call.
                    // modelEditor.openModelObject(object);
                    ModelEditorManager.open((EObject)object, false, UiConstants.ObjectEditor.REFRESH_EDITOR_IF_OPEN);

                }

            }
        }
    }

    protected synchronized void register(ModelViewer viewer) {
        if (!registeredViewers.contains(viewer)) {
            // hook up selection if this is a new ModelViewer
            if (!(viewer instanceof ModelExplorerResourceNavigator))
                viewer.addModelObjectDoubleClickListener(this);
            registeredViewers.add(viewer);
        }
    }

    protected synchronized void unregister(ModelViewer viewer) {
        // unhook this ModelViewer
        viewer.removeModelObjectDoubleClickListener(this);
        registeredViewers.remove(viewer);
    }

    public void dispose() {
        Object[] viewArray = registeredViewers.toArray();
        for (int i = 0; i < viewArray.length; ++i) {
            unregister((ModelViewer)viewArray[i]);
        }
        this.modelEditor.getSite().getWorkbenchWindow().getPartService().removePartListener(this);
    }

    private boolean isActivePart(IWorkbenchPart part) {
        return part == modelEditor.getEditorSite().getWorkbenchWindow().getPartService().getActivePart();
    }

}
