/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import static com.metamatrix.modeler.vdb.ui.preferences.VdbPreferenceConstants.SYNCHRONIZE_WITHOUT_WARNING;
import static org.teiid.designer.core.util.StringConstants.EMPTY_STRING;
import static org.teiid.designer.vdb.Vdb.Event.CLOSED;
import static org.teiid.designer.vdb.Vdb.Event.ENTRY_SYNCHRONIZATION;
import static org.teiid.designer.vdb.Vdb.Event.MODEL_JNDI_NAME;
import static org.teiid.designer.vdb.Vdb.Event.MODEL_TRANSLATOR;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.EditorPart;
import org.osgi.service.prefs.BackingStoreException;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.ui.NewDataRoleWizard;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbDataRole;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbEntry.Synchronization;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.connections.SourceHandlerExtensionManager;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.modeler.vdb.ui.VdbUiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.DefaultContentProvider;
import com.metamatrix.ui.table.CheckBoxColumnProvider;
import com.metamatrix.ui.table.DefaultTableProvider;
import com.metamatrix.ui.table.TableAndButtonsGroup;
import com.metamatrix.ui.table.TableAndButtonsGroup.RemoveButtonProvider;
import com.metamatrix.ui.table.TextColumnProvider;
import com.metamatrix.ui.text.StyledTextEditor;

/**
 * @since 4.0
 */
// TODO: read-only, undo/redo, function model 259
public final class VdbEditor extends EditorPart implements IResourceChangeListener {

    private static final String DESCRIPTION_GROUP = i18n("descriptionGroup"); //$NON-NLS-1$
    private static final String MODELS_GROUP = i18n("modelsGroup"); //$NON-NLS-1$
    private static final String OTHER_FILES_GROUP = i18n("otherFilesGroup"); //$NON-NLS-1$
    private static final String DATA_POLICY_GROUP = i18n("dataPolicyGroup"); //$NON-NLS-1$

    static final String MODEL_COLUMN_NAME = i18n("modelColumnName"); //$NON-NLS-1$
    static final String FILE_COLUMN_NAME = i18n("fileColumnName"); //$NON-NLS-1$
    static final String DATA_POLICY_COLUMN_NAME = i18n("dataPolicyName"); //$NON-NLS-1$
    static final String PATH_COLUMN_NAME = i18n("pathColumnName"); //$NON-NLS-1$
    static final String SYNCHRONIZED_COLUMN_NAME = i18n("synchronizedColumnName"); //$NON-NLS-1$
    static final String VISIBLE_COLUMN_NAME = i18n("visibleColumnName"); //$NON-NLS-1$;
    static final String SOURCE_NAME_COLUMN_NAME = i18n("sourceNameColumnName"); //$NON-NLS-1$;
    static final String TRANSLATOR_COLUMN_NAME = i18n("translatorColumnName"); //$NON-NLS-1$
    static final String JNDI_NAME_COLUMN_NAME = i18n("jndiNameColumnName"); //$NON-NLS-1$;
    static final String DESCRIPTION_COLUMN_NAME = i18n("descriptionColumnName"); //$NON-NLS-1$;

    static final String SYNCHRONIZED_TOOLTIP = i18n("synchronizedTooltip"); //$NON-NLS-1$
    static final String UNSYNCHRONIZED_TOOLTIP = i18n("unsynchronizedTooltip"); //$NON-NLS-1$
    static final String SYNCHRONIZATION_NOT_APPLICABLE_TOOLTIP = i18n("synchronizationNotApplicableTooltip"); //$NON-NLS-1$

    static final String VISIBLE_TOOLTIP = i18n("visibleTooltip"); //$NON-NLS-1$
    static final String NOT_VISIBLE_TOOLTIP = i18n("notVisibleTooltip"); //$NON-NLS-1$

    static final String ADD_FILE_DIALOG_TITLE = i18n("addFileDialogTitle"); //$NON-NLS-1$
    static final String ADD_FILE_DIALOG_MESSAGE = i18n("addFileDialogMessage"); //$NON-NLS-1$
    static final String ADD_FILE_DIALOG_INVALID_SELECTION_MESSAGE = i18n("addFileDialogInvalidSelectionMessage"); //$NON-NLS-1$

    static final String CONFIRM_DIALOG_TITLE = i18n("confirmDialogTitle"); //$NON-NLS-1$
    static final String CONFIRM_SYNCHRONIZE_MESSAGE = i18n("confirmSynchronizeMessage"); //$NON-NLS-1$
    static final String CONFIRM_SYNCHRONIZE_ALL_MESSAGE = i18n("confirmSynchronizeAllMessage"); //$NON-NLS-1$
    static final String CONFIRM_REMOVE_MESSAGE = i18n("confirmRemoveMessage"); //$NON-NLS-1$
    static final String CONFIRM_REMOVE_IMPORTED_BY_MESSAGE = i18n("confirmRemoveImportedByMessage"); //$NON-NLS-1$
    static final String INFORM_DATA_ROLES_ON_ADD_MESSAGE = i18n("informDataRolesExistOnAddMessage"); //$NON-NLS-1$
    

    static final String SYNCHRONIZE_ALL_BUTTON = i18n("synchronizeAllButton"); //$NON-NLS-1$
    static final String COPY_SUFFIX = i18n("cloneDataRoleAction.copySuffix"); //$NON-NLS-1$
    static final String CLONE_DATA_ROLE_LABEL = i18n("cloneDataRoleActionLabel"); //$NON-NLS-1$
    
    static final String WEB_SERVICES_VIEW_MODEL_URI = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$

    private static String i18n( final String id ) {
        return VdbUiConstants.Util.getString(id);
    }

    Vdb vdb;
    StyledTextEditor textEditor;
    @SuppressWarnings( "unchecked" )
    TableAndButtonsGroup modelsGroup;
    @SuppressWarnings( "unchecked" )
    TableAndButtonsGroup otherFilesGroup;
    @SuppressWarnings( "unchecked" )
    TableAndButtonsGroup dataRolesGroup;
    private Button synchronizeAllButton;
    private PropertyChangeListener vdbListener;
    
    Action cloneDataRoleAction;
    VdbDataRole selectedDataRole;
    VdbDataRoleResolver dataRoleResolver;
    
    boolean disposed = false;

    /**
     * Method which adds models to the VDB.
     * 
     * @param models
     */
    public void addModels( final List<IFile> models ) {
        for (final Object model : models)
            vdb.addModelEntry(((IFile)model).getFullPath(), new NullProgressMonitor());

        modelsGroup.getTable().getViewer().refresh();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public void createPartControl( final Composite parent ) {
        // Compute height of description text area
        // For some reason, this can't be less than 4, or the vertical scrollbar widgets never appear (at least on a OSX)
        final GC gc = new GC(parent);
        gc.setFont(parent.getFont());
        final int height = Dialog.convertHeightInCharsToPixels(gc.getFontMetrics(), 4);
        gc.dispose();

        // Insert a ScrolledComposite so controls don't disappear if the panel shrinks
        final ScrolledComposite scroller = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setLayout(new GridLayout());
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        // Tweak the scroll bars to give better scrolling behavior:
        ScrollBar bar = scroller.getHorizontalBar();
        if (bar != null) bar.setIncrement(height / 4);
        bar = scroller.getVerticalBar();
        if (bar != null) bar.setIncrement(height / 4);

        final Composite pg = WidgetFactory.createPanel(scroller, SWT.NONE, GridData.FILL_BOTH, 1, 2);
        scroller.setContent(pg);

        final Group group = WidgetFactory.createGroup(pg, DESCRIPTION_GROUP, GridData.FILL_HORIZONTAL, 2);
        this.textEditor = new StyledTextEditor(group, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 1;
        gridData.heightHint = height;
        gridData.minimumHeight = height;
        this.textEditor.setLayoutData(gridData);
        this.textEditor.setText(vdb.getDescription());
        this.textEditor.getDocument().addDocumentListener(new IDocumentListener() {

            public void documentAboutToBeChanged( final DocumentEvent event ) {
            }

            public void documentChanged( final DocumentEvent event ) {
                vdb.setDescription(textEditor.getText());
            }

        });

        final TextColumnProvider pathColumnProvider = new TextColumnProvider<VdbEntry>() {

            @Override
            public String getName() {
                return PATH_COLUMN_NAME;
            }
            
            @Override
            public Image getImage() {
                return null;
            }

            @Override
            public String getValue( final VdbEntry element ) {
                return element.getName().removeLastSegments(1).toString();
            }
        };
        final CheckBoxColumnProvider syncColumnProvider = new CheckBoxColumnProvider<VdbEntry>() {

            @Override
            public String getName() {
                return null;
            }
            
            @Override
            public Image getImage() {
                return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.SYNCHRONIZE_MODELS_ICON);
            }

            @Override
            public String getToolTip( final VdbEntry element ) {
                if (element.getSynchronization() == Synchronization.Synchronized) return SYNCHRONIZED_TOOLTIP;
                if (element.getSynchronization() == Synchronization.NotSynchronized) return UNSYNCHRONIZED_TOOLTIP;
                return SYNCHRONIZATION_NOT_APPLICABLE_TOOLTIP;
            }

            @Override
            public Boolean getValue( final VdbEntry element ) {
                return element.getSynchronization() == Synchronization.Synchronized;
            }

            @Override
            public boolean isEditable( final VdbEntry element ) {
                return element.getSynchronization() == Synchronization.NotSynchronized;
            }

            @Override
            public void setValue( final VdbEntry element,
                                  final Boolean value ) {
                IPreferenceStore prefStore = VdbUiPlugin.singleton.getPreferenceStore();
                // set value to true if preferene not found
                boolean showWarningDialog = "".equals(prefStore.getString(SYNCHRONIZE_WITHOUT_WARNING)) ? true //$NON-NLS-1$
                                                                                                       : !prefStore.getBoolean(SYNCHRONIZE_WITHOUT_WARNING);
                boolean synchronize = !showWarningDialog;

                if (showWarningDialog) {
                    MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(Display.getCurrent()
                                                                                                        .getActiveShell(),
                                                                                                 CONFIRM_DIALOG_TITLE,
                                                                                                 CONFIRM_SYNCHRONIZE_MESSAGE,
                                                                                                 VdbUiConstants.Util.getString("rememberMyDecision"), //$NON-NLS-1$
                                                                                                 false,
                                                                                                 null,
                                                                                                 null);
                    if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
                        synchronize = true;

                        // save preference
                        if (dialog.getToggleState()) {
                            try {
                                prefStore.setValue(SYNCHRONIZE_WITHOUT_WARNING, true);
                                VdbUiPlugin.singleton.getPreferences().flush();
                            } catch (BackingStoreException e) {
                                VdbUiConstants.Util.log(e);
                            }
                        }
                    }
                }

                if (synchronize) {
                    BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                        @Override
                        public void run() {
                            element.synchronize(new NullProgressMonitor());
                            dataRoleResolver.modelSynchronized(element);
                            VdbEditor.this.doSave(new NullProgressMonitor());
                        }
                    });
                }
            }
        };
        final TextColumnProvider descriptionColumnProvider = new TextColumnProvider<VdbEntry>() {

            @Override
            public String getName() {
                return DESCRIPTION_COLUMN_NAME;
            }
            
            @Override
            public Image getImage() {
                return null;
            }

            @Override
            public String getValue( final VdbEntry element ) {
                return element.getDescription();
            }

            @Override
            public boolean isEditable( final VdbEntry element ) {
                return true;
            }

            @Override
            public void setValue( final VdbEntry element,
                                  final String value ) {
                element.setDescription(value);
            }
        };
        modelsGroup = new TableAndButtonsGroup(pg, MODELS_GROUP, 2, new DefaultTableProvider<VdbModelEntry>() {

            @Override
            public void doubleClicked( final VdbModelEntry element ) {
                openEditor(element);
            }

            @Override
            public VdbModelEntry[] getElements() {
                final Set<VdbModelEntry> modelEntries = vdb.getModelEntries();
                return modelEntries.toArray(new VdbModelEntry[modelEntries.size()]);
            }

            @Override
            public boolean isDoubleClickSupported() {
                return true;
            }
        }, new TextColumnProvider<VdbModelEntry>() {

            @Override
            public Image getImage( final VdbModelEntry element ) {
                return ModelIdentifier.getModelImage(element.findFileInWorkspace());
            }

            @Override
            public String getName() {
                return MODEL_COLUMN_NAME;
            }
            
            @Override
            public Image getImage() {
                return null;
            }

            @Override
            public String getValue( final VdbModelEntry element ) {
                return element.getName().lastSegment();
            }
        }, pathColumnProvider, syncColumnProvider, new CheckBoxColumnProvider<VdbModelEntry>() {

            @Override
            public String getName() {
                return null;
            }
            
            @Override
            public Image getImage() {
                return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.VISIBLE_ICON);
            }

            @Override
            public String getToolTip( final VdbModelEntry element ) {
                return element.isVisible() ? VISIBLE_TOOLTIP : NOT_VISIBLE_TOOLTIP;
            }

            @Override
            public Boolean getValue( final VdbModelEntry element ) {
                return element.isVisible();
            }

            @Override
            public boolean isEditable( final VdbModelEntry element ) {
                return true;
            }

            @Override
            public void setValue( final VdbModelEntry element,
                                  final Boolean value ) {
                element.setVisible(value);
            }
        }, new TextColumnProvider<VdbModelEntry>() {

            @Override
            public String getName() {
                return SOURCE_NAME_COLUMN_NAME;
            }
            
            @Override
            public Image getImage() {
                return null;
            }

            @Override
            public String getValue( final VdbModelEntry element ) {
                final String value = element.getSourceName();
                return value == null ? EMPTY_STRING : value;
            }

            @Override
            public boolean isEditable( final VdbModelEntry element ) {
                return true;
            }

            @Override
            public void setValue( final VdbModelEntry element,
                                  final String value ) {
                element.setSourceName(value);
            }
        }, new TextColumnProvider<VdbModelEntry>() {

            @Override
            public String getName() {
                return TRANSLATOR_COLUMN_NAME;
            }
            
            @Override
            public Image getImage() {
                return null;
            }

            @Override
            public String getValue( final VdbModelEntry element ) {
                final String value = element.getTranslator();
                return value == null ? EMPTY_STRING : value;
            }

            @Override
            public boolean isEditable( final VdbModelEntry element ) {
                return true;
            }

            @Override
            public void setValue( final VdbModelEntry element,
                                  final String value ) {
                element.setTranslator(value);
            }
        }, new TextColumnProvider<VdbModelEntry>() {

            @Override
            public String getName() {
                return JNDI_NAME_COLUMN_NAME;
            }
            
            @Override
            public Image getImage() {
                return null;
            }

            @Override
            public String getValue( final VdbModelEntry element ) {
                final String value = element.getJndiName();
                return value == null ? EMPTY_STRING : value;
            }

            @Override
            public boolean isEditable( final VdbModelEntry element ) {
                return true;
            }

            @Override
            public void setValue( final VdbModelEntry element,
                                  final String value ) {
                element.setJndiName(value);
            }
        }, descriptionColumnProvider);
        final ModelLabelProvider modelLabelProvider = new ModelLabelProvider();
        final ISelectionStatusValidator validator = new ISelectionStatusValidator() {

            public IStatus validate( final Object[] selection ) {
                for (int ndx = selection.length; --ndx >= 0;)
                    if (selection[ndx] instanceof IContainer) return new Status(IStatus.ERROR, VdbUiConstants.PLUGIN_ID, 0,
                                                                                ADD_FILE_DIALOG_INVALID_SELECTION_MESSAGE, null);
                return new Status(IStatus.OK, VdbUiConstants.PLUGIN_ID, 0, EMPTY_STRING, null);
            }
        };
        modelsGroup.add(modelsGroup.new AddButtonProvider() {

            @Override
            protected void add() {
                final ViewerFilter filter = new ViewerFilter() {

                    @Override
                    public boolean select( final Viewer viewer,
                                           final Object parent,
                                           final Object element ) {
                        if (element instanceof IContainer) return true;
                        final IFile file = (IFile)element;
                        if (!ModelUtilities.isModelFile(file) && !ModelUtil.isXsdFile(file)) return false;
                        for (final VdbModelEntry modelEntry : vdb.getModelEntries())
                            if (file.equals(modelEntry.findFileInWorkspace())) return false;
                        return true;
                    }
                };
                ModelingResourceFilter wsFilter = new ModelingResourceFilter(filter);
                wsFilter.setShowHiddenProjects(true);
                final Object[] models = WidgetUtil.showWorkspaceObjectSelectionDialog(ADD_FILE_DIALOG_TITLE,
                                                                                      ADD_FILE_DIALOG_MESSAGE,
                                                                                      true,
                                                                                      null,
                                                                                      wsFilter,
                                                                                      validator,
                                                                                      modelLabelProvider);
                if( !vdb.getDataPolicyEntries().isEmpty() ) {
                	MessageDialog.openInformation(Display.getCurrent().getActiveShell(), 
                			VdbEditor.CONFIRM_DIALOG_TITLE, INFORM_DATA_ROLES_ON_ADD_MESSAGE);
                }
                
                // indicates if this is the first time models are being added
                boolean firstTime = (modelsGroup.getTable().getViewer().getTable().getItemCount() == 0);
                
                // add the models
                for (final Object model : models)
                    vdb.addModelEntry(((IFile)model).getFullPath(), new NullProgressMonitor());
                
                // refresh table from model
                modelsGroup.getTable().getViewer().refresh();

                // pack columns if first time a model is added
                if (firstTime) { 
                    WidgetUtil.pack(modelsGroup.getTable().getViewer());
                }
            }
        });
        final RemoveButtonProvider removeButtonProvider = modelsGroup.new RemoveButtonProvider() {

            @Override
            public void selected( final IStructuredSelection selection ) {
                if (!ConfirmationDialog.confirm(CONFIRM_REMOVE_MESSAGE)) return;
                final Set<VdbEntry> entries = new HashSet<VdbEntry>();
                final Set<VdbModelEntry> importedBy = new HashSet<VdbModelEntry>();
                for (final Object element : selection.toList()) {
                    entries.add((VdbEntry)element);
                    if (element instanceof VdbModelEntry) importedBy.addAll(((VdbModelEntry)element).getImportedBy());
                }
                if (!importedBy.isEmpty()) importedBy.removeAll(entries);
                if (!importedBy.isEmpty()) {
                    if (!ConfirmationDialog.confirm(new ConfirmationDialog(CONFIRM_REMOVE_IMPORTED_BY_MESSAGE) {

                        @Override
                        protected Control createCustomArea( final Composite parent ) {
                            final ListViewer viewer = new ListViewer(parent);
                            viewer.setContentProvider(new DefaultContentProvider());
                            viewer.setLabelProvider(new LabelProvider() {

                                @Override
                                public String getText( final Object element ) {
                                    return ((VdbEntry)element).getName().toString();
                                }
                            });
                            viewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                            viewer.setInput(importedBy);
                            return viewer.getControl();
                        }
                    })) return;
                    entries.addAll(importedBy);
                }
                
                dataRoleResolver.modelEntriesRemoved(entries);
                
                for (final VdbEntry entry : entries)
                    vdb.removeEntry(entry);
            }
        };
        modelsGroup.add(removeButtonProvider);
        // Add selection changed listener so if a Physical Source model is selected, the applicable menu actions are
        // retrieved via the SourceHandler extension point and interface.
        // This allows changing Translator and JNDI names via existing deployed objects on Teiid Servers that are
        // connected in the user's workspace.
        final TableViewer viewer = modelsGroup.getTable().getViewer();
        final MenuManager menuManager = new MenuManager();
        viewer.getControl().setMenu(menuManager.createContextMenu(pg));
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( final SelectionChangedEvent event ) {
                menuManager.removeAll();
                final Object[] actions = SourceHandlerExtensionManager.findApplicableActions(viewer.getSelection());
                if (actions != null) for (final Object action : actions) {
                    if (action instanceof IAction) menuManager.add((IAction)action);
                }
            }
        }); 
        modelsGroup.setInput(vdb);

        final WorkbenchLabelProvider workbenchLabelProvider = new WorkbenchLabelProvider();
        otherFilesGroup = new TableAndButtonsGroup(pg, OTHER_FILES_GROUP, 1, new DefaultTableProvider<VdbEntry>() {

            @Override
            public VdbEntry[] getElements() {
                final Set<VdbEntry> entries = vdb.getEntries();
                return entries.toArray(new VdbEntry[entries.size()]);
            }
        }, new TextColumnProvider<VdbEntry>() {

            @Override
            public Image getImage( final VdbEntry element ) {
                return workbenchLabelProvider.getImage(element.findFileInWorkspace());
            }
            
            @Override
            public Image getImage() {
                return null;
            }

            @Override
            public String getName() {
                return FILE_COLUMN_NAME;
            }

            @Override
            public String getValue( final VdbEntry element ) {
                return element.getName().lastSegment();
            }
        }, pathColumnProvider, syncColumnProvider, descriptionColumnProvider);
        otherFilesGroup.add(otherFilesGroup.new AddButtonProvider() {

            @Override
            protected void add() {
                final ViewerFilter filter = new ViewerFilter() {

                    @Override
                    public boolean select( final Viewer viewer,
                                           final Object parent,
                                           final Object element ) {
                        if (element instanceof IContainer) return true;
                        final IFile file = (IFile)element;
                        if (ModelUtilities.isModelFile(file) || ModelUtil.isXsdFile(file) || ModelUtil.isVdbArchiveFile(file)) return false;
                        for (final VdbEntry entry : vdb.getEntries())
                            if (file.equals(entry.findFileInWorkspace())) return false;
                        return true;
                    }
                };
                final Object[] files = WidgetUtil.showWorkspaceObjectSelectionDialog(ADD_FILE_DIALOG_TITLE,
                                                                                     ADD_FILE_DIALOG_MESSAGE,
                                                                                     true,
                                                                                     null,
                                                                                     new ModelingResourceFilter(filter),
                                                                                     validator,
                                                                                     modelLabelProvider);
                for (final Object file : files)
                    vdb.addEntry(((IFile)file).getFullPath(), new NullProgressMonitor());
            }
        });
        otherFilesGroup.add(removeButtonProvider);
        otherFilesGroup.setInput(vdb);
        
        // ======================= DATA POLICIES GROUP START ================================
        dataRolesGroup = new TableAndButtonsGroup(pg, DATA_POLICY_GROUP, 1, new DefaultTableProvider<VdbDataRole>() {

            @Override
            public VdbDataRole[] getElements() {
                final Set<VdbDataRole> entries = vdb.getDataPolicyEntries();
                return entries.toArray(new VdbDataRole[entries.size()]);
            }
        }, new TextColumnProvider<VdbDataRole>() {

            @Override
            public Image getImage( final VdbDataRole element ) {
                return null;
            }

            @Override
            public String getName() {
                return DATA_POLICY_COLUMN_NAME;
            }
            
            @Override
            public Image getImage() {
                return null;
            }

            @Override
            public String getValue( final VdbDataRole element ) {
                return element.getName();
            }
        }, new TextColumnProvider<VdbDataRole>() {

            @Override
            public String getName() {
                return DESCRIPTION_COLUMN_NAME;
            }
            
            @Override
            public Image getImage() {
                return null;
            }

            @Override
            public String getValue( final VdbDataRole element ) {
                return element.getDescription();
            }

            @Override
            public boolean isEditable( final VdbDataRole element ) {
                return true;
            }

            @Override
            public void setValue( final VdbDataRole element,
                                  final String value ) {
                element.setDescription(value);
            }
        });
        
        dataRolesGroup.add(dataRolesGroup.new NewButtonProvider() {

            @Override
            public void selected(IStructuredSelection selection) {
                ContainerImpl tempContainer = null;
                try {
                    Collection<File> modelFiles = vdb.getModelFiles();

                    tempContainer = (ContainerImpl)ModelerCore.createContainer("tempVdbModelContainer"); //$NON-NLS-1$
                    ModelEditorImpl.setContainer(tempContainer);
                    for (File modelFile : modelFiles) {
                        boolean isVisible = true;

                        Resource r = tempContainer.getResource(URI.createFileURI(modelFile.getPath()), true);
                        if (isVisible && ModelUtil.isModelFile(r) && !ModelUtil.isXsdFile(r)) {
                            EObject firstEObj = r.getContents().get(0);
                            ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(firstEObj);
                            String mmURI = ma.getPrimaryMetamodelUri();
                            if (RelationalPackage.eNS_URI.equalsIgnoreCase(mmURI) ||
                            	XmlDocumentPackage.eNS_URI.equalsIgnoreCase(mmURI) ||
                            	WEB_SERVICES_VIEW_MODEL_URI.equalsIgnoreCase(mmURI)) {
                                // DO NOTHING. This leaves the resource in the temp container
                            } else {
                                tempContainer.getResources().remove(r);
                            }
                        } else {
                            tempContainer.getResources().remove(r);
                        }
                    }
                } catch (CoreException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    ModelEditorImpl.setContainer(null);
                }

                final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();
                final NewDataRoleWizard wizard = new NewDataRoleWizard(tempContainer, null);

				wizard.init(iww.getWorkbench(), new StructuredSelection(vdb
						.getModelEntries()));
				final WizardDialog dialog = new WizardDialog(wizard.getShell(),
						wizard);
				final int rc = dialog.open();
				if( rc == Window.OK ) {
					// Get the Data Policy
					DataRole dp = wizard.getDataRole();
					if( dp != null ) {
						vdb.addDataPolicy(dp, new NullProgressMonitor());
					}

				}
            }
        });
        dataRolesGroup.add(dataRolesGroup.new EditButtonProvider() {

            @Override
            public void selected( IStructuredSelection selection ) {
                VdbDataRole vdbDataRole = (VdbDataRole)selection.getFirstElement();
                if (vdbDataRole == null) {
                    return;
                }
                ContainerImpl tempContainer = null;
                try {
                    Collection<File> modelFiles = vdb.getModelFiles();

                    tempContainer = (ContainerImpl)ModelerCore.createContainer("tempVdbModelContainer"); //$NON-NLS-1$
                    ModelEditorImpl.setContainer(tempContainer);
                    for (File modelFile : modelFiles) {
                        boolean isVisible = true;

                        Resource r = tempContainer.getResource(URI.createFileURI(modelFile.getPath()), true);
                        if (isVisible && ModelUtil.isModelFile(r) && !ModelUtil.isXsdFile(r)) {
                            EObject firstEObj = r.getContents().get(0);
                            ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(firstEObj);
                            String mmURI = ma.getPrimaryMetamodelUri();
                            if (RelationalPackage.eNS_URI.equalsIgnoreCase(mmURI) ||
                                	XmlDocumentPackage.eNS_URI.equalsIgnoreCase(mmURI) ||
                                	WEB_SERVICES_VIEW_MODEL_URI.equalsIgnoreCase(mmURI)) {
                                // DO NOTHING. This leaves the resource in the temp container
                            } else {
                                tempContainer.getResources().remove(r);
                            }
                        } else {
                            tempContainer.getResources().remove(r);
                        }
                    }
                } catch (CoreException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    ModelEditorImpl.setContainer(null);
                }

                DataRole dataPolicy = 
                	new DataRole(vdbDataRole.getName(),
                			vdbDataRole.getDescription(), 
                			vdbDataRole.isAnyAuthenticated(), vdbDataRole.allowCreateTempTables(),
                			vdbDataRole.getMappedRoleNames(), vdbDataRole.getPermissions());

                final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();
                final NewDataRoleWizard wizard = new NewDataRoleWizard(tempContainer, dataPolicy);
                
                wizard.init(iww.getWorkbench(), new StructuredSelection(vdb.getModelEntries()));
                final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
                final int rc = dialog.open();
                if (rc == Window.OK ) {
                    // Get the Data Policy
                    DataRole dp = wizard.getDataRole();
                    if( dp != null ) {
                        vdb.removeDataPolicy(vdbDataRole);
                        vdb.addDataPolicy(dp, new NullProgressMonitor());
                    }

                }
            }
        });
        dataRolesGroup.add(dataRolesGroup.new RemoveButtonProvider() {

            @Override
            public void selected(IStructuredSelection selection) {
                
                for (final Object element : selection.toList()) {
                    if( element instanceof VdbDataRole ) {
                    	vdb.removeDataPolicy((VdbDataRole)element);
                    }

                }
            }
        });
        dataRolesGroup.setInput(vdb);
        
        this.cloneDataRoleAction = new Action(CLONE_DATA_ROLE_LABEL) {
        	
            @Override
            public void run() {
            	
                if( selectedDataRole != null ) {
                    DataRole newDR = new DataRole(
                    		selectedDataRole.getName() + COPY_SUFFIX, 
                    		selectedDataRole.getDescription(),
                    		selectedDataRole.isAnyAuthenticated(),
                    		selectedDataRole.allowCreateTempTables(),
                            selectedDataRole.getMappedRoleNames(), 
                            selectedDataRole.getPermissions());
                    vdb.addDataPolicy(newDR, new NullProgressMonitor());
                    dataRolesGroup.getTable().getViewer().refresh();
                }

            }
        };

        this.cloneDataRoleAction.setEnabled(true);
        
        // Add selection changed listener so if a Physical Source model is selected, the applicable menu actions are
        // retrieved via the SourceHandler extension point and interface.
        // This allows changing Translator and JNDI names via existing deployed objects on Teiid Servers that are
        // connected in the user's workspace.
        final TableViewer dataRolesViewer = dataRolesGroup.getTable().getViewer();
        final MenuManager dataRolesMenuManager = new MenuManager();
        dataRolesViewer.getControl().setMenu(dataRolesMenuManager.createContextMenu(pg));
        dataRolesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( final SelectionChangedEvent event ) {
            	dataRolesMenuManager.removeAll();
                IStructuredSelection sel = (IStructuredSelection)dataRolesViewer.getSelection();
                if( sel.size() == 1 ) {
                	selectedDataRole = (VdbDataRole)sel.getFirstElement();
                	dataRolesMenuManager.add(cloneDataRoleAction);
                }
                
            }
        }); 
        
        // ======================= DATA POLICIES GROUP END ================================
        
        synchronizeAllButton = WidgetFactory.createButton(pg, SYNCHRONIZE_ALL_BUTTON, GridData.HORIZONTAL_ALIGN_CENTER);
        synchronizeAllButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                IPreferenceStore prefStore = VdbUiPlugin.singleton.getPreferenceStore();
                boolean showWarningDialog = "".equals(prefStore.getString(SYNCHRONIZE_WITHOUT_WARNING)) ? true //$NON-NLS-1$
                                                                                                       : !prefStore.getBoolean(SYNCHRONIZE_WITHOUT_WARNING);
                boolean synchronize = !showWarningDialog;
                
                if (showWarningDialog) {
                    MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(Display.getCurrent()
                                                                                                        .getActiveShell(),
                                                                                                 CONFIRM_DIALOG_TITLE,
                                                                                                 CONFIRM_SYNCHRONIZE_ALL_MESSAGE,
                                                                                                 VdbUiConstants.Util.getString("rememberMyDecision"), //$NON-NLS-1$
                                                                                                 false,
                                                                                                 null,
                                                                                                 null);
                    if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
                        synchronize = true;

                        // save if user wants decision remembered
                        if (dialog.getToggleState()) {
                            try {
                                prefStore.setValue(SYNCHRONIZE_WITHOUT_WARNING, true);
                                VdbUiPlugin.singleton.getPreferences().flush();
                            } catch (BackingStoreException e) {
                                VdbUiConstants.Util.log(e);
                            }
                        }
                    }
                }
                
                if (synchronize) {
                    BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                        @Override
                        public void run() {
                            vdb.synchronize(new NullProgressMonitor());
                            modelsGroup.getTable().getViewer().refresh();
                            otherFilesGroup.getTable().getViewer().refresh();
                            
                            dataRoleResolver.allSynchronized();
                            VdbEditor.this.doSave(new NullProgressMonitor());
                        }
                    });
                }
            }
        });
        synchronizeAllButton.setEnabled(!vdb.isSynchronized());

        group.setFont(modelsGroup.getGroup().getFont());

        // pack and resize:
        pg.pack(true);
        
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
    	this.disposed = true;
        if (vdb != null) try {
            vdb.removeChangeListener(vdbListener);
            vdb.close();
            if (textEditor != null) textEditor.dispose();
        } catch (final Exception err) {
            VdbUiConstants.Util.log(err);
            WidgetUtil.showError(err);
        }
        
        // Un-Register this for Resource change events
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        
        super.dispose();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( final IProgressMonitor monitor ) {
        vdb.save(monitor);
        try {
            vdb.getFile().getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
        } catch (final CoreException error) {
            VdbUiConstants.Util.log(error);
            WidgetUtil.showError(error);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        // TODO: implement
    }

    /**
     * @return the VDB being edited
     */
    public Vdb getVdb() {
        return vdb;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init( final IEditorSite site,
                      final IEditorInput input ) {
        final IFile file = ((IFileEditorInput)input).getFile();
        vdb = new Vdb(file, new NullProgressMonitor());
        vdbListener = new PropertyChangeListener() {

            @Override
            public void propertyChange( final PropertyChangeEvent event ) {
                UiUtil.runInSwtThread(new Runnable() {
                    @Override
                    public void run() {
                    	if( !disposed ) {
                    		vdbNotification(event.getPropertyName());
                    	}
                    }
                }, true);
            }
        };
        vdb.addChangeListener(vdbListener);
        setSite(site);
        setInput(input);
        setPartName(file.getName());
        
        dataRoleResolver = new VdbDataRoleResolver(vdb);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return vdb.isModified();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    void openEditor( final VdbEntry entry ) {
        try {
            IDE.openEditor(UiUtil.getWorkbenchPage(), entry.findFileInWorkspace());
        } catch (final Exception error) {
            throw new RuntimeException(error);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
    }

    void vdbNotification( final String property ) {
        if (CLOSED.equals(property)) return;
        if (ENTRY_SYNCHRONIZATION.equals(property) || MODEL_TRANSLATOR.equals(property)
            || MODEL_JNDI_NAME.equals(property)) {
            modelsGroup.getTable().getViewer().refresh();
            otherFilesGroup.getTable().getViewer().refresh();
            modelsGroup.getTable().getViewer().getTable().redraw(); // needed to update the synchronized image
        }
        boolean syncChanged = false;
        for(VdbEntry entry : vdb.getEntries() ) {
        	if( entry.getSynchronization() == Synchronization.NotSynchronized ) {
        		syncChanged = true;
        		break;
        	}
        }
        for(VdbEntry entry : vdb.getModelEntries() ) {
        	if( entry.getSynchronization() == Synchronization.NotSynchronized ) {
        		syncChanged = true;
        		break;
        	}
        }
        synchronizeAllButton.setEnabled(syncChanged);
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }
    
    /**
     * @param event 
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged( IResourceChangeEvent event ) {
        int type = event.getType();
        if (type == IResourceChangeEvent.POST_CHANGE) {
            try {
                IResourceDelta delta = event.getDelta();
                if (delta != null) {
                    delta.accept(new IResourceDeltaVisitor() {

                        public boolean visit( IResourceDelta delta ) {
                        	
                            if (delta.getResource().equals(vdb.getFile()) && ((delta.getKind() & IResourceDelta.REMOVED) != 0)) {
                                Display.getDefault().asyncExec(new Runnable() {

                                    public void run() {
                                        if (Display.getDefault().isDisposed()) {
                                            return;
                                        }
                                        if (UiPlugin.getDefault().getCurrentWorkbenchWindow() != null
                                            && UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage() != null) {
                                            UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().closeEditor(VdbEditor.this,
                                                                                                                          false);
                                        }
                                    }
                                });
                                return false;
                            }
                            return true;
                        }
                    });

                }
            } catch (CoreException e) {
                UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
    }

}
