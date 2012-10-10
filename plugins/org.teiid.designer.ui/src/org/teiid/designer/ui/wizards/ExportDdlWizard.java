/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.wizards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.util.FileUtils;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceFilter;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceSelectionFilter;
import org.teiid.designer.core.workspace.ModelWorkspaceSelections;
import org.teiid.designer.core.workspace.ModelWorkspaceView;
import org.teiid.designer.core.xslt.Style;
import org.teiid.designer.core.xslt.StyleRegistry;
import org.teiid.designer.ddl.DdlOptions;
import org.teiid.designer.ddl.DdlPlugin;
import org.teiid.designer.ddl.DdlWriter;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.RelationalEntity;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
public final class ExportDdlWizard extends AbstractWizard
    implements FileUtils.Constants, IExportWizard, InternalUiConstants.Widgets, PluginConstants.Images, CoreStringUtil.Constants,
    UiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExportDdlWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$
    private static final String FILE_DIALOG_TITLE = getString("fileDialogTitle"); //$NON-NLS-1$

    private static final ImageDescriptor IMAGE = UiPlugin.getDefault().getImageDescriptor(EXPORT_DDL_ICON);

    private static final String METADATA_GROUP = getString("metadataGroup"); //$NON-NLS-1$
    private static final String OPTIONS_GROUP = getString("optionsGroup"); //$NON-NLS-1$
    private static final String COMMENT_OPTIONS_GROUP = getString("commentOptionsGroup"); //$NON-NLS-1$
    private static final String TYPE_LABEL = getString("typeLabel"); //$NON-NLS-1$
    private static final String SCHEMA_CHECKBOX = getString("schemaCheckBox"); //$NON-NLS-1$
    private static final String INFO_COMMENTS_CHECKBOX = getString("infoCommentsCheckBox"); //$NON-NLS-1$
    private static final String TABLE_COMMENTS_CHECKBOX = getString("tableCommentsCheckBox"); //$NON-NLS-1$
    private static final String COLUMN_COMMENTS_CHECKBOX = getString("columnCommentsCheckBox"); //$NON-NLS-1$
    private static final String DROP_STATEMENTS_CHECKBOX = getString("dropStatementsCheckBox"); //$NON-NLS-1$
    private static final String USE_NAMES_IN_SOURCE_CHECKBOX = getString("useNamesInSourceCheckBox"); //$NON-NLS-1$
    private static final String USE_NATIVE_TYPE_CHECKBOX = getString("useNativeTypeCheckBox"); //$NON-NLS-1$
    private static final String ENFORCE_UNIQUE_NAMES_CHECKBOX = getString("enforceUnqiueNamesCheckBox"); //$NON-NLS-1$
    private static final String FILE_GROUP = getString("fileGroup"); //$NON-NLS-1$
    private static final String FILE_LABEL = getString("fileLabel"); //$NON-NLS-1$
    private static final String FILE_BUTTON = BROWSE_BUTTON;

    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
    private static final String INVALID_SELECTION_INITIAL_MESSAGE = getString("initialMessageInvalidSelection"); //$NON-NLS-1$
    private static final String NO_SELECTIONS_MESSAGE = getString("noSelectionsMessage"); //$NON-NLS-1$
    private static final String NO_TYPE_MESSAGE = getString("noTypeMessage"); //$NON-NLS-1$
    private static final String NO_FILE_MESSAGE = getString("noFileMessage"); //$NON-NLS-1$
    private static final String INVALID_FILE_MESSAGE = getString("invalidFileMessage"); //$NON-NLS-1$
    private static final String EXPORT_ERROR_MESSAGE = getString("exportErrorMessage"); //$NON-NLS-1$

    private static final String DDL_EXTENSION = FILE_EXTENSION_SEPARATOR + "ddl"; //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    DdlWriter writer;
    ModelWorkspaceSelections selections;
    private ModelWorkspaceSelectionFilter selectionFilter;
    private File file;
    IStatus status;
    private IStructuredSelection selection;

    private WizardPage pg;
    private TreeViewer viewer;
    private Button schemaCheckBox, infoCommentsCheckBox, tableCommentsCheckBox, columnCommentsCheckBox, dropStatementsCheckBox;
    private Button useNamesInSourceCheckBox, useNativeTypeCheckBox, enforceUniqueNamesCheckBox;
    private Combo typeCombo, fileCombo;
    
    //private String invalidSelectionMessage;
    private boolean invalidSelection;

    /**
     * @since 4.0
     */
    public ExportDdlWizard() {
        super(UiPlugin.getDefault(), TITLE, IMAGE);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        if (this.file.exists() && !WidgetUtil.confirmOverwrite(this.file)) {
            return false;
        }
        try {
            final FileOutputStream stream = new FileOutputStream(this.file);
            new ProgressMonitorDialog(getShell()).run(false, true, new IRunnableWithProgress() {
                @Override
				public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                    try {
                        ExportDdlWizard.this.status = ExportDdlWizard.this.writer.write(ExportDdlWizard.this.selections,
                                                                                        stream,
                                                                                        monitor);
                    } catch (final Exception err) {
                        throw new InvocationTargetException(err);
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            Util.log(e);
                        }
                        monitor.done();
                    }
                }
            });
            if (!this.status.isOK()) {
                Util.log(this.status);
                WidgetUtil.showError(EXPORT_ERROR_MESSAGE);
            }
            // Save settings for next time wizard is run
            final IDialogSettings settings = getDialogSettings();
            settings.put(TYPE_LABEL, typeCombo.getText());
            settings.put(SCHEMA_CHECKBOX, this.schemaCheckBox.getSelection());
            settings.put(INFO_COMMENTS_CHECKBOX, this.infoCommentsCheckBox.getSelection());
            settings.put(TABLE_COMMENTS_CHECKBOX, this.tableCommentsCheckBox.getSelection());
            settings.put(COLUMN_COMMENTS_CHECKBOX, this.columnCommentsCheckBox.getSelection());
            settings.put(DROP_STATEMENTS_CHECKBOX, this.dropStatementsCheckBox.getSelection());
            settings.put(USE_NAMES_IN_SOURCE_CHECKBOX, this.useNamesInSourceCheckBox.getSelection());
            settings.put(USE_NATIVE_TYPE_CHECKBOX, this.useNativeTypeCheckBox.getSelection());
            settings.put(ENFORCE_UNIQUE_NAMES_CHECKBOX, this.enforceUniqueNamesCheckBox.getSelection());
            WidgetUtil.saveSettings(settings, FILE_LABEL, this.fileCombo);
            return true;
        } catch (Throwable err) {
            if (err instanceof InvocationTargetException) {
                err = ((InvocationTargetException)err).getTargetException();
            }
            Util.log(err);
            WidgetUtil.showError(EXPORT_ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 4.0
     */
    @Override
	public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
    	invalidSelection = false;
    	//invalidSelectionMessage = null;
    	// Check for selection to be a single Relational Model
    	if( !SelectionUtilities.isSingleSelection(selection) ) {
    		invalidSelection = true;
    		//invalidSelectionMessage = "Cannot Export DDL.\n\nMultiple resources are selected.\n\nOnly single relational metamodels can be exported as DDL format.";
    	} else if(!SelectionUtilities.isAllIResourceObjects(selection)) {
    		//invalidSelectionMessage = "Cannot Export DDL.\n\nSelected object is not a valid resource.\n\nOnly single relational metamodels can be exported as DDL format.";
    		invalidSelection = true;
    	} else {
    		Object obj = SelectionUtilities.getSelectedObject(selection);
    		if( obj instanceof IResource ) {
    			IResource iRes = (IResource)obj;
    			
    			if( !ModelIdentifier.isRelationalSourceModel(iRes) &&
    				!ModelIdentifier.isRelationalViewModel(iRes) ) {
    				//invalidSelectionMessage = "Cannot Export DDL.\n\nOnly single relational view or source metamodels can be exported as DDL format.";
    				invalidSelection = true;
    			}
    		}
    		
    	}
    	
        this.selection = selection;
        this.selections = new ModelWorkspaceSelections();
        this.writer = DdlPlugin.getInstance().createDdlWriter();
        this.pg = new AbstractWizardPage(ExportDdlWizard.class.getSimpleName(), PAGE_TITLE) {
            @Override
			public void createControl( final Composite parent ) {
            	setControl(createPageControl(parent));
            }
        };

        this.pg.setPageComplete(false);
        addPage(pg);
    }

    /**
     * @since 4.0
     */
    Composite createPageControl( final Composite parent ) {
        // Load all DDL exporter extensions
        final StyleRegistry registry = DdlPlugin.getStyleRegistry();
        final Collection styles = registry.getStyles();

        // Create page
        final Composite pg = new Composite(parent, SWT.NONE);
        pg.setLayout(new GridLayout());
        // Add widgets to page
        Group group = WidgetFactory.createGroup(pg, METADATA_GROUP, GridData.FILL_BOTH);
        // Add contents to view form

        this.viewer = WidgetFactory.createTreeViewer(group, SWT.CHECK | SWT.MULTI);
        final Tree tree = this.viewer.getTree();
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        final ModelWorkspaceView view = new ModelWorkspaceView();
        view.getModelWorkspaceFilters().add(new ModelWorkspaceFilter() {
            @Override
			public boolean select( final Object parent,
                                   final Object node ) {
                if (node instanceof ModelResource) {
                    try {
                        final MetamodelDescriptor mmdesc = ((ModelResource)node).getPrimaryMetamodelDescriptor();
                        if (mmdesc != null) {
                            final String uri = mmdesc.getNamespaceURI();
                            return RelationalPackage.eNS_URI.equals(uri);
                        }
                    } catch (final Throwable err) {
                        Util.log(err);
                    }
                    return false;
                } else if (node instanceof ModelWorkspaceItem) {
                    return true;
                }
                return (node instanceof RelationalEntity && !(node instanceof Procedure || node instanceof Index));
            }
        });
        this.selections.setModelWorkspaceView(view);
        this.selectionFilter = new ModelWorkspaceSelectionFilter() {
            @Override
			public boolean isSelectable( final Object node ) {
                if (node instanceof ModelWorkspaceItem) {
                    return true;
                }
                final EObject parent = ((RelationalEntity)node).eContainer();
                return !(parent instanceof Table || parent instanceof Procedure || parent instanceof Index);
            }
        };
        this.selections.getModelWorkspaceSelectionFilters().add(this.selectionFilter);
        final ITreeContentProvider treeContentProvider = new ITreeContentProvider() {
            @Override
			public void dispose() {
            }

            @Override
			public Object[] getChildren( final Object node ) {
                try {
                    return view.getChildren(node);
                } catch (final ModelWorkspaceException err) {
                    Util.log(err);
                    return EMPTY_STRING_ARRAY;
                }
            }

            @Override
			public Object[] getElements( final Object inputElement ) {
                return getChildren(inputElement);
            }

            @Override
			public Object getParent( final Object node ) {
                return view.getParent(node);
            }
            
            @Override
			public boolean hasChildren( final Object node ) {
                try {
                    return view.hasChildren(node);
                } catch (final ModelWorkspaceException err) {
                    Util.log(err);
                    return false;
                }
            }

            @Override
			public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput ) {
            }
        };
        this.viewer.setContentProvider(treeContentProvider);
        this.viewer.setLabelProvider(new LabelProvider() {
            final ModelExplorerLabelProvider workbenchProvider = new ModelExplorerLabelProvider();

            @Override
            public Image getImage( final Object node ) {
                if (node instanceof EObject) {
                    return ModelUtilities.getEMFLabelProvider().getImage(node);
                }
                return workbenchProvider.getImage(((ModelWorkspaceItem)node).getResource());
            }

            @Override
            public String getText( final Object node ) {
                if (node instanceof EObject) {
                    return ModelUtilities.getEMFLabelProvider().getText(node);
                }
                return workbenchProvider.getText(((ModelWorkspaceItem)node).getResource());
            }
        });
        // Add listener to expand/collapse node when double-clicked
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
			public void doubleClick( final DoubleClickEvent event ) {
                nodeDoubleClicked(event);
            }
        });
        // Add listener to select node when check box selected
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown( final MouseEvent event ) {
                mouseClicked(event);
            }
        });
        // Add listener to select node when expanded/collapsed
        this.viewer.addTreeListener(new ITreeViewerListener() {
            @Override
			public void treeCollapsed( final TreeExpansionEvent event ) {
                nodeExpandedOrCollapsed(event);
            }

            @Override
			public void treeExpanded( final TreeExpansionEvent event ) {
                nodeExpanded(event);
            }
        });
        this.viewer.setInput(ModelerCore.getModelWorkspace());
        final LabelProvider comboLabelProvider = new LabelProvider() {
            @Override
            public String getText( final Object style ) {
                return ((Style)style).getName();
            }
        };
        // Initialize widgets w/ last selections made by user
        final DdlOptions options = this.writer.getOptions();
        final IDialogSettings settings = getDialogSettings();
        final String type = settings.get(TYPE_LABEL);
        if (type != null) {
            options.setStyle(registry.getStyle(settings.get(TYPE_LABEL)));
            options.setGenerateSchema(settings.getBoolean(SCHEMA_CHECKBOX));
            options.setGenerateInfoComments(settings.getBoolean(INFO_COMMENTS_CHECKBOX));
            options.setGenerateTableComments(settings.getBoolean(TABLE_COMMENTS_CHECKBOX));
            options.setGenerateColumnComments(settings.getBoolean(COLUMN_COMMENTS_CHECKBOX));
            options.setGenerateDropStatements(settings.getBoolean(DROP_STATEMENTS_CHECKBOX));
            options.setNameInSourceUsed(settings.getBoolean(USE_NAMES_IN_SOURCE_CHECKBOX));
            options.setNativeTypeUsed(settings.getBoolean(USE_NATIVE_TYPE_CHECKBOX));
            options.setUniqueNamesEnforced(settings.getBoolean(ENFORCE_UNIQUE_NAMES_CHECKBOX));
        }
        group = WidgetFactory.createGroup(pg, OPTIONS_GROUP, GridData.FILL_HORIZONTAL, 1, 2);
        {
            WidgetFactory.createLabel(group, TYPE_LABEL);
            final Style style = options.getStyle();
            this.typeCombo = WidgetFactory.createCombo(group,
                                                       SWT.READ_ONLY,
                                                       GridData.FILL_HORIZONTAL,
                                                       new ArrayList(styles),
                                                       style,
                                                       comboLabelProvider);
            this.typeCombo.addModifyListener(new ModifyListener() {
                @Override
				public void modifyText( final ModifyEvent event ) {
                    typeModified();
                }
            });
            if (style != null) {
                this.typeCombo.setToolTipText(style.getDescription());
            }
            this.schemaCheckBox = WidgetFactory.createCheckBox(group, SCHEMA_CHECKBOX, 0, 2, options.isGenerateSchema());
            this.schemaCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    schemasCheckBoxSelected();
                }
            });
            this.dropStatementsCheckBox = WidgetFactory.createCheckBox(group,
                                                                       DROP_STATEMENTS_CHECKBOX,
                                                                       0,
                                                                       2,
                                                                       options.isGenerateDropStatements());
            this.dropStatementsCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    dropStatementsCheckBoxSelected();
                }
            });
            this.useNamesInSourceCheckBox = WidgetFactory.createCheckBox(group,
                                                                         USE_NAMES_IN_SOURCE_CHECKBOX,
                                                                         0,
                                                                         2,
                                                                         options.isNameInSourceUsed());
            this.useNamesInSourceCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    useNamesInSourceCheckBoxSelected();
                }
            });
            this.useNativeTypeCheckBox = WidgetFactory.createCheckBox(group,
                                                                      USE_NATIVE_TYPE_CHECKBOX,
                                                                      0,
                                                                      2,
                                                                      options.isNativeTypeUsed());
            this.useNativeTypeCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    useNativeTypeCheckBoxSelected();
                }
            });
            this.enforceUniqueNamesCheckBox = WidgetFactory.createCheckBox(group,
                                                                           ENFORCE_UNIQUE_NAMES_CHECKBOX,
                                                                           0,
                                                                           2,
                                                                           options.isUniqueNamesEnforced());
            this.enforceUniqueNamesCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    enforceUniqueNamesCheckBoxSelected();
                }
            });
            Group commentsGroup = WidgetFactory.createGroup(group, COMMENT_OPTIONS_GROUP, GridData.FILL_BOTH, 2, 2);
            
            this.infoCommentsCheckBox = WidgetFactory.createCheckBox(commentsGroup, INFO_COMMENTS_CHECKBOX, 0, 2, options.isGenerateInfoComments());
            this.infoCommentsCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    infoCommentsCheckBoxSelected();
                }
            });
            this.tableCommentsCheckBox = WidgetFactory.createCheckBox(commentsGroup, TABLE_COMMENTS_CHECKBOX, 0, 2, options.isGenerateTableComments());
            this.tableCommentsCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                	tableCommentsCheckBoxSelected();
                }
            });
            this.columnCommentsCheckBox = WidgetFactory.createCheckBox(commentsGroup, COLUMN_COMMENTS_CHECKBOX, 0, 2, options.isGenerateColumnComments());
            this.columnCommentsCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    columnCommentsCheckBoxSelected();
                }
            });
            

        }
        group = WidgetFactory.createGroup(pg, FILE_GROUP, GridData.FILL_HORIZONTAL, 1, 3);
        {
            WidgetFactory.createLabel(group, FILE_LABEL);
            this.fileCombo = WidgetFactory.createCombo(group, SWT.NONE, GridData.FILL_HORIZONTAL, settings.getArray(FILE_LABEL));
            this.fileCombo.addModifyListener(new ModifyListener() {
                @Override
				public void modifyText( final ModifyEvent event ) {
                    fileModified();
                }
            });
            WidgetFactory.createButton(group, FILE_BUTTON).addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    fileButtonSelected();
                }
            });
        }
        // Initialize widgets
        if (this.selection != null && !invalidSelection) {
            final ArrayList objs = new ArrayList(this.selection.size());
            final Iterator iter = this.selection.iterator();
            for (int ndx = 0; iter.hasNext(); ++ndx) {
                final Object obj = iter.next();
                final IPath path = (obj instanceof IResource ? ((IResource)obj).getFullPath() : view.getPath(obj));
                try {
                    objs.add(view.findObject(path));
                } catch (final ModelWorkspaceException err) {
                    Util.log(err);
                    WidgetUtil.showError(err);
                }
            }
            if (!objs.isEmpty()) {
                this.viewer.setSelection(new StructuredSelection(objs), true);
                final TreeItem[] items = this.viewer.getTree().getSelection();
                for (int ndx = items.length; --ndx >= 0;) {
                    toggleSelection(items[ndx]);
                }
            }
            this.pg.setMessage(INITIAL_MESSAGE);
        } else {
        	this.pg.setMessage(INVALID_SELECTION_INITIAL_MESSAGE); //"Select view or source relational model in workspace");
        }
        
        return pg;
    }

    // ============================================================================================================================
    // MVC Controller Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void infoCommentsCheckBoxSelected() {
        this.writer.getOptions().setGenerateInfoComments(this.infoCommentsCheckBox.getSelection());
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void tableCommentsCheckBoxSelected() {
        this.writer.getOptions().setGenerateTableComments(this.tableCommentsCheckBox.getSelection());
    }
    
    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void columnCommentsCheckBoxSelected() {
        this.writer.getOptions().setGenerateColumnComments(this.columnCommentsCheckBox.getSelection());
    }
    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void dropStatementsCheckBoxSelected() {
        this.writer.getOptions().setGenerateDropStatements(this.dropStatementsCheckBox.getSelection());
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void useNamesInSourceCheckBoxSelected() {
        this.writer.getOptions().setNameInSourceUsed(this.useNamesInSourceCheckBox.getSelection());
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void useNativeTypeCheckBoxSelected() {
        this.writer.getOptions().setNativeTypeUsed(this.useNativeTypeCheckBox.getSelection());
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void enforceUniqueNamesCheckBoxSelected() {
        this.writer.getOptions().setUniqueNamesEnforced(this.enforceUniqueNamesCheckBox.getSelection());
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void fileButtonSelected() {
        // Display file dialog for user to choose libraries
        final FileDialog dlg = new FileDialog(getShell(), SWT.SAVE | SWT.SINGLE);
        dlg.setFilterExtensions(new String[] {"*.ddl", "*.sql", "*.*"}); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        dlg.setText(FILE_DIALOG_TITLE);
        final String file = dlg.open();
        if (file != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(file);
            if (file.indexOf('.') < 0) {
                buffer.append(DDL_EXTENSION);
            }
            this.fileCombo.setText(buffer.toString());
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void fileModified() {
        String file = this.fileCombo.getText();
        if( file != null && file.length() > 0 ) {
	        final char lastChr = file.charAt(file.length() - 1);
	        if (file.indexOf(FILE_EXTENSION_SEPARATOR) < 0 && lastChr != ':' && lastChr != '\\' && lastChr != '/') {
	            file += DDL_EXTENSION;
	        }
	        this.file = new File(file);
        }
        validatePage();
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void mouseClicked( final MouseEvent event ) {
        // Return if not left-click
        if (event.button != 1) {
            return;
        }
        final TreeItem item = this.viewer.getTree().getItem(new Point(event.x, event.y));
        if (item != null) {
            final Object node = item.getData();
            this.viewer.setSelection(new StructuredSelection(node));
            if (event.x < item.getBounds().x - item.getImage().getBounds().width - IMAGE_ICON_GAP
                && this.selectionFilter.isSelectable(node)) {
                toggleSelection(item);
            } else {
                updateCheckBox(item);
            }
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void nodeDoubleClicked( final DoubleClickEvent event ) {
        final Object node = ((IStructuredSelection)event.getSelection()).getFirstElement();
        this.viewer.setExpandedState(node, !this.viewer.getExpandedState(node));
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void nodeExpanded( final TreeExpansionEvent event ) {
        nodeExpandedOrCollapsed(event);
        updateCheckBoxes(this.viewer.getTree().getSelection()[0].getItems());
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void nodeExpandedOrCollapsed( final TreeExpansionEvent event ) {
        this.viewer.setSelection(new StructuredSelection(event.getElement()));
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void schemasCheckBoxSelected() {
        this.writer.getOptions().setGenerateSchema(this.schemaCheckBox.getSelection());
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    void typeModified() {
        final Style style = DdlPlugin.getStyleRegistry().getStyle(this.typeCombo.getText());
        this.writer.getOptions().setStyle(style);
        if (style != null) {
            this.typeCombo.setToolTipText(style.getDescription());
        }
        validatePage();
    }

    // ============================================================================================================================
    // Utility Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private void toggleSelection( final TreeItem item ) {
        final Object node = item.getData();
        final boolean select = (this.selections.getSelectionMode(node) != ModelWorkspaceSelections.SELECTED);
        // Select node in both view and model
        try {
            this.selections.setSelected(node, select);
        } catch (final ModelWorkspaceException err) {
            Util.log(err);
        }
        // Update check boxes of item, ancestors, and expanded (now or previously) children
        updateCheckBox(item);
        for (TreeItem parent = item.getParentItem(); parent != null; parent = parent.getParentItem()) {
            updateCheckBox(parent);
        }
        updateCheckBoxes(item.getItems());
        validatePage();
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private void updateCheckBox( final TreeItem item ) {
        final Object node = item.getData();
        if (node == null) {
            return;
        }
        final int mode = this.selections.getSelectionMode(node);
        item.setChecked(mode != ModelWorkspaceSelections.UNSELECTED);
        item.setGrayed(mode == ModelWorkspaceSelections.PARTIALLY_SELECTED);
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private void updateCheckBoxes( final TreeItem[] items ) {
        for (int ndx = items.length; --ndx >= 0;) {
            final TreeItem item = items[ndx];
            updateCheckBoxes(item.getItems());
            updateCheckBox(item);
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private void validatePage() {
        if (!this.selections.hasSelectionModes()) {
            WizardUtil.setPageComplete(this.pg, NO_SELECTIONS_MESSAGE, IMessageProvider.ERROR);
        } else if (this.writer.getOptions().getStyle() == null) {
            WizardUtil.setPageComplete(this.pg, NO_TYPE_MESSAGE, IMessageProvider.ERROR);
        } else if (this.file == null) {
            WizardUtil.setPageComplete(this.pg, NO_FILE_MESSAGE, IMessageProvider.ERROR);
        } else if (this.file.isDirectory()) {
            WizardUtil.setPageComplete(this.pg, INVALID_FILE_MESSAGE, IMessageProvider.ERROR);
        } else if (this.file.exists()) {
            WizardUtil.setPageComplete(this.pg, WidgetUtil.getFileExistsMessage(this.file), IMessageProvider.WARNING);
        } else {
            WizardUtil.setPageComplete(this.pg);
        }
    }
}
