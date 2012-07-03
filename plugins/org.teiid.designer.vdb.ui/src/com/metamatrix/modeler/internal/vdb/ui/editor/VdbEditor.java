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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbDataRole;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbEntry.Synchronization;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.connections.SourceHandlerExtensionManager;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants.Images;
import com.metamatrix.modeler.vdb.ui.VdbUiPlugin;
import com.metamatrix.modeler.vdb.ui.translators.TranslatorOverridesPanel;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.ButtonProvider;
import com.metamatrix.ui.internal.widget.DefaultContentProvider;
import com.metamatrix.ui.table.CheckBoxColumnProvider;
import com.metamatrix.ui.table.DefaultTableProvider;
import com.metamatrix.ui.table.ResourceEditingSupport;
import com.metamatrix.ui.table.TableAndToolBar;
import com.metamatrix.ui.table.TextColumnProvider;
import com.metamatrix.ui.text.StyledTextEditor;

/**
 * @since 4.0
 */
// TODO: read-only, undo/redo, function model 259
public final class VdbEditor extends EditorPart implements IResourceChangeListener {

    static final String MODEL_COLUMN_NAME = i18n("modelColumnName"); //$NON-NLS-1$
    static final String FILE_COLUMN_NAME = i18n("fileColumnName"); //$NON-NLS-1$
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

    static final String CONFIRM_DIRTY_MODELS_DIALOG_TITLE = i18n("confirmDirtyModelsDialogTitle"); //$NON-NLS-1$
    static final String CONFIRM_DIRTY_MODELS_DIALOG_MESSAGE= i18n("confirmDirtyModelsSynchronizeMessage"); //$NON-NLS-1$
    static final String CONFIRM_DIALOG_TITLE = i18n("confirmDialogTitle"); //$NON-NLS-1$
    static final String CONFIRM_SYNCHRONIZE_MESSAGE = i18n("confirmSynchronizeMessage"); //$NON-NLS-1$
    static final String CONFIRM_SYNCHRONIZE_ALL_MESSAGE = i18n("confirmSynchronizeAllMessage"); //$NON-NLS-1$
    static final String CONFIRM_REMOVE_MESSAGE = i18n("confirmRemoveMessage"); //$NON-NLS-1$
    static final String CONFIRM_REMOVE_IMPORTED_BY_MESSAGE = i18n("confirmRemoveImportedByMessage"); //$NON-NLS-1$
    static final String INFORM_DATA_ROLES_ON_ADD_MESSAGE = i18n("informDataRolesExistOnAddMessage"); //$NON-NLS-1$
    static final String INVALID_INTEGER_INPUT_TITLE = i18n("invalidQueryTimeoutValueTitle"); //$NON-NLS-1$
    static final String INVALID_INTEGER_INPUT_MESSAGE = i18n("invalidQueryTimeoutValueMessage"); //$NON-NLS-1$

    static final String WEB_SERVICES_VIEW_MODEL_URI = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$

    static String i18n( final String id ) {
        return VdbUiConstants.Util.getString(id);
    }

    Vdb vdb;
    StyledTextEditor textEditor;
    TableAndToolBar<VdbModelEntry> modelsGroup;
    TableAndToolBar<VdbEntry> otherFilesGroup;
    TableAndToolBar<VdbDataRole> dataRolesGroup;
    private Button synchronizeAllButton;
    private PropertyChangeListener vdbListener;

    Action cloneDataRoleAction;
    VdbDataRole selectedDataRole;
    VdbDataRoleResolver dataRoleResolver;
    TranslatorOverridesPanel pnlTranslatorOverrides;
    
    boolean disposed = false;

    private final TextColumnProvider<VdbEntry> descriptionColumnProvider = new TextColumnProvider<VdbEntry>() {
        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ColumnProvider#getName()
         */
        @Override
        public String getName() {
            return DESCRIPTION_COLUMN_NAME;
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ColumnProvider#getImage()
         */
        @Override
        public Image getImage() {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ColumnProvider#getValue(java.lang.Object)
         */
        @Override
        public String getValue( final VdbEntry element ) {
            return element.getDescription();
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.DefaultColumnProvider#isEditable(java.lang.Object)
         */
        @Override
        public boolean isEditable( final VdbEntry element ) {
            return true;
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.DefaultColumnProvider#setValue(java.lang.Object, java.lang.Object)
         */
        @Override
        public void setValue( final VdbEntry element,
                              final String value ) {
            element.setDescription(value);
        }
    };

    private final ModelLabelProvider modelLabelProvider = new ModelLabelProvider();

    private final TextColumnProvider<VdbEntry> pathColumnProvider = new TextColumnProvider<VdbEntry>() {
        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ColumnProvider#getName()
         */
        @Override
        public String getName() {
            return PATH_COLUMN_NAME;
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ColumnProvider#getImage()
         */
        @Override
        public Image getImage() {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ColumnProvider#getValue(java.lang.Object)
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
         * @see com.metamatrix.ui.table.ColumnProvider#getName()
         */
        @Override
        public String getName() {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ColumnProvider#getImage()
         */
        @Override
        public Image getImage() {
            return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.SYNCHRONIZE_MODELS_ICON);
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.DefaultColumnProvider#getToolTip(java.lang.Object)
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
         * @see com.metamatrix.ui.table.ColumnProvider#getValue(java.lang.Object)
         */
        @Override
        public Boolean getValue( final VdbEntry element ) {
            return element.getSynchronization() == Synchronization.Synchronized;
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.DefaultColumnProvider#isEditable(java.lang.Object)
         */
        @Override
        public boolean isEditable( final VdbEntry element ) {
            return element.getSynchronization() == Synchronization.NotSynchronized;
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.DefaultColumnProvider#setValue(java.lang.Object, java.lang.Object)
         */
        @Override
        public void setValue( final VdbEntry element,
                              final Boolean value ) {
            IPreferenceStore prefStore = VdbUiPlugin.singleton.getPreferenceStore();
            // set value to true if preferene not found
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
                BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
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
    }
    
    private void packModelsGroup() {
        modelsGroup.getTable().getColumn(0).getColumn().pack();
        modelsGroup.getTable().getColumn(1).getColumn().pack();
        modelsGroup.getTable().getColumn(4).getColumn().pack();
        modelsGroup.getTable().getColumn(5).getColumn().pack();
        modelsGroup.getTable().getColumn(6).getColumn().pack();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void createDataRolesControl( Composite parent ) {
        final String DATA_POLICY_COLUMN_NAME = i18n("dataPolicyName"); //$NON-NLS-1$

        final ButtonProvider editProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.EDIT_ROLE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("editRoleToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return (selection.size() == 1);
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
                VdbDataRole vdbDataRole = (VdbDataRole)selection.getFirstElement();
                if (vdbDataRole == null) {
                    return;
                }
                ContainerImpl tempContainer = null;
                try {
                    Collection<File> modelFiles = getVdb().getModelFiles();

                    tempContainer = (ContainerImpl)ModelerCore.createContainer("tempVdbModelContainer"); //$NON-NLS-1$
                    ModelEditorImpl.setContainer(tempContainer);
                    for (File modelFile : modelFiles) {
                        boolean isVisible = true;

                        Resource r = tempContainer.getResource(URI.createFileURI(modelFile.getPath()), true);
                        if (isVisible && ModelUtil.isModelFile(r) && !ModelUtil.isXsdFile(r)) {
                            EObject firstEObj = r.getContents().get(0);
                            ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(firstEObj);
                            String mmURI = ma.getPrimaryMetamodelUri();
                            if (RelationalPackage.eNS_URI.equalsIgnoreCase(mmURI)
                                || XmlDocumentPackage.eNS_URI.equalsIgnoreCase(mmURI)
                                || WEB_SERVICES_VIEW_MODEL_URI.equalsIgnoreCase(mmURI)
                                || ModelIdentifier.FUNCTION_MODEL_URI.equals(mmURI)) {
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

                DataRole dataPolicy = new DataRole(vdbDataRole.getName(), vdbDataRole.getDescription(),
                                                   vdbDataRole.isAnyAuthenticated(), vdbDataRole.allowCreateTempTables(),
                                                   vdbDataRole.getMappedRoleNames(), vdbDataRole.getPermissions());

                final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();
                final NewDataRoleWizard wizard = new NewDataRoleWizard(tempContainer, dataPolicy);

                wizard.init(iww.getWorkbench(), new StructuredSelection(getVdb().getModelEntries()));
                final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
                final int rc = dialog.open();
                if (rc == Window.OK) {
                    // Get the Data Policy
                    DataRole dp = wizard.getDataRole();
                    if (dp != null) {
                        getVdb().removeDataPolicy(vdbDataRole);
                        getVdb().addDataPolicy(dp, new NullProgressMonitor());
                    }

                }
            }
        };

        dataRolesGroup = new TableAndToolBar(parent, 1, new DefaultTableProvider<VdbDataRole>() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.DefaultTableProvider#doubleClicked(java.lang.Object)
             */
            @Override
            public void doubleClicked( VdbDataRole element ) {
                editProvider.selected(new StructuredSelection(element));
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.TableProvider#getElements()
             */
            @Override
            public VdbDataRole[] getElements() {
                final Set<VdbDataRole> entries = getVdb().getDataPolicyEntries();
                return entries.toArray(new VdbDataRole[entries.size()]);
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.DefaultTableProvider#isDoubleClickSupported()
             */
            @Override
            public boolean isDoubleClickSupported() {
                return true;
            }
        }, new TextColumnProvider<VdbDataRole>() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.DefaultColumnProvider#getImage(java.lang.Object)
             */
            @Override
            public Image getImage( final VdbDataRole element ) {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.ColumnProvider#getName()
             */
            @Override
            public String getName() {
                return DATA_POLICY_COLUMN_NAME;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.ColumnProvider#getImage()
             */
            @Override
            public Image getImage() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.ColumnProvider#getValue(java.lang.Object)
             */
            @Override
            public String getValue( final VdbDataRole element ) {
                return element.getName();
            }
        }, new TextColumnProvider<VdbDataRole>() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.ColumnProvider#getName()
             */
            @Override
            public String getName() {
                return DESCRIPTION_COLUMN_NAME;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.ColumnProvider#getImage()
             */
            @Override
            public Image getImage() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.ColumnProvider#getValue(java.lang.Object)
             */
            @Override
            public String getValue( final VdbDataRole element ) {
                return element.getDescription();
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.DefaultColumnProvider#isEditable(java.lang.Object)
             */
            @Override
            public boolean isEditable( final VdbDataRole element ) {
                return true;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.table.DefaultColumnProvider#setValue(java.lang.Object, java.lang.Object)
             */
            @Override
            public void setValue( final VdbDataRole element,
                                  final String value ) {
                element.setDescription(value);
            }
        });

        ButtonProvider newProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.ADD_ROLE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("addRoleToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return true;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
                ContainerImpl tempContainer = null;
                try {
                    Collection<File> modelFiles = getVdb().getModelFiles();

                    tempContainer = (ContainerImpl)ModelerCore.createContainer("tempVdbModelContainer"); //$NON-NLS-1$
                    ModelEditorImpl.setContainer(tempContainer);
                    for (File modelFile : modelFiles) {
                        boolean isVisible = true;

                        Resource r = tempContainer.getResource(URI.createFileURI(modelFile.getPath()), true);
                        if (isVisible && ModelUtil.isModelFile(r) && !ModelUtil.isXsdFile(r)) {
                            EObject firstEObj = r.getContents().get(0);
                            ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(firstEObj);
                            String mmURI = ma.getPrimaryMetamodelUri();
                            if (RelationalPackage.eNS_URI.equalsIgnoreCase(mmURI)
                                || XmlDocumentPackage.eNS_URI.equalsIgnoreCase(mmURI)
                                || WEB_SERVICES_VIEW_MODEL_URI.equalsIgnoreCase(mmURI)
                                || ModelIdentifier.FUNCTION_MODEL_URI.equals(mmURI)) {
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

                wizard.init(iww.getWorkbench(), new StructuredSelection(getVdb().getModelEntries()));
                final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
                final int rc = dialog.open();
                if (rc == Window.OK) {
                    // Get the Data Policy
                    DataRole dp = wizard.getDataRole();
                    if (dp != null) {
                        getVdb().addDataPolicy(dp, new NullProgressMonitor());
                    }

                }
            }
        };

        dataRolesGroup.add(newProvider);
        dataRolesGroup.add(editProvider);

        ButtonProvider removeProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.REMOVE_ROLE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("removeRoleToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return !selection.isEmpty();
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public void selected( IStructuredSelection selection ) {
                if (ConfirmationDialog.confirm(CONFIRM_REMOVE_MESSAGE)) {
                    for (final Object element : selection.toList()) {
                        if (element instanceof VdbDataRole) {
                            getVdb().removeDataPolicy((VdbDataRole)element);
                        }

                    }
                }
            }
        };

        dataRolesGroup.add(removeProvider);
        dataRolesGroup.setInput(vdb);

        this.cloneDataRoleAction = new Action(i18n("cloneDataRoleActionLabel")) { //$NON-NLS-1$
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {

                if (selectedDataRole != null) {
                    DataRole newDR = new DataRole(
                                                  selectedDataRole.getName() + i18n("cloneDataRoleAction.copySuffix"), //$NON-NLS-1$
                                                  selectedDataRole.getDescription(), selectedDataRole.isAnyAuthenticated(),
                                                  selectedDataRole.allowCreateTempTables(),
                                                  selectedDataRole.getMappedRoleNames(), selectedDataRole.getPermissions());
                    getVdb().addDataPolicy(newDR, new NullProgressMonitor());
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
        dataRolesViewer.getControl().setMenu(dataRolesMenuManager.createContextMenu(parent));
        dataRolesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
                dataRolesMenuManager.removeAll();
                IStructuredSelection sel = (IStructuredSelection)dataRolesViewer.getSelection();
                if (sel.size() == 1) {
                    selectedDataRole = (VdbDataRole)sel.getFirstElement();
                    dataRolesMenuManager.add(cloneDataRoleAction);
                }

            }
        });
    }

    private void createDescriptionControl( Composite parent ) {
    	Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1, 2);
    	panel.setLayout(new GridLayout(2, false));

		Group propertiesGroup = WidgetFactory.createGroup(panel, i18n("properties"), SWT.FILL, 1, 2); //$NON-NLS-1$
		GridData gd_1 = new GridData(GridData.FILL_VERTICAL);
		gd_1.widthHint = 220;
		propertiesGroup.setLayoutData(gd_1);

		Label label = new Label(propertiesGroup, SWT.NONE);
		label.setText(i18n("queryTimeoutLabel")); //$NON-NLS-1$

		final Text queryTimeoutText = new Text(propertiesGroup, SWT.BORDER | SWT.SINGLE);
		queryTimeoutText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		queryTimeoutText.setText(Integer.toString(vdb.getQueryTimeout()));
    	queryTimeoutText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				try {
                    int valueInSecs = Integer.parseInt(queryTimeoutText.getText());
                    if (valueInSecs > -1) {
                        getVdb().setQueryTimeout(valueInSecs);
					}
				} catch (NumberFormatException ex) {
					MessageDialog.openWarning(Display.getCurrent().getActiveShell(),
                            VdbEditor.INVALID_INTEGER_INPUT_TITLE,
                            INVALID_INTEGER_INPUT_MESSAGE);
					queryTimeoutText.setText(Integer.toString(vdb.getQueryTimeout()));
				}
				
			}
		});
    	
    	Group descriptionGroup = WidgetFactory.createGroup(panel, i18n("description"), GridData.FILL_BOTH, 1, 1); //$NON-NLS-1$
    	
        this.textEditor = new StyledTextEditor(descriptionGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 1;

        this.textEditor.setLayoutData(gridData);
        this.textEditor.setText(vdb.getDescription());
        this.textEditor.getDocument().addDocumentListener(new IDocumentListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
             */
            @Override
            public void documentAboutToBeChanged( final DocumentEvent event ) {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
             */
            @Override
            public void documentChanged( final DocumentEvent event ) {
                getVdb().setDescription(textEditor.getText());
            }

        });
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
            createDataRolesControl(pnlRoles);
        }

        { // description tab
            CTabItem descriptionTab = new CTabItem(tabFolder, SWT.NONE);
            descriptionTab.setText(i18n("descriptionTab")); //$NON-NLS-1$
            descriptionTab.setToolTipText(i18n("descriptionTabToolTip")); //$NON-NLS-1$
            Composite pnlDescription = new Composite(tabFolder, SWT.NONE);
            pnlDescription.setLayout(new GridLayout());
            pnlDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            descriptionTab.setControl(pnlDescription);
            createDescriptionControl(pnlDescription);
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void createOtherFilesControl( Composite parent ) {
        final WorkbenchLabelProvider workbenchLabelProvider = new WorkbenchLabelProvider();
        otherFilesGroup = new TableAndToolBar(parent, 1, new DefaultTableProvider<VdbEntry>() {
                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see com.metamatrix.ui.table.DefaultTableProvider#doubleClicked(java.lang.Object)
                                                   */
                                                  @Override
                                                  public void doubleClicked( VdbEntry element ) {
                                                      openEditor(element);
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see com.metamatrix.ui.table.TableProvider#getElements()
                                                   */
                                                  @Override
                                                  public VdbEntry[] getElements() {
                                                      final Set<VdbEntry> entries = getVdb().getEntries();
                                                      return entries.toArray(new VdbEntry[entries.size()]);
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see com.metamatrix.ui.table.DefaultTableProvider#isDoubleClickSupported()
                                                   */
                                                  @Override
                                                  public boolean isDoubleClickSupported() {
                                                      return true;
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
                                                   * @see com.metamatrix.ui.table.DefaultColumnProvider#getImage(java.lang.Object)
                                                   */
                                                  @Override
                                                  public Image getImage( final VdbEntry element ) {
                                                      return workbenchLabelProvider.getImage(element.findFileInWorkspace());
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see com.metamatrix.ui.table.ColumnProvider#getImage()
                                                   */
                                                  @Override
                                                  public Image getImage() {
                                                      return null;
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see com.metamatrix.ui.table.ColumnProvider#getName()
                                                   */
                                                  @Override
                                                  public String getName() {
                                                      return FILE_COLUMN_NAME;
                                                  }

                                                  /**
                                                   * {@inheritDoc}
                                                   * 
                                                   * @see com.metamatrix.ui.table.ColumnProvider#getValue(java.lang.Object)
                                                   */
                                                  @Override
                                                  public String getValue( final VdbEntry element ) {
                                                      return element.getName().lastSegment();
                                                  }
                                              }, this.pathColumnProvider, this.syncColumnProvider, this.descriptionColumnProvider);

        ButtonProvider addProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.ADD_FILE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("addFileToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return true;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
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
                        if (element instanceof IContainer) return true;
                        final IFile file = (IFile)element;
                        if (ModelUtilities.isModelFile(file) || ModelUtil.isXsdFile(file) || ModelUtil.isVdbArchiveFile(file)) return false;
                        for (final VdbEntry entry : getVdb().getEntries())
                            if (file.equals(entry.findFileInWorkspace())) return false;
                        return true;
                    }
                };
                final Object[] files = WidgetUtil.showWorkspaceObjectSelectionDialog(ADD_FILE_DIALOG_TITLE,
                                                                                     ADD_FILE_DIALOG_MESSAGE,
                                                                                     true,
                                                                                     null,
                                                                                     new ModelingResourceFilter(filter),
                                                                                     getValidator(),
                                                                                     getModelLabelProvider());

                // indicates if this is the first time models are being added
                boolean firstTime = (otherFilesGroup.getTable().getViewer().getTable().getItemCount() == 0);

                for (final Object file : files)
                    getVdb().addEntry(((IFile)file).getFullPath(), new NullProgressMonitor());

                // refresh table from model
                otherFilesGroup.getTable().getViewer().refresh();

                // pack columns if first time a file is added
                if (firstTime) {
                    WidgetUtil.pack(otherFilesGroup.getTable().getViewer());
                }
            }
        };
        otherFilesGroup.add(addProvider);

        ButtonProvider removeProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.REMOVE_FILE);
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("removeFileToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return !selection.isEmpty();
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
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
            }
        };

        otherFilesGroup.add(removeProvider);
        otherFilesGroup.setInput(vdb);
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

        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
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
            pnlModels.setLayout(new GridLayout());
            pnlModels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            modelsTab.setControl(pnlModels);
            createModelsSection(pnlModels);
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

        { // synchronize button
            synchronizeAllButton = WidgetFactory.createButton(pnlTop, i18n("synchronizeAllButton"), //$NON-NLS-1$
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
                        BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
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
                            }
                        });
                    }
                }
            });
            synchronizeAllButton.setEnabled(!vdb.isSynchronized());
        }

        tabFolder.setSelection(0);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void createModelsSection( Composite parent ) {
        modelsGroup = new TableAndToolBar(parent, 1,
                                          new DefaultTableProvider<VdbModelEntry>() {
                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultTableProvider#doubleClicked(java.lang.Object)
                                               */
                                              @Override
                                              public void doubleClicked( final VdbModelEntry element ) {
                                                  openEditor(element);
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.TableProvider#getElements()
                                               */
                                              @Override
                                              public VdbModelEntry[] getElements() {
                                                  final Set<VdbModelEntry> modelEntries = getVdb().getModelEntries();
                                                  return modelEntries.toArray(new VdbModelEntry[modelEntries.size()]);
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultTableProvider#isDoubleClickSupported()
                                               */
                                              @Override
                                              public boolean isDoubleClickSupported() {
                                                  return true;
                                              }
                                          }, new TextColumnProvider<VdbModelEntry>() {
                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultColumnProvider#getImage(java.lang.Object)
                                               */
                                              @Override
                                              public Image getImage( final VdbModelEntry element ) {
                                                  return ModelIdentifier.getModelImage(element.findFileInWorkspace());
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getName()
                                               */
                                              @Override
                                              public String getName() {
                                                  return MODEL_COLUMN_NAME;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getImage()
                                               */
                                              @Override
                                              public Image getImage() {
                                                  return null;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getValue(java.lang.Object)
                                               */
                                              @Override
                                              public String getValue( final VdbModelEntry element ) {
                                                  return element.getName().lastSegment();
                                              }
                                          }, this.pathColumnProvider, this.syncColumnProvider,
                                          new CheckBoxColumnProvider<VdbModelEntry>() {
                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getName()
                                               */
                                              @Override
                                              public String getName() {
                                                  return null;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getImage()
                                               */
                                              @Override
                                              public Image getImage() {
                                                  return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.VISIBLE_ICON);
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultColumnProvider#getToolTip(java.lang.Object)
                                               */
                                              @Override
                                              public String getToolTip( final VdbModelEntry element ) {
                                                  return element.isVisible() ? VISIBLE_TOOLTIP : NOT_VISIBLE_TOOLTIP;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getValue(java.lang.Object)
                                               */
                                              @Override
                                              public Boolean getValue( final VdbModelEntry element ) {
                                                  return element.isVisible();
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultColumnProvider#isEditable(java.lang.Object)
                                               */
                                              @Override
                                              public boolean isEditable( final VdbModelEntry element ) {
                                                  return true;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultColumnProvider#setValue(java.lang.Object,
                                               *      java.lang.Object)
                                               */
                                              @Override
                                              public void setValue( final VdbModelEntry element,
                                                                    final Boolean value ) {
                                                  element.setVisible(value);
                                              }
                                          }, new TextColumnProvider<VdbModelEntry>() {
                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getName()
                                               */
                                              @Override
                                              public String getName() {
                                                  return SOURCE_NAME_COLUMN_NAME;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getImage()
                                               */
                                              @Override
                                              public Image getImage() {
                                                  return null;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getValue(java.lang.Object)
                                               */
                                              @Override
                                              public String getValue( final VdbModelEntry element ) {
                                                  final String value = element.getSourceName();
                                                  return value == null ? EMPTY_STRING : value;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultColumnProvider#isEditable(java.lang.Object)
                                               */
                                              @Override
                                              public boolean isEditable( final VdbModelEntry element ) {
                                                  return true;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultColumnProvider#setValue(java.lang.Object,
                                               *      java.lang.Object)
                                               */
                                              @Override
                                              public void setValue( final VdbModelEntry element,
                                                                    final String value ) {
                                                  element.setSourceName(value);
                                              }
                                          }, new TextColumnProvider<VdbModelEntry>() {
                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getName()
                                               */
                                              @Override
                                              public String getName() {
                                                  return TRANSLATOR_COLUMN_NAME;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getImage()
                                               */
                                              @Override
                                              public Image getImage() {
                                                  return null;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getValue(java.lang.Object)
                                               */
                                              @Override
                                              public String getValue( final VdbModelEntry element ) {
                                                  final String value = element.getTranslator();
                                                  return value == null ? EMPTY_STRING : value;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultColumnProvider#isEditable(java.lang.Object)
                                               */
                                              @Override
                                              public boolean isEditable( final VdbModelEntry element ) {
                                                  return true;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultColumnProvider#setValue(java.lang.Object,
                                               *      java.lang.Object)
                                               */
                                              @Override
                                              public void setValue( final VdbModelEntry element,
                                                                    final String value ) {
                                                  element.setTranslator(value);
                                              }
                                          }, new TextColumnProvider<VdbModelEntry>() {
                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getName()
                                               */
                                              @Override
                                              public String getName() {
                                                  return JNDI_NAME_COLUMN_NAME;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getImage()
                                               */
                                              @Override
                                              public Image getImage() {
                                                  return null;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.ColumnProvider#getValue(java.lang.Object)
                                               */
                                              @Override
                                              public String getValue( final VdbModelEntry element ) {
                                                  final String value = element.getJndiName();
                                                  return value == null ? EMPTY_STRING : value;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultColumnProvider#isEditable(java.lang.Object)
                                               */
                                              @Override
                                              public boolean isEditable( final VdbModelEntry element ) {
                                                  return true;
                                              }

                                              /**
                                               * {@inheritDoc}
                                               * 
                                               * @see com.metamatrix.ui.table.DefaultColumnProvider#setValue(java.lang.Object,
                                               *      java.lang.Object)
                                               */
                                              @Override
                                              public void setValue( final VdbModelEntry element,
                                                                    final String value ) {
                                                  element.setJndiName(value);
                                              }
                                          }, this.descriptionColumnProvider);

        ButtonProvider addProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.ADD_MODEL);
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("addModelToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return true;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
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
                        if (element instanceof IContainer) return true;
                        final IFile file = (IFile)element;
                        if (!ModelUtilities.isModelFile(file) && !ModelUtil.isXsdFile(file)) return false;
                        for (final VdbModelEntry modelEntry : getVdb().getModelEntries())
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
                                                                                      getValidator(),
                                                                                      getModelLabelProvider());
                if (!getVdb().getDataPolicyEntries().isEmpty()) {
                    MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
                                                  VdbEditor.CONFIRM_DIALOG_TITLE,
                                                  INFORM_DATA_ROLES_ON_ADD_MESSAGE);
                }

                // indicates if this is the first time models are being added
                boolean firstTime = (modelsGroup.getTable().getViewer().getTable().getItemCount() == 0);

                // add the models
                for (final Object model : models)
                    getVdb().addModelEntry(((IFile)model).getFullPath(), new NullProgressMonitor());

                // refresh table from model
                modelsGroup.getTable().getViewer().refresh();
                pnlTranslatorOverrides.refresh();

                // pack columns if first time a model is added
                if (firstTime) {
                    WidgetUtil.pack(modelsGroup.getTable().getViewer());
                }
            }
        };

        modelsGroup.add(addProvider);

        ButtonProvider removeProvider = new ButtonProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getImageDescriptor()
             */
            @Override
            public ImageDescriptor getImageDescriptor() {
                return VdbUiPlugin.singleton.getImageDescriptor(Images.REMOVE_MODEL);
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getText()
             */
            @Override
            public String getText() {
                return null;
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#getToolTip()
             */
            @Override
            public String getToolTip() {
                return i18n("removeModelToolTip"); //$NON-NLS-1$
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
             */
            @Override
            public boolean isEnabled( IStructuredSelection selection ) {
                return !selection.isEmpty();
            }

            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.ui.internal.widget.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
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
            }
        };

        modelsGroup.add(removeProvider);

        // Add selection changed listener so if a Physical Source model is selected, the applicable menu actions are
        // retrieved via the SourceHandler extension point and interface.
        // This allows changing Translator and JNDI names via existing deployed objects on Teiid Servers that are
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
            }
        });

        // set a custom cell editor on translator column
        EditingSupport editor = new TranslatorEditingSupport(modelsGroup.getViewer(), getVdb().getFile());
        modelsGroup.getTable().getColumn(5).setEditingSupport(editor);

        // set a custom cell editor on JNDI column
        editor = new JndiEditingSupport(modelsGroup.getViewer(), getVdb().getFile());
        modelsGroup.getTable().getColumn(6).setEditingSupport(editor);

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

    ModelLabelProvider getModelLabelProvider() {
        return this.modelLabelProvider;
    }

    ISelectionStatusValidator getValidator() {
        return this.validator;
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

    class TranslatorEditingSupport extends ResourceEditingSupport {

        /**
         * @param viewer
         * @param vdb
         */
        public TranslatorEditingSupport( ColumnViewer viewer,
                                         IResource vdb ) {
            super(viewer, vdb);
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ResourceEditingSupport#canAddNewValue(java.lang.Object)
         */
        @Override
        protected boolean canAddNewValue( Object element ) {
            return true;
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ResourceEditingSupport#getElementValue(java.lang.Object)
         */
        @Override
        protected String getElementValue( Object element ) {
            return ((VdbModelEntry)element).getTranslator();
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ResourceEditingSupport#refreshItems(java.lang.Object)
         */
        @Override
        protected String[] refreshItems( Object element ) {
            List<String> translators = new ArrayList<String>();
            // get the available translators from the server
            String[] serverTypes = SourceHandlerExtensionManager.getVdbConnectionFinder().getTranslatorTypes();

            if (serverTypes != null) {
                translators.addAll(Arrays.asList(serverTypes));
            }

            // add in the translator overrides from the VDB
            for (TranslatorOverride translator : getVdb().getTranslators()) {
                translators.add(translator.getName());
            }

            Collections.sort(translators);
            return translators.toArray(new String[translators.size()]);
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ResourceEditingSupport#setElementValue(java.lang.Object, java.lang.String)
         */
        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
            if (newValue == null) {
                newValue = ""; //$NON-NLS-1$
            }

            ((VdbModelEntry)element).setTranslator(newValue);
        }
    }

    class JndiEditingSupport extends ResourceEditingSupport {

        /**
         * @param viewer
         * @param vdb
         */
        public JndiEditingSupport( ColumnViewer viewer,
                                   IResource vdb ) {
            super(viewer, vdb);
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ResourceEditingSupport#canAddNewValue(java.lang.Object)
         */
        @Override
        protected boolean canAddNewValue( Object element ) {
            return true;
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ResourceEditingSupport#getElementValue(java.lang.Object)
         */
        @Override
        protected String getElementValue( Object element ) {
            return ((VdbModelEntry)element).getJndiName();
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ResourceEditingSupport#refreshItems(java.lang.Object)
         */
        @Override
        protected String[] refreshItems( Object element ) {
            return SourceHandlerExtensionManager.getVdbConnectionFinder().getDataSourceNames();
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.ui.table.ResourceEditingSupport#setElementValue(java.lang.Object, java.lang.String)
         */
        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
            if (newValue == null) {
                newValue = ""; //$NON-NLS-1$
            }

            ((VdbModelEntry)element).setJndiName(newValue);
        }
    }

}
