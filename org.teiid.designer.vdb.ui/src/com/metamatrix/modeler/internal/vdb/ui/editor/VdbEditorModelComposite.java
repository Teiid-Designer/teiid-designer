/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbEntry.SyncState;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.InternalUiConstants.Widgets;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.ListMessageDialog;
import com.metamatrix.ui.internal.widget.ListPanel;
import com.metamatrix.ui.internal.widget.ListPanelAdapter;
import com.metamatrix.ui.internal.widget.TablePanel;

/**
 * The Models panel of the VdbEditorOverviewPage. Before version 4.2, this code was implemented as a separate multi-tab Editor
 * called VdbModelEditorPage.
 * 
 * @since 4.2
 */
public final class VdbEditorModelComposite
    implements ListPanel.Constants, CoreStringUtil.Constants, VdbUiConstants, VdbEditor.Constants, Widgets {

    private static final String I18N_PREFIX = "VdbEditorModelPage."; //$NON-NLS-1$
    private static final String MODEL_SELECTION_DIALOG_TITLE = getString("modelSelectionDialogTitle"); //$NON-NLS-1$
    private static final String MODEL_NOT_FOUND_DIALOG_TITLE = getString("modelNotFoundDialogTitle"); //$NON-NLS-1$
    private static final String MODELS_GROUP = getString("modelsGroup"); //$NON-NLS-1$
    private static final String MODEL_SELECTION_DIALOG_MESSAGE = getString("modelSelectionDialogMessage"); //$NON-NLS-1$
    private static final String MODEL_NOT_FOUND_DIALOG_MESSAGE = getString("modelNotFoundDialogMessage"); //$NON-NLS-1$
    static final String INVALID_SELECTION_MESSAGE = getString("invalidSelectionMessage"); //$NON-NLS-1$
    private static final String REFRESH_BUTTON = getString("refreshButton"); //$NON-NLS-1$
    private static final String REFRESH_BUTTON_TOOLTIP = getString("refreshButtonTooltip"); //$NON-NLS-1$
    private static final String SYNCHRONIZE_ALL_BUTTON = getString("synchronizeAllButton"); //$NON-NLS-1$
    private static final String SYNCHRONIZE_ALL_BUTTON_TOOLTIP = getString("synchronizeAllButtonTooltip"); //$NON-NLS-1$
    private static final String FAILED_ADD_MSG_KEY = getString("failedAddMessage"); //$NON-NLS-1$
    static final String FAILED_SYNCHRONIZATION_MSG = getString("failedSynchronizationMessage"); //$NON-NLS-1$
    static final String SAVE_ON_SYNC_TITLE = getString("saveOnSyncSynchronizationTitle"); //$NON-NLS-1$
    static final String SAVE_ON_SYNC_MESSAGE = getString("saveOnSyncSynchronizationMessage"); //$NON-NLS-1$
    static final String ADDING_MODELS_MESSAGE = getString("addingModelsMessage"); //$NON-NLS-1$
    static final String ADD_MODEL_ERROR_TITLE = getString("addModelErrorTitle"); //$NON-NLS-1$
    private static final String REMOVE_MODEL_WITH_DEPENDENTS_TITLE = getString("removeModelWithDependents.title"); //$NON-NLS-1$

    private static final String REMOVE_MODEL_WITH_DEPENDENTS_MESSAGE_ID = "removeModelWithDependents.message"; //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     final Object parm ) {
        return Util.getString(I18N_PREFIX + id, parm);
    }

    VdbEditor editor;
    IResource vdbResource;

    TablePanel modelPanel;

    private VdbEditorModelTableProvider tableProvider;

    private Button refreshButton;
    Button synchronizeAllButton;
    boolean synchronizingModels = false;

    /**
     * @since 4.0
     */
    VdbEditorModelComposite( final VdbEditor editor ) {
        this.editor = editor;
        this.vdbResource = ((IFileEditorInput)editor.getEditorInput()).getFile();
    }

    /**
     * @since 4.0
     */
    Object[] addModels() {
        final IStructuredSelection selection = UiUtil.getStructuredSelection();
        Object obj;
        if (selection == null) obj = null;
        else {
            obj = selection.getFirstElement();
            if (obj instanceof IResource && !ModelUtilities.isModelFile((IResource)obj) && !ModelUtil.isXsdFile((IResource)obj)) obj = null;
            else if (obj instanceof EObject) obj = ModelerCore.getModelEditor().findModelResource((EObject)obj);
            // Null selection object for dialog if already in model list
            if (obj != null && this.modelPanel.contains(obj)) obj = null;
        }
        final ViewerFilter filter = new ViewerFilter() {

            @Override
            public boolean select( final Viewer viewer,
                                   final Object parent,
                                   final Object element ) {
                if (element instanceof IContainer) return true;

                if (element instanceof IResource) if (ModelUtilities.isModelFile((IResource)element)
                                                      || ModelUtil.isXsdFile((IResource)element)) return !VdbEditorModelComposite.this.modelPanel.contains(element);

                return false;
            }
        };
        final ISelectionStatusValidator validator = new ISelectionStatusValidator() {

            public IStatus validate( final Object[] selection ) {
                for (int ndx = selection.length; --ndx >= 0;)
                    if (selection[ndx] instanceof IContainer) return new Status(IStatus.ERROR, PLUGIN_ID, 0,
                                                                                INVALID_SELECTION_MESSAGE, null);
                return new Status(IStatus.OK, PLUGIN_ID, 0, EMPTY_STRING, null);
            }
        };

        final Object[] models = WidgetUtil.showWorkspaceObjectSelectionDialog(MODEL_SELECTION_DIALOG_TITLE,
                                                                              MODEL_SELECTION_DIALOG_MESSAGE,
                                                                              true,
                                                                              obj,
                                                                              new ModelingResourceFilter(filter),
                                                                              validator,
                                                                              new ModelLabelProvider());

        Object[] result = new Object[0];
        if (models.length > 0) // Add the models picked in the selection dialog ...
        if (validateModels(models)) result = addModels(models);
        return result;
    }

    /**
     * Broken out from addModels( ) so that it can be called by Refresh
     * 
     * @param modelFiles
     * @return
     * @since 4.2
     */
    Object[] addModels( final Object[] modelFiles ) {
        final ArrayList<IFile> addedModels = new ArrayList<IFile>();
        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor theMonitor ) {
                final boolean allOK = true;

                if (modelFiles != null && modelFiles.length > 0) { // &&
                    // allSelectedAreValidVDBModels(modelFiles,
                    // FAILED_ADD_MSG_KEY))
                    // {

                    // Get a crude estimate for the amount of work required to add
                    // the selected models and all their dependent models
                    int totalWork = 0;
                    for (int ndx = 0; ndx < modelFiles.length; ++ndx) {
                        final IFile model = (IFile)modelFiles[ndx];
                        totalWork += WorkspaceResourceFinderUtil.getDependentResources(model).length;
                    }
                    theMonitor.beginTask(ADDING_MODELS_MESSAGE, totalWork);
                    theMonitor.setTaskName(ADDING_MODELS_MESSAGE);

                    // Add each selected model to the VDB along with their dependent models
                    for (int ndx = 0; ndx < modelFiles.length; ++ndx) {
                        final IFile model = (IFile)modelFiles[ndx];

                        // model has been previously added don't try adding again
                        boolean addedPreviously = false;
                        for (final VdbEntry entry : editor.getVdb().getEntries())
                            if (model.getFullPath().toString().equals(entry.getName())) {
                                addedPreviously = true;
                                break;
                            }

                        // skip adding this model if added previously
                        if (addedPreviously) {
                            theMonitor.worked(1);
                            continue;
                        }

                        try {

                            // Add the model to the VDB
                            editor.getVdb().addModelEntry(model.getFullPath());
                            addedModels.add(model);
                        } catch (final Exception err) {
                            Util.log(err);
                            WidgetUtil.showError(err.getLocalizedMessage());
                        }
                        if (!allOK) {
                            theMonitor.setCanceled(true);
                            break;
                        }

                    }
                }

                if (!addedModels.isEmpty()) Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        editor.update();
                    }
                });

                theMonitor.done();
            }
        };
        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, true, op);
        } catch (final InterruptedException e) {
        } catch (final InvocationTargetException e) {
            VdbUiConstants.Util.log(e.getTargetException());
        }

        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                initializeModelVisibility();
            }
        });

        return addedModels.toArray();
    }

    Object[] addModelsInTransaction() {
        Object[] result = null;

        final boolean started = ModelerCore.startTxn(false, false, "Add Models to VDB", this); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            result = addModels();
            succeeded = true;
        } catch (final Exception ex) {
            VdbUiConstants.Util.log(IStatus.ERROR, ex, ex.getMessage());
        } finally {
            if (started) if (succeeded) ModelerCore.commitTxn();
            else {
                // We don't want to roll this back. Not really changing anything in any model
                // ModelerCore.rollbackTxn();
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    public Control createPartControl( final Composite parent ) {
        tableProvider = new VdbEditorModelTableProvider(this.editor);

        // pre-filter the model list, showing only PUBLIC models
        final ArrayList models = new ArrayList();
        for (final VdbEntry entry : this.editor.getVdb().getEntries())
            if (!(entry instanceof VdbModelEntry) || !((VdbModelEntry)entry).isBuiltIn()) models.add(entry);

        this.modelPanel = new TablePanel(parent, MODELS_GROUP, new ListPanelAdapter() {

            @Override
            public Object[] addButtonSelected() {
                return addModelsInTransaction();
            }

            @Override
            public Object editButtonSelected( final IStructuredSelection selection ) {
                return editModel(selection);
            }

            @Override
            public void itemsSelected( final IStructuredSelection selection ) {
                initRefreshButton(selection);
                initSynchronizeAllButton();
            }

            @Override
            public Object[] removeButtonSelected( final IStructuredSelection selection ) {
                return removeModels(selection);
            }
        }, SWT.MULTI | SWT.CHECK | SWT.FULL_SELECTION, ITEMS_EDITABLE);

        this.modelPanel.getTableViewer().setLabelProvider(tableProvider);
        this.modelPanel.getTableViewer().setContentProvider(tableProvider);
        this.modelPanel.deleteMessageLabel(); // delete the message label
        this.modelPanel.getTableViewer().addFilter(new ViewerFilter() {
            @Override
            public boolean select( final Viewer viewer,
                                   final Object parentElement,
                                   final Object element ) {
                if (element instanceof VdbModelEntry && ((VdbModelEntry)element).isBuiltIn()) return false;
                return true;
            }
        });

        this.modelPanel.getTableViewer().getTable().setHeaderVisible(true);
        this.modelPanel.getTableViewer().getTable().setLinesVisible(false);
        tableProvider.buildTableColumns(modelPanel.getTableViewer());
        this.modelPanel.getTableViewer().setInput(models);
        this.modelPanel.resetSelectionListener();
        this.modelPanel.getTableViewer().setSorter(new ViewerSorter() {});

        this.modelPanel.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( final SelectionChangedEvent event ) {
                final ISelectionProvider provider = editor.getSite().getSelectionProvider();
                if (event.getSelection() instanceof StructuredSelection) {
                    final StructuredSelection selection = (StructuredSelection)event.getSelection();
                    if (selection.size() == 1) provider.setSelection(event.getSelection());
                    else provider.setSelection(new StructuredSelection());
                } else provider.setSelection(event.getSelection());
            }
        });

        refreshButton = this.modelPanel.addButton(REFRESH_BUTTON);
        refreshButton.setToolTipText(REFRESH_BUTTON_TOOLTIP);

        refreshButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {

                BusyIndicator.showWhile(null, new Runnable() {
                    public void run() {
                        modelPanel.setEnabled(false);

                        boolean bSaveAfter = false;
                        if (operationWillSyncAllRemainingUnsynchedModels()) bSaveAfter = MessageDialog.openQuestion(editor.getSite().getShell(),
                                                                                                                    SAVE_ON_SYNC_TITLE,
                                                                                                                    SAVE_ON_SYNC_MESSAGE);

                        final boolean started = ModelerCore.startTxn(false, false, "Synchonize Selected Models", this); //$NON-NLS-1$
                        boolean succeeded = false;

                        try {
                            refreshSelectedModels(modelPanel.getSelection().toList());
                            if (bSaveAfter) editor.doSave(new NullProgressMonitor());
                            succeeded = true;
                        } catch (final Exception ex) {
                            VdbUiConstants.Util.log(IStatus.ERROR, ex, ex.getMessage());
                        } finally {
                            if (started) {
                                if (succeeded) ModelerCore.commitTxn();
                                else {
                                    // We don't want to roll this back. Not really changing anything in any model
                                    // ModelerCore.rollbackTxn();
                                }
                                modelPanel.setEnabled(true);
                            }
                        }
                    }
                });
            }
        });

        refreshButton.moveBelow(this.modelPanel.getButton(InternalUiConstants.Widgets.REMOVE_BUTTON));
        refreshButton.setEnabled(false);

        synchronizeAllButton = this.modelPanel.addButton(SYNCHRONIZE_ALL_BUTTON);
        synchronizeAllButton.setToolTipText(SYNCHRONIZE_ALL_BUTTON_TOOLTIP);

        synchronizeAllButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                synchronizeVdb(false);
            }
        });

        synchronizeAllButton.moveBelow(refreshButton);
        synchronizeAllButton.setEnabled(false);

        // swj: I really hate putting these height constants here, but I've
        // tried everything else
        // and the table in the list panel simply won't layout properly in all
        // cases.
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 140;
        gd.minimumHeight = 140;
        this.modelPanel.getTableViewer().getTable().setLayoutData(gd);
        gd = new GridData(GridData.FILL_BOTH);
        // gd.heightHint = 136;
        gd.minimumHeight = 180;
        modelPanel.setLayoutData(gd);

        initializeModelVisibility();
        this.modelPanel.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged( final CheckStateChangedEvent event ) {
                setVisibilityState(event.getElement(), event.getChecked());
            }
        });

        // correction for defect 15847 - bad layout.
        // Apparently, the Style bits were not needed, anyway.
        // WidgetFactory.createWrappingLabel(modelPanel.getGroup(), GridData.FILL_BOTH, 2, INDEX_MESSAGE);

        refresh();

        return modelPanel;
    }

    /**
     * @since 4.0
     */
    Object editModel( final IStructuredSelection selection ) {
        final IFile file = ((VdbEntry)selection.getFirstElement()).findFile();
        if (file == null) {
            MessageDialog.openWarning(null, MODEL_NOT_FOUND_DIALOG_TITLE, MODEL_NOT_FOUND_DIALOG_MESSAGE);
            return null;
        }
        try {
            IDE.openEditor(UiUtil.getWorkbenchPage(), file);
        } catch (final PartInitException err) {
            Util.log(err);
            WidgetUtil.showError(err.getLocalizedMessage());
        }
        return null;
    }

    /**
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     * @since 4.0
     */
    public void gotoMarker( final IMarker marker ) {
    }

    boolean hasStaleModels() {
        return editor.getVdb().isSynchronized();
    }

    void initializeModelVisibility() {
        final CheckboxTableViewer table = (CheckboxTableViewer)this.modelPanel.getTableViewer();
        for (int i = 0; i < table.getTable().getItemCount(); ++i) {
            final Object row = table.getElementAt(i);
            if (row instanceof VdbModelEntry) table.setChecked(row, ((VdbModelEntry)row).isVisible());
        }
    }

    void initRefreshButton( final IStructuredSelection selection ) {
        if (!this.modelPanel.getEnabled()) {
            this.refreshButton.setEnabled(false);
            return;
        }

        boolean enable = !selection.isEmpty();
        for (final Iterator iter = selection.iterator(); iter.hasNext();)
            if (((VdbEntry)iter.next()).getSyncState() != SyncState.OutOfSync) {
                enable = false;
                break;
            }
        this.refreshButton.setEnabled(enable);
    }

    void initSynchronizeAllButton() {
        if (synchronizingModels) return;

        if (!this.modelPanel.getEnabled()) {
            this.synchronizeAllButton.setEnabled(false);
            return;
        }

        this.synchronizeAllButton.setEnabled(hasStaleModels());
    }

    public void layout() {
        this.modelPanel.layout(true);
    }

    boolean operationWillSyncAllRemainingUnsynchedModels() {

        final List lstSelectedModels = modelPanel.getSelection().toList();
        for (final VdbEntry entry : editor.getVdb().getEntries())
            // test each model in the full list to see if it is out of sync
            if (entry.getSyncState() == SyncState.OutOfSync) // see if this out of sync model is NOT in the selection; if so
            // return false
            if (!lstSelectedModels.contains(entry)) return false;

        // if there are not out of sync models outside of the selected list,
        // return true
        return true;
    }

    void refresh() {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                if (modelPanel.getTableViewer() != null && !modelPanel.getTableViewer().getTable().isDisposed()) {
                    modelPanel.getTableViewer().refresh();
                    initializeModelVisibility();
                    final ISelection selection = modelPanel.getTableViewer().getSelection();
                    if (!synchronizingModels) initSynchronizeAllButton();
                    if (selection instanceof IStructuredSelection) initRefreshButton((IStructuredSelection)selection);
                }
            }
        });
    }

    void refreshSelectedModels( final List selectedModels ) {
        synchronizingModels = true;
        final boolean modified = editor.getVdb().synchronize(selectedModels);
        if (modified) {
            this.editor.update();
            refresh();
        }

        synchronizingModels = false;
    }

    /**
     * @since 4.0
     */
    Object[] removeModels( final IStructuredSelection selection ) {
        final List selected = selection.toList();

        // Determine which models depend upon the models selected for deletion
        final Vdb vdb = editor.getVdb();
        final Set<VdbModelEntry> dependentEntriesToDelete = new HashSet<VdbModelEntry>();
        for (final Iterator iter = selected.iterator(); iter.hasNext();) {
            final VdbEntry entry = (VdbEntry)iter.next();
            if (entry instanceof VdbModelEntry) dependentEntriesToDelete.addAll(((VdbModelEntry)entry).getDependsUpon());
        }
        // Remove models explicitly selected for deletion
        dependentEntriesToDelete.removeAll(selected);
        // Prompt user if OK to delete dependents
        boolean delete = true;
        if (!dependentEntriesToDelete.isEmpty()) delete = ListMessageDialog.openWarningQuestion(modelPanel.getShell(),
                                                                                                REMOVE_MODEL_WITH_DEPENDENTS_TITLE,
                                                                                                null,
                                                                                                getString(REMOVE_MODEL_WITH_DEPENDENTS_MESSAGE_ID,
                                                                                                          selected),
                                                                                                new ArrayList(
                                                                                                              dependentEntriesToDelete),
                                                                                                new ModelReferenceLabelProvider());

        final Set<VdbEntry> removedModels = new HashSet<VdbEntry>(selected);
        if (delete) {
            removedModels.addAll(dependentEntriesToDelete);
            // user wanted to delete, or there were no dependencies:
            for (final VdbEntry entry : removedModels)
                vdb.removeEntry(entry);
            this.editor.update();
        }
        return removedModels.toArray();
    }

    public void resetColumnWidths() {
        tableProvider.resetColumnWidths(modelPanel.getTableViewer());
    }

    public void setEnabledState( final boolean isEnabled ) {
        this.modelPanel.setEnabled(isEnabled);
        if (!isEnabled) {
            refreshButton.setEnabled(false);
            synchronizeAllButton.setEnabled(false);
        }
    }

    void setVisibilityState( final Object entry,
                             final boolean makeVisible ) {
        ((VdbModelEntry)entry).setVisible(makeVisible);
        this.editor.update();
    }

    /**
     * Public method used to synch the VDB outside of this editor sub-panel. if autoSave is ON, then this method forces a save,
     * else the user is asked to save the dirty editor after synchronizing. (Defect 22305)
     * 
     * @param autoSave
     */
    public void synchronizeVdb( final boolean autoSave ) {

        BusyIndicator.showWhile(null, new Runnable() {
            public void run() {
                if (hasStaleModels()) {
                    modelPanel.setEnabled(false);

                    boolean bSaveAfter = autoSave;

                    if (!bSaveAfter) bSaveAfter = MessageDialog.openQuestion(editor.getSite().getShell(),
                                                                             SAVE_ON_SYNC_TITLE,
                                                                             SAVE_ON_SYNC_MESSAGE);

                    final boolean started = ModelerCore.startTxn(false, false, "Synchonize All Models", this); //$NON-NLS-1$
                    boolean succeeded = false;

                    try {
                        editor.getVdb().synchronize();
                        if (bSaveAfter) editor.doSave(new NullProgressMonitor());
                        succeeded = true;
                    } catch (final Exception ex) {
                        VdbUiConstants.Util.log(IStatus.ERROR, ex, ex.getMessage());
                    } finally {
                        if (started) if (succeeded) ModelerCore.commitTxn();
                        else // We don't want to roll this back. Not really changing anything in any model
                        ModelerCore.rollbackTxn();
                        modelPanel.setEnabled(true);
                    }
                }
            }
        });
    }

    private boolean validateModels( final Object[] modelFiles ) {
        // defect 15891 - scan entries and their dependencies before adding to
        // VDB
        // check the model, and its dependencies (the new 'true' at the end).

        boolean valid = true;
        for (int ndx = 0; ndx < modelFiles.length; ++ndx) {
            final IFile model = (IFile)modelFiles[ndx];
            if (!ModelUtilities.verifyWorkspaceValidationState(model, this, FAILED_ADD_MSG_KEY, true)) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    static final class ModelReferenceLabelProvider implements ILabelProvider {
        public void addListener( final ILabelProviderListener listener ) {
        }

        public void dispose() {
        }

        public Image getImage( final Object element ) {
            return null;
        }

        public String getText( final Object element ) {
            return ((VdbEntry)element).getName().toString();
        }

        public boolean isLabelProperty( final Object element,
                                        final String property ) {
            return false;
        }

        public void removeListener( final ILabelProviderListener listener ) {
        }
    }
}
