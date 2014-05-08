/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.ui.editor;

import static org.teiid.core.designer.util.StringConstants.EMPTY_STRING;
import static org.teiid.designer.vdb.Vdb.Event.CLOSED;
import static org.teiid.designer.vdb.Vdb.Event.DATA_POLICY_ADDED;
import static org.teiid.designer.vdb.Vdb.Event.DATA_POLICY_REMOVED;
import static org.teiid.designer.vdb.Vdb.Event.ENTRY_SYNCHRONIZATION;
import static org.teiid.designer.vdb.Vdb.Event.MODEL_JNDI_NAME;
import static org.teiid.designer.vdb.Vdb.Event.MODEL_TRANSLATOR;
import static org.teiid.designer.vdb.ui.preferences.VdbPreferenceConstants.SYNCHRONIZE_WITHOUT_WARNING;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.EditorPart;
import org.osgi.service.prefs.BackingStoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.VdbHelper;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ResourceChangeUtilities;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.table.CheckBoxColumnProvider;
import org.teiid.designer.ui.common.table.DefaultTableProvider;
import org.teiid.designer.ui.common.table.TableAndToolBar;
import org.teiid.designer.ui.common.table.TextColumnProvider;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.ui.common.widget.ButtonProvider;
import org.teiid.designer.ui.common.widget.DefaultContentProvider;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.properties.extension.VdbFileDialogUtil;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbEntry.Synchronization;
import org.teiid.designer.vdb.VdbFileEntry;
import org.teiid.designer.vdb.VdbFileEntry.FileEntryType;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.connections.SourceHandlerExtensionManager;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.VdbUiConstants.Images;
import org.teiid.designer.vdb.ui.VdbUiPlugin;
import org.teiid.designer.vdb.ui.editor.panels.DataRolesPanel;
import org.teiid.designer.vdb.ui.editor.panels.DescriptionPanel;
import org.teiid.designer.vdb.ui.editor.panels.ModelDetailsPanel;
import org.teiid.designer.vdb.ui.editor.panels.PropertiesPanel;
import org.teiid.designer.vdb.ui.editor.panels.UserDefinedPropertiesPanel;
import org.teiid.designer.vdb.ui.translators.TranslatorOverridesPanel;


/**
 * @since 8.0
 */
// TODO: read-only, undo/redo, function model 259
public final class VdbEditor extends EditorPart implements IResourceChangeListener {

    static final String MODEL_COLUMN_NAME = i18n("modelColumnName"); //$NON-NLS-1$
    static final String FILE_COLUMN_NAME = i18n("fileColumnName"); //$NON-NLS-1$
    static final String PATH_COLUMN_NAME = i18n("pathColumnName"); //$NON-NLS-1$
    static final String VDB_LOC_COLUMN_NAME = i18n("locationInVdbColumnName"); //$NON-NLS-1$
    static final String SYNCHRONIZED_COLUMN_NAME = i18n("synchronizedColumnName"); //$NON-NLS-1$
    static final String VISIBLE_COLUMN_NAME = i18n("visibleColumnName"); //$NON-NLS-1$;
    static final String DESCRIPTION_COLUMN_NAME = i18n("descriptionColumnName"); //$NON-NLS-1$;

    static final String SYNCHRONIZED_TOOLTIP = i18n("synchronizedTooltip"); //$NON-NLS-1$
    static final String UNSYNCHRONIZED_TOOLTIP = i18n("unsynchronizedTooltip"); //$NON-NLS-1$
    static final String SYNCHRONIZATION_NOT_APPLICABLE_TOOLTIP = i18n("synchronizationNotApplicableTooltip"); //$NON-NLS-1$

    static final String VISIBLE_TOOLTIP = i18n("visibleTooltip"); //$NON-NLS-1$
    static final String NOT_VISIBLE_TOOLTIP = i18n("notVisibleTooltip"); //$NON-NLS-1$

    static final String ADD_FILE_DIALOG_TITLE = i18n("addFileDialogTitle"); //$NON-NLS-1$
    static final String ADD_FILE_DIALOG_MESSAGE = i18n("addFileDialogMessage"); //$NON-NLS-1$
    static final String ADD_FILE_DIALOG_INVALID_SELECTION_MESSAGE = i18n("addFileDialogInvalidSelectionMessage"); //$NON-NLS-1$
    static final String ADD_FILE_DIALOG_NON_MODEL_SELECTED_MESSAGE = i18n("addFileDialogNonModelSelectedMessage"); //$NON-NLS-1$
    static final String ADD_FILE_DIALOG_VDB_SOURCE_MODEL_SELECTED_MESSAGE = i18n("addFileDialogVdbSourceModelSelectedMessage");  //$NON-NLS-1$
    static final String ADD_FILE_DIALOG_MODEL_WITH_SAME_NAME_EXISTS_SELECTED_MESSAGE = i18n("addFileDialogModelWithSameNameExistsSelectedMessage");  //$NON-NLS-1$
    
    
    static final String CONFIRM_DIRTY_MODELS_DIALOG_TITLE = i18n("confirmDirtyModelsDialogTitle"); //$NON-NLS-1$
    static final String CONFIRM_DIRTY_MODELS_DIALOG_MESSAGE= i18n("confirmDirtyModelsSynchronizeMessage"); //$NON-NLS-1$
    static final String CONFIRM_DIALOG_TITLE = i18n("confirmDialogTitle"); //$NON-NLS-1$
    static final String CONFIRM_SYNCHRONIZE_MESSAGE = i18n("confirmSynchronizeMessage"); //$NON-NLS-1$
    static final String CONFIRM_SYNCHRONIZE_ALL_MESSAGE = i18n("confirmSynchronizeAllMessage"); //$NON-NLS-1$
    static final String CONFIRM_REMOVE_MESSAGE = i18n("confirmRemoveMessage"); //$NON-NLS-1$
    static final String CONFIRM_REMOVE_IMPORTED_BY_MESSAGE = i18n("confirmRemoveImportedByMessage"); //$NON-NLS-1$
    static final String CONFIRM_OVERWRITE_USERFILE_MESSAGE = i18n("confirmOverwriteUserFileMessage"); //$NON-NLS-1$
    static final String CONFIRM_OVERWRITE_UDFJAR_MESSAGE = i18n("confirmOverwriteUdfJarMessage"); //$NON-NLS-1$
    static final String INFORM_DATA_ROLES_ON_ADD_MESSAGE = i18n("informDataRolesExistOnAddMessage"); //$NON-NLS-1$
    static final String CANNOT_ADD_DUPLICATE_MODEL_NAME_TITLE = i18n("cannotAddDuplicateModelNameTitle"); //$NON-NLS-1$
    static final String CANNOT_ADD_DUPLICATE_MODEL_NAME_MESSAGE = i18n("cannotAddDuplicateModelNameMessage"); //$NON-NLS-1$
    
    static final int MODELS_PANEL_WIDTH_HINT = 300;  // Models Panel Overall Width
    static final int MODELS_PANEL_IMAGE_COL_WIDTH = 50;  // Image Cols Width
    static final int MODELS_PANEL_MODELNAME_COL_WIDTH_MIN = 200;  // Min ModelName Width
    
//    static final String WEB_SERVICES_VIEW_MODEL_URI = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$

    static String i18n( final String id ) {
        return VdbUiConstants.Util.getString(id);
    }

    Vdb vdb;

    TableAndToolBar<VdbModelEntry> modelsGroup;
    ModelDetailsPanel modelDetailsPanel;
    TableAndToolBar<VdbEntry> otherFilesGroup;
    TableAndToolBar<VdbEntry> udfJarsGroup;
    private Button synchronizeAllButton;
    Button showImportVdbsButton;
    Label validationDateTimeLabel;
    Label validationVersionLabel;
    private PropertyChangeListener vdbListener;

    DataRolesPanel dataRolesPanel;
    VdbDataRoleResolver dataRoleResolver;
    TranslatorOverridesPanel pnlTranslatorOverrides;
    PropertiesPanel propertiesPanel;
    UserDefinedPropertiesPanel userDefinedPropertiesPanel;
    DescriptionPanel descriptionPanel;
    
    boolean disposed = false;

    private final TextColumnProvider<VdbEntry> descriptionColumnProvider = new TextColumnProvider<VdbEntry>() {
        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ColumnProvider#getName()
         */
        @Override
        public String getName() {
            return DESCRIPTION_COLUMN_NAME;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ColumnProvider#getImage()
         */
        @Override
        public Image getImage() {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ColumnProvider#getValue(java.lang.Object)
         */
        @Override
        public String getValue( final VdbEntry element ) {
            return element.getDescription();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#isEditable(java.lang.Object)
         */
        @Override
        public boolean isEditable( final VdbEntry element ) {
            return true;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#setValue(java.lang.Object, java.lang.Object)
         */
        @Override
        public void setValue( final VdbEntry element,
                              final String value ) {
            element.setDescription(value);
        }
    };

    private final ModelLabelProvider modelLabelProvider = new ModelLabelProvider();
    
    private final TextColumnProvider<VdbEntry> locationInVdbColumnProvider = new TextColumnProvider<VdbEntry>() {
        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ColumnProvider#getName()
         */
        @Override
        public String getName() {
            return VDB_LOC_COLUMN_NAME;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ColumnProvider#getImage()
         */
        @Override
        public Image getImage() {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ColumnProvider#getValue(java.lang.Object)
         */
        @Override
        public String getValue( final VdbEntry element ) {
            return element.getName().removeLastSegments(1).toString();
        }
    };

    private final CheckBoxColumnProvider<VdbEntry> syncColumnProvider = new CheckBoxColumnProvider<VdbEntry>() {
        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ColumnProvider#getName()
         */
        @Override
        public String getName() {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ColumnProvider#getImage()
         */
        @Override
        public Image getImage() {
            return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.SYNCHRONIZE_MODELS_ICON);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#getToolTip(java.lang.Object)
         */
        @Override
        public String getToolTip( final VdbEntry element ) {
            if (element.getSynchronization() == Synchronization.Synchronized) return SYNCHRONIZED_TOOLTIP;
            if (element.getSynchronization() == Synchronization.NotSynchronized) return UNSYNCHRONIZED_TOOLTIP;
            return SYNCHRONIZATION_NOT_APPLICABLE_TOOLTIP;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ColumnProvider#getValue(java.lang.Object)
         */
        @Override
        public Boolean getValue( final VdbEntry element ) {
            return element.getSynchronization() == Synchronization.Synchronized;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#isEditable(java.lang.Object)
         */
        @Override
        public boolean isEditable( final VdbEntry element ) {
            return element.getSynchronization() == Synchronization.NotSynchronized;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#setValue(java.lang.Object, java.lang.Object)
         */
        @Override
        public void setValue( final VdbEntry element,
                              final Boolean value ) {
            IPreferenceStore prefStore = VdbUiPlugin.singleton.getPreferenceStore();
            // set value to true if preference not found
            boolean showWarningDialog = "".equals(prefStore.getString(SYNCHRONIZE_WITHOUT_WARNING)) ? true //$NON-NLS-1$
            : !prefStore.getBoolean(SYNCHRONIZE_WITHOUT_WARNING);
            boolean synchronize = !showWarningDialog;
            
            boolean okIfModelsDirty = true;

            // check Workspace for Dirty Open Editors
            Collection<IFile> iFiles = VdbUtil.getVdbModels(getVdb());
            boolean askedQuestion = false;
            for( IFile theFile : iFiles ) {
            	if( ModelEditorManager.isOpen(theFile) && ModelEditorManager.getModelEditorForFile(theFile, false).isDirty() ) {
            		askedQuestion = true;
            		boolean confirm = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
            				CONFIRM_DIRTY_MODELS_DIALOG_TITLE,
            				CONFIRM_DIRTY_MODELS_DIALOG_MESSAGE);
                    if (confirm) {
                    	okIfModelsDirty = true;
                    } else {
                    	okIfModelsDirty = false;
                    }
            	}
            	if( askedQuestion ) {
            		break;
            	}
            }

            boolean hasDataRoles = !getVdb().getDataPolicyEntries().isEmpty();
            if (okIfModelsDirty && showWarningDialog) {
            	if( hasDataRoles ) {
	                MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(Display.getCurrent().getActiveShell(),
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
            	} else {
            		synchronize = true;
            	}
            }

            if (okIfModelsDirty && synchronize) {
                UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                    /**
                     * {@inheritDoc}
                     * 
                     * @see java.lang.Runnable#run()
                     */
                    @Override
                    public void run() {
                        element.synchronize(new NullProgressMonitor());
                        dataRoleResolver.modelSynchronized(element);
                        VdbEditor.this.doSave(new NullProgressMonitor());
                        showImportVdbsButton.setEnabled(!getVdb().getImportVdbEntries().isEmpty());
                    }
                });
            }
        }
    };

    private final ISelectionStatusValidator validator = new ISelectionStatusValidator() {
        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
         */
        @Override
        public IStatus validate( final Object[] selection ) {
            for (int ndx = selection.length; --ndx >= 0;)
                if (selection[ndx] instanceof IContainer) return new Status(IStatus.ERROR, VdbUiConstants.PLUGIN_ID, 0,
                                                                            ADD_FILE_DIALOG_INVALID_SELECTION_MESSAGE, null);
            return new Status(IStatus.OK, VdbUiConstants.PLUGIN_ID, 0, EMPTY_STRING, null);
        }
    };
    
    private final ISelectionStatusValidator modelSelectionValidator = new ISelectionStatusValidator() {
        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
         */
        @Override
        public IStatus validate( final Object[] selection ) {
            for (int ndx = selection.length; --ndx >= 0;) {
            	Object obj = selection[ndx];
                if (obj instanceof IContainer) {
                	return new Status(IStatus.ERROR, VdbUiConstants.PLUGIN_ID, 0, ADD_FILE_DIALOG_INVALID_SELECTION_MESSAGE, null);
                } else if( obj instanceof IFile ) {
                	IFile file = (IFile)obj;
                
                	if ( !ModelUtilities.isModelFile(file) && !ModelUtil.isXsdFile(file) ) {
                		return new Status(IStatus.ERROR, VdbUiConstants.PLUGIN_ID, 0, ADD_FILE_DIALOG_NON_MODEL_SELECTED_MESSAGE, null); 
                	}
                	if( ModelUtilities.isVdbSourceModel(file) ) {
                		return new Status(IStatus.ERROR, VdbUiConstants.PLUGIN_ID, 0, ADD_FILE_DIALOG_VDB_SOURCE_MODEL_SELECTED_MESSAGE, null);
                	}
                	
                	// Check for duplicate model and/or user file names
                	if( VdbUtil.modelAlreadyExistsInVdb(file.getFullPath().removeFileExtension().lastSegment(), file.getFullPath(), getVdb()) ) {
                		return new Status(IStatus.ERROR, VdbUiConstants.PLUGIN_ID, 0, 
                				ADD_FILE_DIALOG_MODEL_WITH_SAME_NAME_EXISTS_SELECTED_MESSAGE, null);
                	}
                }
            }
            
            return new Status(IStatus.OK, VdbUiConstants.PLUGIN_ID, 0, EMPTY_STRING, null);
        }
    };

    /**
     * Method which adds models to the VDB.
     * 
     * @param models
     */
    public void addModels( final List<IFile> models ) {
        for (final Object model : models) {
            vdb.addModelEntry(((IFile)model).getFullPath(), new NullProgressMonitor());
        }

        modelsGroup.getTable().getViewer().refresh();
        pnlTranslatorOverrides.refresh();
        packModelsGroup();
        
        showImportVdbsButton.setEnabled(!getVdb().getImportVdbEntries().isEmpty());
        
        udfJarsGroup.getTable().getViewer().refresh();
        otherFilesGroup.getTable().getViewer().refresh();
    }
    
    /*
     * Set the image cols to a fixed width, then use the remainder for Model Name column
     */
    void packModelsGroup() {
    	// Set Image Col Widths
        modelsGroup.getTable().getColumn(1).getColumn().setWidth(MODELS_PANEL_IMAGE_COL_WIDTH);
        modelsGroup.getTable().getColumn(2).getColumn().setWidth(MODELS_PANEL_IMAGE_COL_WIDTH);
        
        // Get Overall area width
        Rectangle area = modelsGroup.getTable().getViewer().getTable().getClientArea();
        int totalAreaWidth = area.width;
        
        // Use the Minimum for ModelName Column
        int col1Width = totalAreaWidth - MODELS_PANEL_IMAGE_COL_WIDTH - MODELS_PANEL_IMAGE_COL_WIDTH;
        if(col1Width<MODELS_PANEL_MODELNAME_COL_WIDTH_MIN) col1Width=MODELS_PANEL_MODELNAME_COL_WIDTH_MIN;
        modelsGroup.getTable().getColumn(0).getColumn().setWidth(col1Width);
    }

    private void createEditorBottom( Composite parent ) {
        Composite pnlBottom = new Composite(parent, SWT.BORDER);
        pnlBottom.setLayout(new GridLayout());
        pnlBottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        CTabFolder tabFolder = WidgetFactory.createTabFolder(pnlBottom);

        { // roles tab
            CTabItem rolesTab = new CTabItem(tabFolder, SWT.NONE);
            rolesTab.setText(i18n("rolesTab")); //$NON-NLS-1$
            rolesTab.setToolTipText(i18n("rolesTabToolTip")); //$NON-NLS-1$
            Composite pnlRoles = new Composite(tabFolder, SWT.NONE);
            pnlRoles.setLayout(new GridLayout());
            pnlRoles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            rolesTab.setControl(pnlRoles);
            dataRolesPanel = new DataRolesPanel(pnlRoles, this);
        }

        { // properties tab
            CTabItem propertiesTab = new CTabItem(tabFolder, SWT.NONE);
            propertiesTab.setText(i18n("propertiesTab")); //$NON-NLS-1$
            propertiesTab.setToolTipText(i18n("propertiesTabToolTip")); //$NON-NLS-1$
            Composite pnlProperties = new Composite(tabFolder, SWT.NONE);
            pnlProperties.setLayout(new GridLayout());
            pnlProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            propertiesTab.setControl(pnlProperties);
            propertiesPanel = new PropertiesPanel(pnlProperties, this);
        }
        
        { // properties tab
            CTabItem tab = new CTabItem(tabFolder, SWT.NONE);
            tab.setText(i18n("userDefinedPropertiesTab")); //$NON-NLS-1$
            tab.setToolTipText(i18n("userDefinedPropertiesTabToolTip")); //$NON-NLS-1$
            Composite pnlProperties = new Composite(tabFolder, SWT.NONE);
            pnlProperties.setLayout(new GridLayout());
            pnlProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            tab.setControl(pnlProperties);
            userDefinedPropertiesPanel = new UserDefinedPropertiesPanel(pnlProperties, this);
        }
        
        { // description tab
            CTabItem descriptionTab = new CTabItem(tabFolder, SWT.NONE);
            descriptionTab.setText(i18n("descriptionTab")); //$NON-NLS-1$
            descriptionTab.setToolTipText(i18n("descriptionTabToolTip")); //$NON-NLS-1$
            Composite pnlDescription = new Composite(tabFolder, SWT.NONE);
            pnlDescription.setLayout(new GridLayout());
            pnlDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            descriptionTab.setControl(pnlDescription);
            descriptionPanel = new DescriptionPanel(pnlDescription, this);
        }

        { // translator overrides tab
            CTabItem translatorOverridesTab = new CTabItem(tabFolder, SWT.NONE);
            translatorOverridesTab.setText(i18n("translatorOverridesTab")); //$NON-NLS-1$
            translatorOverridesTab.setToolTipText(i18n("translatorOverridesTabToolTip")); //$NON-NLS-1$
            pnlTranslatorOverrides = new TranslatorOverridesPanel(tabFolder, this.vdb);
            translatorOverridesTab.setControl(pnlTranslatorOverrides);
        }

        tabFolder.setSelection(0);
    }

    void addSelectionToVdb(VdbFolders vdbFolder, TableViewer tableViewer,
                                           FileEntryType fileEntryType, String confirmOverwriteMessage) {
        // userFile Selection Dialog
        final Shell shell = Display.getCurrent().getActiveShell();

        // Get the VDB Project
        IProject vdbProject = getVdb().getFile().getProject();
        String fileStr = VdbFileDialogUtil.selectFile(shell, vdbProject, vdbFolder);

        List<File> files = new ArrayList<File>();
        if(fileStr!=null && !fileStr.trim().isEmpty()) {
            File theFile = VdbHelper.findVdbFile(fileStr,vdbProject);
            if(theFile!=null && theFile.exists()) {
                files.add(theFile);
            }
        }

        // indicates if this is the first time models are being added
        boolean firstTime = (tableViewer.getTable().getItemCount() == 0);

        // Add the VdbFileEntry for userFile to the VDB
        for (final File file : files) {
            String fileName = file.getName();
            IPath filePath = new Path(file.getAbsolutePath());
            // Check the selected file name against current entries.  If duplicate name, prompt for overwrite
            Set<VdbEntry> currentUserFiles = getVdb().getUserFileEntries();
            // Matching entry - prompt user for overwrite
            if(entrySetContainsName(currentUserFiles, fileName)) {
                // Prompt user whether to overwrite
                if(ConfirmationDialog.confirm(new ConfirmationDialog(confirmOverwriteMessage))) {
                    // Find the matching entry
                    VdbEntry matchingEntry = null;
                    for(VdbEntry entry: currentUserFiles) {
                        String entryShortName = entry.getName().lastSegment();
                        if(entryShortName.equals(fileName)) {
                            matchingEntry = entry;
                            break;
                        }
                    }
                    // Remove the current entry
                    if(matchingEntry != null)
                        getVdb().removeEntry(matchingEntry);
                    // Add the selected file
                    getVdb().addFileEntry(filePath, fileEntryType, new NullProgressMonitor());
                }
            // No matching entries - safe to add the new entry
            } else {
                getVdb().addFileEntry(filePath, fileEntryType, new NullProgressMonitor());
            }
        }

        // refresh table from model
        tableViewer.refresh();

        // pack columns if first time a file is added
        if (firstTime) {
            WidgetUtil.pack(tableViewer);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void createUdfJarsControl( Composite parent ) {
        final WorkbenchLabelProvider workbenchLabelProvider = new WorkbenchLabelProvider();
        udfJarsGroup = new TableAndToolBar(parent, 1, new DefaultTableProvider<VdbEntry>() {
                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.DefaultTableProvider#doubleClicked(java.lang.Object)
                                                   */
                                                  @Override
                                                  public void doubleClicked( VdbEntry element ) {
                                                      openEditor(element);
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.TableProvider#getElements()
                                                   */
                                                  @Override
                                                  public VdbEntry[] getElements() {
                                                      Set<VdbEntry> udfJarEntries = getVdb().getUdfJarEntries();
                                                      return udfJarEntries.toArray(new VdbEntry[udfJarEntries.size()]);
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.DefaultTableProvider#isDoubleClickSupported()
                                                   */
                                                  @Override
                                                  public boolean isDoubleClickSupported() {
                                                      return false;
                                                  }

                                                  void openEditor( final VdbEntry entry ) {
                                                      try {
                                                          IDE.openEditor(UiUtil.getWorkbenchPage(), entry.findFileInWorkspace());
                                                      } catch (final Exception error) {
                                                          throw new RuntimeException(error);
                                                      }
                                                  }
                                              }, new TextColumnProvider<VdbEntry>() {
                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#getImage(java.lang.Object)
                                                   */
                                                  @Override
                                                  public Image getImage( final VdbEntry element ) {
                                                      return workbenchLabelProvider.getImage(element.findFileInWorkspace());
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.ColumnProvider#getImage()
                                                   */
                                                  @Override
                                                  public Image getImage() {
                                                      return null;
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.ColumnProvider#getName()
                                                   */
                                                  @Override
                                                  public String getName() {
                                                      return FILE_COLUMN_NAME;
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.ColumnProvider#getValue(java.lang.Object)
                                                   */
                                                  @Override
                                                  public String getValue( final VdbEntry element ) {
                                                      return element.getName().lastSegment();
                                                  }
                                              }, this.locationInVdbColumnProvider, this.descriptionColumnProvider);

        ButtonProvider addProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.ADD_FILE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("addUdfJarToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return true;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
                addSelectionToVdb(VdbFolders.UDF,
                                              udfJarsGroup.getTable().getViewer(),
                                              VdbFileEntry.FileEntryType.UDFJar,
                                              CONFIRM_OVERWRITE_UDFJAR_MESSAGE);
            }
        };
        udfJarsGroup.add(addProvider);

        ButtonProvider removeProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.REMOVE_FILE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("removeUdfJarToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return !selection.isEmpty();
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
                if (!ConfirmationDialog.confirm(CONFIRM_REMOVE_MESSAGE)) return;
                final Set<VdbEntry> entries = new HashSet<VdbEntry>();
                final Set<VdbEntry> importedBy = new HashSet<VdbEntry>();
                for (final Object element : selection.toList()) {
                    entries.add((VdbEntry)element);
                    if (element instanceof VdbModelEntry) importedBy.addAll(((VdbModelEntry)element).getImportedBy());
                }
                if (!importedBy.isEmpty()) importedBy.removeAll(entries);
                if (!importedBy.isEmpty()) {
                    if (!ConfirmationDialog.confirm(new ConfirmationDialog(CONFIRM_REMOVE_IMPORTED_BY_MESSAGE) {
                        /**
                         * {@inheritDoc}
                         * 
                         * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
                         */
                        @Override
                        protected Control createCustomArea( final Composite parent ) {
                            final ListViewer viewer = new ListViewer(parent);
                            viewer.setContentProvider(new DefaultContentProvider());
                            viewer.setLabelProvider(new LabelProvider() {
                                /**
                                 * {@inheritDoc}
                                 * 
                                 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
                                 */
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

                for (final VdbEntry entry : entries) {
                    getVdb().removeEntry(entry);
                }
                showImportVdbsButton.setEnabled(!getVdb().getImportVdbEntries().isEmpty());
            }
        };

        udfJarsGroup.add(removeProvider);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void createOtherFilesControl( Composite parent ) {
        final WorkbenchLabelProvider workbenchLabelProvider = new WorkbenchLabelProvider();
        otherFilesGroup = new TableAndToolBar(parent, 1, new DefaultTableProvider<VdbEntry>() {
                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.DefaultTableProvider#doubleClicked(java.lang.Object)
                                                   */
                                                  @Override
                                                  public void doubleClicked( VdbEntry element ) {
                                                      openEditor(element);
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.TableProvider#getElements()
                                                   */
                                                  @Override
                                                  public VdbEntry[] getElements() {
                                                      Set<VdbEntry> userFileEntries = getVdb().getUserFileEntries();
                                                      return userFileEntries.toArray(new VdbEntry[userFileEntries.size()]);
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.DefaultTableProvider#isDoubleClickSupported()
                                                   */
                                                  @Override
                                                  public boolean isDoubleClickSupported() {
                                                      return false;
                                                  }

                                                  void openEditor( final VdbEntry entry ) {
                                                      try {
                                                          IDE.openEditor(UiUtil.getWorkbenchPage(), entry.findFileInWorkspace());
                                                      } catch (final Exception error) {
                                                          throw new RuntimeException(error);
                                                      }
                                                  }
                                              }, new TextColumnProvider<VdbEntry>() {
                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#getImage(java.lang.Object)
                                                   */
                                                  @Override
                                                  public Image getImage( final VdbEntry element ) {
                                                      return workbenchLabelProvider.getImage(element.findFileInWorkspace());
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.ColumnProvider#getImage()
                                                   */
                                                  @Override
                                                  public Image getImage() {
                                                      return null;
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.ColumnProvider#getName()
                                                   */
                                                  @Override
                                                  public String getName() {
                                                      return FILE_COLUMN_NAME;
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see org.teiid.designer.ui.common.table.ColumnProvider#getValue(java.lang.Object)
                                                   */
                                                  @Override
                                                  public String getValue( final VdbEntry element ) {
                                                      return element.getName().lastSegment();
                                                  }
                                              }, this.locationInVdbColumnProvider, this.descriptionColumnProvider);

        ButtonProvider addProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.ADD_FILE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("addFileToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return true;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
                addSelectionToVdb(VdbFolders.OTHER_FILES,
                                              otherFilesGroup.getTable().getViewer(),
                                              VdbFileEntry.FileEntryType.UserFile,
                                              CONFIRM_OVERWRITE_USERFILE_MESSAGE);
            }
        };
        otherFilesGroup.add(addProvider);

        ButtonProvider removeProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.REMOVE_FILE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("removeFileToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return !selection.isEmpty();
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
                if (!ConfirmationDialog.confirm(CONFIRM_REMOVE_MESSAGE)) return;
                final Set<VdbEntry> entries = new HashSet<VdbEntry>();
                final Set<VdbEntry> importedBy = new HashSet<VdbEntry>();
                for (final Object element : selection.toList()) {
                    entries.add((VdbEntry)element);
                    if (element instanceof VdbModelEntry)
                        importedBy.addAll(((VdbModelEntry)element).getImportedBy());
                }
                if (!importedBy.isEmpty()) importedBy.removeAll(entries);
                if (!importedBy.isEmpty()) {
                    if (!ConfirmationDialog.confirm(new ConfirmationDialog(CONFIRM_REMOVE_IMPORTED_BY_MESSAGE) {
                        /**
                         * {@inheritDoc}
                         * 
                         * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
                         */
                        @Override
                        protected Control createCustomArea( final Composite parent ) {
                            final ListViewer viewer = new ListViewer(parent);
                            viewer.setContentProvider(new DefaultContentProvider());
                            viewer.setLabelProvider(new LabelProvider() {
                                /**
                                 * {@inheritDoc}
                                 * 
                                 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
                                 */
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

                for (final VdbEntry entry : entries) {
                    getVdb().removeEntry(entry);
                }
                showImportVdbsButton.setEnabled(!getVdb().getImportVdbEntries().isEmpty());
            }
        };

        otherFilesGroup.add(removeProvider);
    }
    
    /*
     * Determine if the supplied VdbEntry set contains an entry with the supplied shortName
     * @param entries the VdbEntry set
     * @param shortName the entry short name
     * @return 'true' if the set contains an entry with the short name, 'false' if not. 
     */
    boolean entrySetContainsName(Set<VdbEntry> entries, String shortName) {
        List<String> currentNames = new ArrayList<String>(entries.size());

        for (VdbEntry entry : entries) {
            IPath entryPath = entry.getName();
            String entryName = entryPath.lastSegment();
            currentNames.add(entryName);

        }

        if (currentNames.contains(shortName))
            return true;
        return false;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( final Composite parent ) {
        parent.setLayout(new GridLayout());
        parent.setLayoutData(new GridData());

        // Insert a ScrolledComposite so controls don't disappear if the panel shrinks
        final ScrolledComposite scroller = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setLayout(new GridLayout());
        scroller.setLayoutData(new GridData(GridData.FILL_BOTH));
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);

        SashForm sash = new SashForm(scroller, SWT.VERTICAL);
        sash.setLayoutData(new GridData(GridData.FILL_BOTH));
        scroller.setContent(sash);

        createEditorTop(sash);
        createEditorBottom(sash);
        sash.setWeights(new int[] {50, 50});

        ModelerCore.getWorkspace().addResourceChangeListener(this);
        
        showImportVdbsButton.setEnabled(!getVdb().getImportVdbEntries().isEmpty());
    }

    private void createEditorTop( Composite parent ) {
        Composite pnlTop = new Composite(parent, SWT.BORDER);
        pnlTop.setLayout(new GridLayout());
        pnlTop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        CTabFolder tabFolder = WidgetFactory.createTabFolder(pnlTop);

        { // models tab
            CTabItem modelsTab = new CTabItem(tabFolder, SWT.NONE);
            modelsTab.setText(i18n("modelsTab")); //$NON-NLS-1$
            modelsTab.setToolTipText(i18n("modelsTabToolTip")); //$NON-NLS-1$
            
            Composite pnlModels = new Composite(tabFolder, SWT.NONE);
            pnlModels.setLayout(new GridLayout(1, false));
            pnlModels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            
            SashForm splitter = new SashForm(pnlModels,SWT.HORIZONTAL);
            splitter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            
            Composite pnlModelsList = new Composite(splitter, SWT.NONE);
            pnlModelsList.setLayout(new GridLayout());
            pnlModelsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            ((GridData)pnlModelsList.getLayoutData()).widthHint = MODELS_PANEL_WIDTH_HINT;
            
            createModelsSection(pnlModelsList);
            
            modelDetailsPanel = new ModelDetailsPanel(splitter, this);
            splitter.setWeights(new int[] {35, 65});
            
            modelsTab.setControl(pnlModels);
            
            
        }

        { // UDF jars tab
            CTabItem filesTab = new CTabItem(tabFolder, SWT.NONE);
            filesTab.setText(i18n("udfJarsTab")); //$NON-NLS-1$
            filesTab.setToolTipText(i18n("udfJarsTabToolTip")); //$NON-NLS-1$
            Composite pnlFiles = new Composite(tabFolder, SWT.NONE);
            pnlFiles.setLayout(new GridLayout());
            pnlFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            filesTab.setControl(pnlFiles);
            createUdfJarsControl(pnlFiles);
        }
        
        { // other files tab
            CTabItem filesTab = new CTabItem(tabFolder, SWT.NONE);
            filesTab.setText(i18n("filesTab")); //$NON-NLS-1$
            filesTab.setToolTipText(i18n("filesTabToolTip")); //$NON-NLS-1$
            Composite pnlFiles = new Composite(tabFolder, SWT.NONE);
            pnlFiles.setLayout(new GridLayout());
            pnlFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            filesTab.setControl(pnlFiles);
            createOtherFilesControl(pnlFiles);
        }

        Composite extraButtonPanel = WidgetFactory.createPanel(pnlTop);
        extraButtonPanel.setLayout(new GridLayout(6, false));
        { // synchronize button
        	
            synchronizeAllButton = WidgetFactory.createButton(extraButtonPanel, i18n("synchronizeAllButton"), //$NON-NLS-1$
                                                              GridData.HORIZONTAL_ALIGN_BEGINNING);
            synchronizeAllButton.setToolTipText(i18n("synchronizeAllButtonToolTip")); //$NON-NLS-1$
            synchronizeAllButton.addSelectionListener(new SelectionAdapter() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    IPreferenceStore prefStore = VdbUiPlugin.singleton.getPreferenceStore();
                    boolean showWarningDialog = "".equals(prefStore.getString(SYNCHRONIZE_WITHOUT_WARNING)) ? true //$NON-NLS-1$
                    : !prefStore.getBoolean(SYNCHRONIZE_WITHOUT_WARNING);
                    boolean synchronize = !showWarningDialog;
                    boolean okIfModelsDirty = true;

                    // check Workspace for Dirty Open Editors
                    Collection<IFile> iFiles = VdbUtil.getVdbModels(getVdb());
                    boolean askedQuestion = false;
                    for( IFile theFile : iFiles ) {
                    	if( ModelEditorManager.isOpen(theFile) && ModelEditorManager.getModelEditorForFile(theFile, false).isDirty() ) {
                    		askedQuestion = true;
                    		boolean confirm = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
                                    CONFIRM_DIRTY_MODELS_DIALOG_TITLE,
                    				CONFIRM_DIRTY_MODELS_DIALOG_MESSAGE);
                            if (confirm) {
                            	okIfModelsDirty = true;
                            } else {
                            	okIfModelsDirty = false;
                            }
                    	}
                    	if( askedQuestion ) {
                    		break;
                    	}
                    }
                    boolean hasDataRoles = !getVdb().getDataPolicyEntries().isEmpty();
                    if (okIfModelsDirty && showWarningDialog) {
                    	if( hasDataRoles ) {
	                        MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(Display.getCurrent().getActiveShell(),
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
                    	} else {
                    		synchronize = true;
                    	}
                    }

                    if (okIfModelsDirty && synchronize) {
                        UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                            /**
                             * {@inheritDoc}
                             * 
                             * @see java.lang.Runnable#run()
                             */
                            @Override
                            public void run() {
                                getVdb().synchronize(new NullProgressMonitor());
                                modelsGroup.getTable().getViewer().refresh();
                                otherFilesGroup.getTable().getViewer().refresh();
                                pnlTranslatorOverrides.refresh();
                                dataRoleResolver.allSynchronized();
                                VdbEditor.this.doSave(new NullProgressMonitor());
                                showImportVdbsButton.setEnabled(!getVdb().getImportVdbEntries().isEmpty());
                                modelDetailsPanel.refreshModelDetails();
                            }
                        });
                    }
                }
            });
            synchronizeAllButton.setEnabled(!vdb.isSynchronized());
        }
        { // synchronize button
            showImportVdbsButton = WidgetFactory.createButton(extraButtonPanel, "Show Import VDBs",//i18n("showImportVdbsButton"), //$NON-NLS-1$
                                                              GridData.HORIZONTAL_ALIGN_BEGINNING);
            showImportVdbsButton.setToolTipText(i18n("synchronizeAllButtonToolTip")); //$NON-NLS-1$
            showImportVdbsButton.addSelectionListener(new SelectionAdapter() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                	
                	ShowImportVdbsDialog dialog = new ShowImportVdbsDialog(Display.getCurrent().getActiveShell(), getVdb());
                	
                	dialog.open();
                }
            });
            showImportVdbsButton.setEnabled(!getVdb().getImportVdbEntries().isEmpty());
        }
        
        { // Validatin Info
        	Color blueColor = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
        	Label label1 = WidgetFactory.createLabel(extraButtonPanel, "  " + i18n("lastValidated") + " : "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        	GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(label1);
        	
        	String dateTimeString = i18n("undefined"); //$NON-NLS-1$
        	if( getVdb().getValidationDateTime() != null ) {
        		dateTimeString = getVdb().getValidationDateTime().toString();
        	}
        	this.validationDateTimeLabel = WidgetFactory.createLabel(extraButtonPanel, dateTimeString);
        	validationDateTimeLabel.setForeground(blueColor);
        	validationDateTimeLabel.setText(dateTimeString);
        	GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(validationDateTimeLabel);
        	
        	Label label3 = WidgetFactory.createLabel(extraButtonPanel, "    " + i18n("teiidRuntimeVersion") + " : "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        	GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(label3);
        	
        	String versionString = i18n("undefined"); //$NON-NLS-1$
        	if( getVdb().getValidationVersion() != null ) {
        		versionString = getVdb().getValidationVersion();
        	}
        	validationVersionLabel = WidgetFactory.createLabel(extraButtonPanel, versionString);
        	validationVersionLabel.setForeground(blueColor);
        	GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(validationVersionLabel);
        }
        
        // Set Vdb input on Udf and Other files tab.
        this.udfJarsGroup.setInput(vdb);
        this.otherFilesGroup.setInput(vdb);
        
        tabFolder.setSelection(0);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void createModelsSection( Composite parent ) {
        modelsGroup = new TableAndToolBar(parent, 1,
                                          new DefaultTableProvider<VdbModelEntry>() {
                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.DefaultTableProvider#doubleClicked(java.lang.Object)
                                               */
                                              @Override
                                              public void doubleClicked( final VdbModelEntry element ) {
                                                  openEditor(element);
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.TableProvider#getElements()
                                               */
                                              @Override
                                              public VdbModelEntry[] getElements() {
                                                  final Set<VdbModelEntry> modelEntries = getVdb().getModelEntries();
                                                  return modelEntries.toArray(new VdbModelEntry[modelEntries.size()]);
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.DefaultTableProvider#isDoubleClickSupported()
                                               */
                                              @Override
                                              public boolean isDoubleClickSupported() {
                                                  return true;
                                              }
                                          }, new TextColumnProvider<VdbModelEntry>() {
                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#getImage(java.lang.Object)
                                               */
                                              @Override
                                              public Image getImage( final VdbModelEntry element ) {
                                                  return ModelIdentifier.getModelImage(element.findFileInWorkspace());
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.ColumnProvider#getName()
                                               */
                                              @Override
                                              public String getName() {
                                                  return MODEL_COLUMN_NAME;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.ColumnProvider#getImage()
                                               */
                                              @Override
                                              public Image getImage() {
                                                  return null;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.ColumnProvider#getValue(java.lang.Object)
                                               */
                                              @Override
                                              public String getValue( final VdbModelEntry element ) {
                                                  return element.getName().lastSegment();
                                              }
                                          }, this.syncColumnProvider,
                                          new CheckBoxColumnProvider<VdbModelEntry>() {
                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.ColumnProvider#getName()
                                               */
                                              @Override
                                              public String getName() {
                                                  return null;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.ColumnProvider#getImage()
                                               */
                                              @Override
                                              public Image getImage() {
                                                  return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.VISIBLE_ICON);
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#getToolTip(java.lang.Object)
                                               */
                                              @Override
                                              public String getToolTip( final VdbModelEntry element ) {
                                                  return element.isVisible() ? VISIBLE_TOOLTIP : NOT_VISIBLE_TOOLTIP;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.ColumnProvider#getValue(java.lang.Object)
                                               */
                                              @Override
                                              public Boolean getValue( final VdbModelEntry element ) {
                                                  return element.isVisible();
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#isEditable(java.lang.Object)
                                               */
                                              @Override
                                              public boolean isEditable( final VdbModelEntry element ) {
                                                  return true;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#setValue(java.lang.Object,
                                               *      java.lang.Object)
                                               */
                                              @Override
                                              public void setValue( final VdbModelEntry element,
                                                                    final Boolean value ) {
                                                  element.setVisible(value);
                                              }
                                          });

        ButtonProvider addProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.ADD_MODEL);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("addModelToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return true;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
                final ViewerFilter filter = new ViewerFilter() {
                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
                     *      java.lang.Object)
                     */
                    @Override
                    public boolean select( final Viewer viewer,
                                           final Object parent,
                                           final Object element ) {
                        if (element instanceof IContainer) {
                            // Get the project for this container
                            IProject proj = ((IContainer)element).getProject();
                            
                            // Get the VDB Project
                            IProject vdbProject = getVdb().getFile().getProject();
                            
                            // Can only add Models from the VDB Project
                            if(proj.isOpen() && proj.equals(vdbProject)) {
                                return true;
                            } 
                            return false;
                        }
                        final IFile file = (IFile)element;
                        if (!ModelUtilities.isModelFile(file)) {
                        	return false;
                        }

                        if (ModelUtil.isXsdFile(file)) {
                            // xsd files now added as other files - TEIIDDES-2120
                            return false;
                        }

                        for (final VdbModelEntry modelEntry : getVdb().getModelEntries())
                            if (file.equals(modelEntry.findFileInWorkspace())) return false;
                        return true;
                    }
                };
                                
                ModelingResourceFilter wsFilter = new ModelingResourceFilter(filter);
                wsFilter.setShowHiddenProjects(false);
                final Object[] models = WidgetUtil.showWorkspaceObjectSelectionDialog(ADD_FILE_DIALOG_TITLE,
                                                                                      ADD_FILE_DIALOG_MESSAGE,
                                                                                      true,
                                                                                      null,
                                                                                      wsFilter,
                                                                                      getModelSelectionValidator(),
                                                                                      getModelLabelProvider());
                if (!getVdb().getDataPolicyEntries().isEmpty()) {
                    MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
                                                  VdbEditor.CONFIRM_DIALOG_TITLE,
                                                  INFORM_DATA_ROLES_ON_ADD_MESSAGE);
                }

                // indicates if this is the first time models are being added
                boolean firstTime = (modelsGroup.getTable().getViewer().getTable().getItemCount() == 0);
                
                boolean success = false;
                boolean foundDuplicateNames = false;
                boolean proceed = false;
                try {
					// Check to see if any of the models or dependents will result in duplicate model names in VDB
					for( final Object model : models ) {
						boolean canAdd = VdbUtil.canAddModelToVdb((IFile)model, getVdb());
						if( !canAdd ) {
							MessageDialog.openError(Display.getCurrent().getActiveShell(),
					                                  VdbEditor.CANNOT_ADD_DUPLICATE_MODEL_NAME_TITLE,
					                                  CANNOT_ADD_DUPLICATE_MODEL_NAME_MESSAGE);
							foundDuplicateNames = true;
						}
						break;
					}
					success = true;
				} catch (ModelWorkspaceException ex) {
					UiConstants.Util.log(IStatus.ERROR, ex, ex.getMessage());
				} finally {
					if( success ) {
						proceed = !foundDuplicateNames;
					}
				}
                
                if( proceed ) {
	                // add the models
	                for (final Object model : models) {
	                    getVdb().addModelEntry(((IFile)model).getFullPath(), new NullProgressMonitor());
	                }
                }

                // refresh table from model
                modelsGroup.getTable().getViewer().refresh();
                pnlTranslatorOverrides.refresh();
                udfJarsGroup.getTable().getViewer().refresh();

                // pack columns if first time a model is added
                if (firstTime) {
                	packModelsGroup();
                }
                
                showImportVdbsButton.setEnabled(!getVdb().getImportVdbEntries().isEmpty());
            }
        };

        modelsGroup.add(addProvider);

        ButtonProvider removeProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.REMOVE_MODEL);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("removeModelToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return !selection.isEmpty();
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.ui.common.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
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
                        /**
                         * {@inheritDoc}
                         * 
                         * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
                         */
                        @Override
                        protected Control createCustomArea( final Composite parent ) {
                            final ListViewer viewer = new ListViewer(parent);
                            viewer.setContentProvider(new DefaultContentProvider());
                            viewer.setLabelProvider(new LabelProvider() {
                                /**
                                 * {@inheritDoc}
                                 * 
                                 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
                                 */
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

                for (final VdbEntry entry : entries) {
                    getVdb().removeEntry(entry);
                }
                
                udfJarsGroup.getTable().getViewer().refresh();
                
                showImportVdbsButton.setEnabled(!getVdb().getImportVdbEntries().isEmpty());
            }
        };

        modelsGroup.add(removeProvider);

        // Add selection changed listener so if a Physical Source model is selected, the applicable menu actions are
        // retrieved via the SourceHandler extension point and interface.
        // This allows changing Translator and JNDI names via existing deployed objects on Teiid Instances that are
        // connected in the user's workspace.
        final TableViewer viewer = modelsGroup.getTable().getViewer();
        final MenuManager menuManager = new MenuManager();
        viewer.getControl().setMenu(menuManager.createContextMenu(parent));
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
                menuManager.removeAll();
                final Object[] actions = SourceHandlerExtensionManager.findApplicableActions(viewer.getSelection());
                if (actions != null) for (final Object action : actions) {
                    if (action instanceof IAction) menuManager.add((IAction)action);
                }
                
                // UPDATE MODEL DETAILS WIDGETS
                IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
                VdbModelEntry selectedEntry = null;
                if (sel.getFirstElement() instanceof VdbModelEntry) {
                	selectedEntry = (VdbModelEntry)sel.getFirstElement();
                }
                
                modelDetailsPanel.setSelectedVdbModelEntry(selectedEntry);
            }
        });

        modelsGroup.setInput(vdb);
        packModelsGroup();
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
            if( descriptionPanel != null ) {
            	descriptionPanel.close();
            }
        } catch (final Exception err) {
            VdbUiConstants.Util.log(err);
            WidgetUtil.showError(err);
        }

        // Un-Register this for Resource change events
        ModelerCore.getWorkspace().removeResourceChangeListener(this);

        super.dispose();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( final IProgressMonitor monitor ) {
    	vdb.setValidationDateTime(new Date());
    	vdb.setValidationVersion(ModelerCore.getTeiidServerVersion().toString());
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

    ModelLabelProvider getModelLabelProvider() {
        return this.modelLabelProvider;
    }

    ISelectionStatusValidator getValidator() {
        return this.validator;
    }
    
    ISelectionStatusValidator getModelSelectionValidator() {
        return this.modelSelectionValidator;
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
            /**
             * {@inheritDoc}
             * 
             * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
             */
            @Override
            public void propertyChange( final PropertyChangeEvent event ) {
                UiUtil.runInSwtThread(new Runnable() {
                    /**
                     * {@inheritDoc}
                     * 
                     * @see java.lang.Runnable#run()
                     */
                    @Override
                    public void run() {
                        if (!disposed) {
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
            IFile model = entry.findFileInWorkspace();

            // make sure model is found in workspace
            if (model != null) {
                IDE.openEditor(UiUtil.getWorkbenchPage(), model);
            }
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
        if (ENTRY_SYNCHRONIZATION.equals(property) || MODEL_TRANSLATOR.equals(property) || MODEL_JNDI_NAME.equals(property)) {
            modelsGroup.getTable().getViewer().refresh();
            otherFilesGroup.getTable().getViewer().refresh();
            modelsGroup.getTable().getViewer().getTable().redraw(); // needed to update the synchronized image
            modelDetailsPanel.refreshModelDetails();
        }
        if( DATA_POLICY_ADDED.equals(property) || DATA_POLICY_REMOVED.equals(property) ) {
        	dataRolesPanel.refresh();
        }
        boolean syncChanged = false;
        for (VdbEntry entry : vdb.getEntries()) {
            if (entry.getSynchronization() == Synchronization.NotSynchronized) {
                syncChanged = true;
                break;
            }
        }
        for (VdbEntry entry : vdb.getModelEntries()) {
            if (entry.getSynchronization() == Synchronization.NotSynchronized) {
                syncChanged = true;
                break;
            }
        }
        synchronizeAllButton.setEnabled(syncChanged);
    	String dateTimeString = i18n("undefined"); //$NON-NLS-1$
    	if( getVdb().getValidationDateTime() != null ) {
    		dateTimeString = getVdb().getValidationDateTime().toString();
    	}
    	this.validationDateTimeLabel.setText(dateTimeString);
    	String validationString = i18n("undefined"); //$NON-NLS-1$
    	if( getVdb().getValidationVersion() != null ) {
    		validationString = getVdb().getValidationVersion();
    	}
    	this.validationVersionLabel.setText(validationString);
    	
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    /**
     * @param event
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    @Override
    public void resourceChanged( IResourceChangeEvent event ) {
        int type = event.getType();
        if (type == IResourceChangeEvent.POST_CHANGE) {
            try {
                IResourceDelta delta = event.getDelta();
                if (delta != null) {
                    delta.accept(new IResourceDeltaVisitor() {
                        /**
                         * {@inheritDoc}
                         * 
                         * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
                         */
                        @Override
                        public boolean visit( IResourceDelta delta ) {
                            IResource resource = delta.getResource();

                            if (resource.equals(getVdb().getFile())
                                && ((delta.getKind() & IResourceDelta.REMOVED) != 0)) {
                                Display.getDefault().asyncExec(new Runnable() {
                                    /**
                                     * {@inheritDoc}
                                     * 
                                     * @see java.lang.Runnable#run()
                                     */
                                    @Override
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
                            
                            // see if resource is one in the VDB
                            if ((resource instanceof IFile) && ResourceChangeUtilities.isContentChanged(delta)) {
                                boolean foundIt = false;
                                IFile changedFile = (IFile)resource;
                                
                                for (VdbEntry entry : getVdb().getModelEntries()) {
                                    if (entry.getName().equals(changedFile.getFullPath())) {
                                        entry.setSynchronization(Synchronization.NotSynchronized);
                                        foundIt = true;
                                        break;
                                    }
                                }

                                if (!foundIt) {
                                    for (VdbEntry entry : getVdb().getEntries()) {
                                        if (entry.getName().equals(changedFile.getFullPath())) {
                                            entry.setSynchronization(Synchronization.NotSynchronized);
                                            // no need to set foundIt to true as it is not needed later
                                            break;
                                        }
                                    }
                                }
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
