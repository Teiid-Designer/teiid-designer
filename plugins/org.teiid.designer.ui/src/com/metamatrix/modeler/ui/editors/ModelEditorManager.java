/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.editors;

import static com.metamatrix.modeler.internal.ui.PluginConstants.Prefs.General.AUTO_OPEN_EDITOR_IF_NEEDED;
import static com.metamatrix.modeler.ui.UiConstants.Util;
import static com.metamatrix.modeler.ui.UiConstants.Extensions.MODEL_EDITOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

/**
 * ModelEditorManager is a class of static utility methods for easily focusing on an object in the correct ModelEditor, or editing
 * an object in the correct ModelObjectEditor.
 */
abstract public class ModelEditorManager {

    public static final String OPEN_EDITOR_TITLE = Util.getString("ModelEditorManager.openModelEditorTitle"); //$NON-NLS-1$
    public static final String OPEN_EDITOR_MESSAGE = Util.getString("ModelEditorManager.openModelEditorMessage"); //$NON-NLS-1$
    private static final String ALWAY_FORCE_OPEN_MESSAGE = Util.getString("ModelEditorManager.alwaysForceOpenMessage"); //$NON-NLS-1$

    private static final String READ_ONLY_TITLE = Util.getString("ModelEditorManager.alwaysForceOpenMessage"); //$NON-NLS-1$
    private static final String READ_ONLY_MESSAGE = Util.getString("ModelEditorManager.alwaysForceOpenMessage"); //$NON-NLS-1$

    static final String VR_MSG = Util.getString("ModelEditor.virtualRelationalNotLicensedMessage"); //$NON-NLS-1$
    static final String XML_MSG = Util.getString("ModelEditor.xmlNotLicensedMessage"); //$NON-NLS-1$

    private static final ModelerUndoManager undoManager = ModelerUndoManager.getInstance();

    static IEditorPart staticEditor;

    /**
     * Activates the ModelEditor for the specified model file and brings it to the front of the active Workbench's Editor Site.
     * 
     * @param modelFile the file that should be displayed in the ModelEditor.
     * @param forceOpen if true and there is no ModelEditor open for the specified file, then a new ModelEditor will be created
     *        and opened in the Editor Site.
     */
    public static void activate( final IFile modelFile,
                                 final boolean forceOpen ) {
        activate(modelFile, forceOpen, true);
    }

    /**
     * Activates the ModelEditor for the specified model file and brings it to the front of the active Workbench's Editor Site.
     * 
     * @param modelFile the file that should be displayed in the ModelEditor.
     * @param forceOpen if true and there is no ModelEditor open for the specified file, then a new ModelEditor will be created
     *        and opened in the Editor Site.
     * @param async if true, execute an asyncExec. If false, use syncExec.
     */
    public static void activate( final IFile modelFile,
                                 final boolean forceOpen,
                                 boolean async ) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                final ModelEditor modelEditor = getModelEditorForFile(modelFile, forceOpen);
                if (modelEditor != null) {
                    modelEditor.setFocus();
                }
            }
        };

        if (async) {
            Display.getDefault().asyncExec(r);
        } else {
            Display.getDefault().syncExec(r);
        } // endif
    }

    /**
     * Activates the ModelEditor for the specified ModelResource and brings it to the front of the active Workbench's Editor Site.
     * 
     * @param modelResource the ModelResoruce that should be displayed in the ModelEditor.
     * @param forceOpen if true and there is no ModelEditor open for the specified ModelResource, then a new ModelEditor will be
     *        created and opened in the Editor Site.
     */
    public static void activate( final ModelResource modelResource,
                                 final boolean forceOpen ) {
        activate(modelResource, forceOpen, false);
    }

    /**
     * Activates the ModelEditor for the specified ModelResource and brings it to the front of the active Workbench's Editor Site.
     * Required by Defect 19537 in order to allow actions & workers the ability to auto-open editors, but still keep focus to do
     * additional work, like renaming in tree, etc.
     * 
     * @param modelResource the ModelResoruce that should be displayed in the ModelEditor.
     * @param forceOpen if true and there is no ModelEditor open for the specified ModelResource, then a new ModelEditor will be
     *        created and opened in the Editor Site.
     * @param maintainActivePart if true, cache the active part prior to getting model editor, then reset active page when
     *        finished
     */
    public static void activate( final ModelResource modelResource,
                                 final boolean forceOpen,
                                 final boolean maintainActivePart ) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                IWorkbenchPart activePart = null;
                IWorkbenchPage activePage = null;

                if (maintainActivePart) {
                    activePage = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    if (activePage != null) {
                        activePart = activePage.getActivePart();
                    }
                }
                final IFile file = (IFile)modelResource.getResource();
                final ModelEditor modelEditor = getModelEditorForFile(file, forceOpen);
                if (modelEditor != null) {
                    activate(modelEditor);
                }
                if (activePart != null) {
                    // Reset active page to re-focus it.
                    activePage.activate(activePart);
                }
            }
        });
    }

    public static void activate( final ModelEditor editor ) {
        // make sure the instance part is the active editor; if not, activate it
        IWorkbenchPage page = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        page.bringToTop(editor);
    }

    public static void autoSelectEditor( final ModelEditor editor,
                                         final ModelEditorPage thePage ) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                for (Iterator iter = editor.getAllEditors().iterator(); iter.hasNext();) {
                    Object nextPage = iter.next();
                    if (nextPage == thePage) {
                        editor.selectPage(thePage);
                    }
                }
            }
        });
    }

    /**
     * In order for a model object to be edited, it's editor must be open. This method uses the user preference for auto-opening the
     * editor. If the preference is set to auto-open the editor is automatically opened. Otherwise, a dialog is opened asking the
     * user if they want to open the editor.
     * 
     * @param shell the shell to display the dialog (can be <code>null</code>)
     * @param eObject the model object whose editor is being requested to be opened (cannot be <code>null</code>)
     * @return <code>true</code> if the editor was opened
     */
    public static boolean autoOpen( Shell shell,
                                    EObject eObject,
                                    boolean showReadOnlyDialog ) {
        CoreArgCheck.isNotNull(eObject, "eObject is null"); //$NON-NLS-1$

        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);

        // if the modelResource is null, we can't edit the properties
        if (modelResource == null) {
            return false;
        }

        if (!ModelEditorManager.isOpen(eObject)) {
            // if readonly can't open
            if (ModelUtil.isIResourceReadOnly(modelResource.getResource())) {
                if (showReadOnlyDialog) {
                    MessageDialog.openError(shell, READ_ONLY_TITLE, READ_ONLY_MESSAGE);
                }

                return false;
            }

            // get preference value for auto-open-editor
            String autoOpen = UiPlugin.getDefault().getPreferenceStore().getString(AUTO_OPEN_EDITOR_IF_NEEDED);

            // if the preference is to auto-open, then set forceOpen so we don't prompt the user
            boolean forceOpen = MessageDialogWithToggle.ALWAYS.equals(autoOpen);

            if (!forceOpen) {
                MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoCancelQuestion(shell, OPEN_EDITOR_TITLE,
                                                                                                 OPEN_EDITOR_MESSAGE,
                                                                                                 ALWAY_FORCE_OPEN_MESSAGE, false,
                                                                                                 UiPlugin.getDefault()
                                                                                                         .getPreferenceStore(),
                                                                                                 AUTO_OPEN_EDITOR_IF_NEEDED);
                int result = dialog.getReturnCode();
                switch (result) {
                // yes, ok
                case IDialogConstants.YES_ID:
                case IDialogConstants.OK_ID:
                    forceOpen = true;
                    break;
                // no
                case IDialogConstants.NO_ID:
                    forceOpen = false;
                    break;
                }
            }

            if (forceOpen) {
                open(eObject, true);
                return true;
            }

            return false;
        }

        return true;
    }

    /**
     * Convenience method to send focus to the proper ModelEditor for a given object and open the correct ModelEditorPage for the
     * specified object.
     * 
     * @param object the EObject within a ModelResource that should be opened in the proper ModelEditor
     * @param forceOpen if true, the correct ModelEditor will be opened if one is not already open. if false, and a ModelEditor
     *        for this resource is not opened, one will not be opened and this method will return.
     */
    public static void open( final EObject object,
                             final boolean forceOpen ) {
        UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
            @Override
            public void run() {
                if (object != null) {
                    final ModelEditor modelEditor = getModelEditorForObject(object, forceOpen);
                    if (modelEditor != null) {

                        // set 'forceRefresh' to be the same value as 'forceOpen'; the theory is
                        // that if the user wanted to make sure the editor was created and opened
                        // they would also wish to get the highlighting refreshed; and if they did
                        // not wish to force the open they would not care about the refresn.
                        boolean forceRefresh = forceOpen;
                        if (modelEditor.openModelObject(object, forceRefresh)) {
                            activate(modelEditor);
                        }
                    }
                }
            }
        });
    }

    public static void openInEditMode( final Object input,
                                       final boolean forceOpen,
                                       int objectEditorValue ) {
        // Need to get the model annotation, if the input is NOT an eObject
        EObject theEObject = null;
        if (input instanceof EObject) {
            theEObject = (EObject)input;
        } else if (input instanceof IFile) {
            try {
                theEObject = ModelUtilities.getModelResourceForIFile((IFile)input, true).getModelAnnotation();
            } catch (ModelWorkspaceException err) {
                WidgetUtil.showError(err);
                Util.log(err);
            }
        } else if (input instanceof ModelResource) {
            try {
                theEObject = ((ModelResource)input).getModelAnnotation();
            } catch (ModelWorkspaceException err) {
                WidgetUtil.showError(err);
                Util.log(err);
            }
        }

        if (theEObject != null) {
            open(theEObject, true, objectEditorValue);
        }
    }

    /**
     * Convenience method to send focus to the proper ModelEditor for a given object and open the correct ModelEditorPage for the
     * specified object. Added integer value to open/close or update object editor with new object input.
     * 
     * @param object the EObject within a ModelResource that should be opened in the proper ModelEditor
     * @param forceOpen if true, the correct ModelEditor will be opened if one is not already open. if false, and a ModelEditor
     *        for this resource is not opened, one will not be opened and this method will return.
     */
    public static void open( final EObject object,
                             final boolean forceOpen,
                             final int objectEditorValue ) {
        UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
            @Override
            public void run() {
                if (object != null) {
                    final ModelEditor modelEditor = getModelEditorForObject(object, forceOpen);
                    if (modelEditor != null) {
                        if (modelEditor.openModelObject(object)) {
                            activate(modelEditor);
                            updateObjectEditor(modelEditor, object, objectEditorValue);
                            modelEditor.openComplete();
                        }
                    }
                }
            }
        });
    }

    /**
     * Method which creates and schedules a job specifically for opening a ModelEditor for a given object Since this is a
     * ModelEditor call, the job is placed on the SWT thread
     * 
     * @param object
     * @since 5.0.2
     */
    public static void openWithJob( final Object object ) {

        if (object != null) {
            openAndEditWithJob(object, null);
        }
    }

    /**
     * @param object
     * @param editableObject
     * @since 5.0.2
     */
    public static void openAndEditWithJob( final Object object,
                                           final EObject editableObject ) {
        if (object != null) {
            final ModelResource mr = ModelUtilities.getModelResource(object);
            if (mr != null) {
                try {
                    final OpenModelEditorJob openJob = new OpenModelEditorJob((IFile)mr.getCorrespondingResource(),
                                                                              editableObject);
                    UiUtil.runInSwtThread(new Runnable() {
                        @Override
                        public void run() {
                            openJob.schedule();
                        }
                    }, true);
                } catch (ModelWorkspaceException theException) {
                    Util.log(theException);
                }
            }
        }
    }

    /**
     * Determine if a ModelEditor is open for the specified model file.
     * 
     * @param modelFile
     * @return true if there is an editor open for the specified file.
     */
    public static boolean isOpen( final IFile modelFile ) {
        return (getModelEditorForFile(modelFile, false) != null);
    }

    /**
     * Determine if a ModelEditor is open for the specified model object.
     * 
     * @param modelObject
     * @return true if there is an editor open for the specified object.
     */
    public static boolean isOpen( final EObject modelObject ) {
        return (getModelEditorForObject(modelObject, false) != null);
    }

    /**
     * Determine if a ModelEditor is open and has been initialized for the specified model file.
     * 
     * @param modelFile
     * @return true if there is an editor open & initialized for the specified file.
     */
    public static boolean isOpenAndInitialized( final IFile modelFile ) {
        boolean result = false;
        ModelEditor editor = getModelEditorForFile(modelFile, false);
        if (editor != null && editor.hasInitialized()) {
            result = true;
        }
        return result;
    }

    /**
     * Programatically close a ModelEditor for the specified model file, if one exists.
     * 
     * @param modelFile
     * @param save true will request that the user to save or discard their changes. Should be set to true unless the modelFile is
     *        being deleted.
     * @return true if the editor closed successfully or there was no editor open for the specified modelFile. Will return false
     *         if the user aborted the close.
     */
    public static boolean close( final IFile modelFile,
                                 final boolean save ) {
        CloseEditorRunnable runnable = new CloseEditorRunnable(modelFile, save);
        Display.getDefault().syncExec(runnable);
        return runnable.didClose;
    }

    /**
     * Programatically save a ModelEditor for the specified model file, if one exists.
     * 
     * @param modelFile
     */
    public static boolean save( final IFile modelFile ) {
        boolean bSaveDone = false;
        ModelEditor me = getModelEditorForFile(modelFile, false);

        if (me != null) {
            bSaveDone = true;
            me.doSave(new NullProgressMonitor());
        } else {
            bSaveDone = false;
        }
        return bSaveDone;
    }

    /**
     * Determine if the specified model object can be opened in a ModelObjectEditorPane beneath the ModelEditor.
     * 
     * @param object
     * @return
     */
    public static boolean canEdit( final EObject object ) {
        if (object != null) {
            final ModelEditor modelEditor = getModelEditorForObject(object, false);
            if (modelEditor != null) {
                return modelEditor.canEditModelObject(object);
            }
        }
        return false;
    }

    /**
     * Open the specified model object in a ModelObjectEditorPane.
     * 
     * @param object
     * @return
     */
    public static void edit( final EObject object ) {
        edit(object, null);
    }

    /**
     * Open the specified model object in a ModelObjectEditorPane.
     * 
     * @param object
     * @param editorId the ID of the specific ModelObjectEditor
     * @return
     */
    public static void edit( final EObject object,
                             final String editorId ) {
        UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
            @Override
            public void run() {
                if (object != null) {
                    final ModelEditor modelEditor = getModelEditorForObject(object, true);
                    if (modelEditor != null) {
                        Object editableObject = modelEditor.getEditableObject(object);
                        if (editorId == null) {
                            if ((editableObject != null && editableObject instanceof EObject)) {
                                ModelEditorManager.open((EObject)editableObject,
                                                        false,
                                                        UiConstants.ObjectEditor.FORCE_OPEN_EDITOR);
                            } else ModelEditorManager.open(object, false, UiConstants.ObjectEditor.FORCE_OPEN_EDITOR);
                        } else {
                            if (modelEditor.openModelObject(object)) activate(modelEditor);
                            modelEditor.editModelObject(object, editorId);
                        }
                    }
                    // final ModelEditor modelEditor = getModelEditorForObject(object, true);
                    // if ( modelEditor != null ) {
                    // if ( modelEditor.openModelObject(object) ) {
                    // activate(modelEditor);
                    // // Check to see if no object editor exists or if exists and not editing current
                    // // target EObject
                    // if( modelEditor.getActiveObjectEditor() == null ||
                    // !modelEditor.getActiveObjectEditor().isEditingObject(object) )
                    // modelEditor.editModelObject(object, editorId);
                    //
                    // }
                    // }
                }
            }
        });
    }

    /**
     * Obtain a list of IFiles that are modified in open editors.
     * 
     * @return a list of IFiles that are open in an EditorPart with pending changes.
     */
    public static Collection getDirtyResources() {
        Collection result = Collections.EMPTY_LIST;
        IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
        if (page != null) {
            // look through the open editors and see if there is one available for this model file.
            IEditorPart[] editors = page.getDirtyEditors();
            if (editors != null && editors.length > 0) {
                result = new ArrayList(editors.length);
                for (int i = 0; i < editors.length; ++i) {
                    IEditorInput input = editors[i].getEditorInput();
                    if (input instanceof IFileEditorInput) {
                        result.add(((IFileEditorInput)input).getFile());
                    }
                }

            }
        }
        return result;
    }

    /**
     * Obtain a list of IFiles that are open in editors.
     * 
     * @return a list of IFiles that are open in an EditorPart.
     */
    public static Collection getOpenResources() {
        Collection result = Collections.EMPTY_LIST;
        IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
        if (page != null) {
            // look through the open editors and see if there is one available for this model file.
            IEditorReference[] editorRefs = page.getEditorReferences();
            if (editorRefs != null && editorRefs.length > 0) {
                result = new ArrayList(editorRefs.length);
                for (int i = 0; i < editorRefs.length; ++i) {
                    IEditorPart editor = editorRefs[i].getEditor(false);
                    if (editor != null) {
                        IEditorInput input = editor.getEditorInput();
                        if (input instanceof IFileEditorInput) {
                            result.add(((IFileEditorInput)input).getFile());
                        }
                    } else {
                        // if the editor is null, all we can do is lookup the resource via the tooltip path
                        String pathString = editorRefs[i].getTitleToolTip();
                        IPath path = new Path(pathString);
                        IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
                        if (resource instanceof IFile) {
                            result.add(resource);
                        }
                    }
                }
            }
        }
        return result;
    }

    // =============================================
    // Undo/Redo Methods

    public static void markNotSignificant( Object transactionID ) {
        undoManager.markNotSignificant(transactionID);
    }

    public static void ignoreUndoableToolkitEdit( Object transactionID ) {
        undoManager.ignoreUndoableToolkitEdit(transactionID);
    }

    // =============================================
    // Private Methods

    public static ModelEditor getModelEditorForFile( final IFile file,
                                                     boolean forceOpen ) {
        if (ModelerCore.HEADLESS) {
            return null;
        }

        ModelEditor result = null;
        staticEditor = null;
        if (file != null) {
            IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();

            if (window != null) {
                final IWorkbenchPage page = window.getActivePage();

                if (page != null) {
                    // look through the open editors and see if there is one available for this model file.
                    IEditorReference[] editors = page.getEditorReferences();
                    for (int i = 0; i < editors.length; ++i) {

                        IEditorPart editor = editors[i].getEditor(false);
                        if (editor != null) {
                            IEditorInput input = editor.getEditorInput();
                            if (input instanceof IFileEditorInput) {
                                if (file.equals(((IFileEditorInput)input).getFile())) {
                                    // found it;
                                    if (editor instanceof ModelEditor) {
                                        result = (ModelEditor)editor;
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    if (result == null && forceOpen) {
                        UiUtil.runInSwtThread(new Runnable() {
                            @Override
                            public void run() {
                                // there is no model editor open for this object. Open one and hand it the double-click target.
                                try {

                                    IEditorPart editor = IDE.openEditor(page, file, MODEL_EDITOR);
                                    if (editor instanceof ModelEditor) {
                                        staticEditor = editor;
                                    }

                                } catch (PartInitException e) {
                                    String message = e.getStatus().getMessage();
                                    if (message != null) {
                                        String targetVrMsg = VR_MSG;
                                        String targetXmlMsg = XML_MSG;
                                        if (message.equals(targetVrMsg)) {
                                            Util.log(IStatus.WARNING, targetVrMsg);
                                        } else if (message.equals(targetXmlMsg)) {
                                            Util.log(IStatus.WARNING, targetXmlMsg);
                                        } else {
                                            Util.log(IStatus.ERROR,
                                                     e,
                                                     Util.getString("ModelEditorManager.getModelEditorForFile", file.toString())); //$NON-NLS-1$
                                        }
                                    }
                                    staticEditor = null;
                                }
                            }
                        },
                                              true);
                        result = (ModelEditor)staticEditor;
                    }
                }
            }
        }
        staticEditor = null;
        return result;
    }

    /*
     * Find an Editor Reference, if any, for the given file
     * @param file
     * @since 4.2
     * @return an IEditorReference, or null
     */
    public static IEditorReference getEditorReferenceForFile( IFile file ) {
        // jh Defect 19139: added this method.

        IEditorReference result = null;

        if (file != null) {
            IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();

            if (window != null) {
                IWorkbenchPage page = window.getActivePage();

                // jh Defect 19139:
                // There may not be an actual 'realized' editor. In this case just remove the Reference
                if (page != null) {
                    // look through the open editor refs and see if there is one for this model file.
                    IEditorReference[] editors = page.getEditorReferences();

                    for (int i = 0; i < editors.length; ++i) {

                        String sEditorReferenceName = editors[i].getName();

                        if (sEditorReferenceName != null && file.getName().equals(sEditorReferenceName)) {
                            // found it;
                            result = editors[i];
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    /* Remove the Editor Reference
     * @param editorRef
     * @since 4.2
     */
    public static void removeEditorReference( IEditorReference editorRef ) {
        // jh Defect 19139: added this method.

        if (editorRef != null) {
            IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();

            if (window != null) {
                IWorkbenchPage page = window.getActivePage();

                if (page != null) {
                    // look through the open editors and see if there is one available for this model file.
                    IEditorReference[] editors = page.getEditorReferences();

                    for (int i = 0; i < editors.length; ++i) {

                        if (editorRef.equals(editors[i])) {

                            IEditorReference[] editorRefsToClose = {editorRef};
                            page.closeEditors(editorRefsToClose, false);

                            break;
                        }
                    }
                }
            }
        }
    }

    static ModelEditor getModelEditorForObject( EObject object,
                                                boolean forceOpen ) {
        ModelEditor result = null;

        IFile file = null;
        ModelResource mdlRsrc = ModelUtilities.getModelResourceForModelObject(object);
        if (mdlRsrc != null) {
            file = (IFile)mdlRsrc.getResource();
            result = getModelEditorForFile(file, forceOpen);
        }
        return result;
    }

    /**
     * Static method used to generically close the model object editor for the active page.
     * 
     * @return true if editor was found and active object editor was closed
     * @since 4.2
     */
    public static boolean closeObjectEditor() {
        boolean hasActiveObjectEditor = false;
        IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
        if (page != null) {
            // Check to see if page is our multi-page model editor, then close object editor.
            IEditorPart activeEditor = page.getActiveEditor();
            if (activeEditor instanceof ModelEditor) {
                hasActiveObjectEditor = (((ModelEditor)activeEditor).getActiveObjectEditor() != null);
                ((ModelEditor)activeEditor).closeObjectEditor();
            }
        }
        return hasActiveObjectEditor;
    }

    /*
     * Private method used to manage the model object editor state via this manager.
     * @param modelEditor
     * @param object
     * @param objectEditorValue
     * @since 4.2
     */
    static void updateObjectEditor( ModelEditor modelEditor,
                                    EObject object,
                                    int objectEditorValue ) {
        switch (objectEditorValue) {
            case UiConstants.ObjectEditor.FORCE_CLOSE_EDITOR: {
                if (modelEditor.getActiveObjectEditor() != null) modelEditor.closeObjectEditor();
            }
                break;
            case UiConstants.ObjectEditor.FORCE_OPEN_EDITOR: {
                if (modelEditor.canEditModelObject(object)) {
                    modelEditor.editModelObject(object, null);
                }
            }
                break;
            case UiConstants.ObjectEditor.REFRESH_EDITOR_IF_OPEN: {
                if (modelEditor.getActiveObjectEditor() != null) {
                    Object editableObject = modelEditor.getEditableObject(object);
                    if (editableObject != null) {
                        if (!modelEditor.getActiveObjectEditor().isEditingObject(editableObject)) {
                            modelEditor.editModelObject(editableObject, null);
                        }
                    } else {
                        modelEditor.closeObjectEditor();
                    }
                }
            }
                break;

            case UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR:
            default: {
                // Do NOthing.
            }
                break;
        }
    }

}

/**
 * CloseEditorRunnable is a Runnable for closing a ModelEditor that can return a boolean for whether or not the editor actually
 * closed.
 */
class CloseEditorRunnable implements Runnable {

    private IFile modelFile;
    private boolean save;
    public boolean didClose = true;

    public CloseEditorRunnable( IFile modelFile,
                                boolean save ) {
        this.modelFile = modelFile;
        this.save = save;
    }

    @Override
    public void run() {
        final ModelEditor modelEditor = ModelEditorManager.getModelEditorForFile(modelFile, false);
        if (modelEditor != null) {
            didClose = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().closeEditor(modelEditor, save);
        }
    }
}
