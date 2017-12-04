/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.editors.summary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EventObject;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.notification.util.NotificationUtilities;
import org.teiid.designer.core.transaction.SourcedNotification;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.diagram.DiagramEntity;
import org.teiid.designer.relational.ui.actions.CreateRelationalIndexAction;
import org.teiid.designer.relational.ui.actions.CreateRelationalProcedureAction;
import org.teiid.designer.relational.ui.actions.CreateRelationalTableAction;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.actions.CreateViewIndexAction;
import org.teiid.designer.transformation.ui.actions.CreateViewProcedureAction;
import org.teiid.designer.transformation.ui.actions.CreateViewTableAction;
import org.teiid.designer.ui.actions.ModelerActionService;
import org.teiid.designer.ui.actions.ModelerSpecialActionManager;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.actions.ModelActionConstants;
import org.teiid.designer.ui.common.actions.ModelActionConstants.Special;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.editors.ModelEditorPage;
import org.teiid.designer.ui.editors.ModelEditorPageOutline;
import org.teiid.designer.ui.editors.ModelEditorSelectionSynchronizer;
import org.teiid.designer.ui.event.ModelResourceEvent;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.outline.ModelOutlineContentProvider;
import org.teiid.designer.ui.util.ModelObjectTreeViewerNotificationHandler;
import org.teiid.designer.ui.views.EditDescriptionDialog;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class ModelEditorMainPage extends EditorPart 
	implements ModelEditorPage, IResourceChangeListener, INotifyChangedListener, ISelectionProvider {

	private static final String SPACE = StringConstants.SPACE;
	private static final String TAB = StringConstants.TAB;
	private static final String BLANK = StringConstants.EMPTY_STRING;
    private ModelerActionService actionService;

    /**
     * Identifier of this editor
     */
    public static final String EDITOR_ID = ModelEditorMainPage.class.getCanonicalName();
    
    /**
     * Model info
     */
    
    ModelResource modelResource;
    IResource iResource;
    boolean isRelational;
    boolean isVirtual;

    /**
     * Flag indicating editor's dirty status
     */
    private boolean dirty = false;

    private ScrolledForm form;
    FormToolkit toolkit;
    
    private Composite mainControl;
    
    private Composite contentsPanel;

    private Hyperlink editDescriptionHL;

    private SashForm hSplitter;
    private TreeViewer modelTreeViewer;
    private StyledTextEditor textViewerPanel;
    
    Button previewButton;
    
    Button generateDataServiceButton;
    
    IAction collapseAll;
    IAction expandAll;
    
	private TabItem propertiesTab;
    ModelObjectPropertiesPanel propertiesPanel;
    
    private ModelObjectActionService objectActionService;
    
    private INotifyChangedListener notificationHandler;
    
    ModelEditorHyperlinkManager hyperLinkActionManager;

    @Override
    public void init(IEditorSite site, IEditorInput input) {
        setSite(site);
        setInput(input);
        if (input instanceof IFileEditorInput) {
            IFileEditorInput ifei = (IFileEditorInput)input;
            IFile modelFile = ifei.getFile();
            this.iResource = modelFile;
            this.modelResource = ModelUtilities.getModelResource(modelFile);
            if( this.modelResource != null ) {
            	this.isRelational = ModelIdentifier.isRelationalSourceModel(modelResource) ||
            			ModelIdentifier.isRelationalViewModel(modelResource);
            	this.isVirtual = ModelIdentifier.isRelationalViewModel(modelResource);
            	setTitleImage(ModelIdentifier.getModelImage(modelResource));
            	
            }
        }
        actionService = (ModelerActionService)org.teiid.designer.ui.UiPlugin.getDefault().getActionService(getSite().getPage());
        objectActionService = new ModelObjectActionService(actionService, this);
        
        this.expandAll = new Action(Messages.expandAll) {
            @Override
            public void run() {
            	modelTreeViewer.expandAll();
            }
		};
		this.expandAll.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.EXPAND_ALL_ICON));
		
        this.collapseAll = new Action(Messages.collapseAll) {
            @Override
            public void run() {
            	modelTreeViewer.collapseAll();
            }
		};
		this.collapseAll.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.COLLAPSE_ALL_ICON));
    }

    protected void createNotifyChangedListener(TreeViewer treeViewer) {
    	notificationHandler = new ModelObjectTreeViewerNotificationHandler(treeViewer);
    	ModelUtilities.addNotifyChangedListener(notificationHandler);
    	ModelUtilities.addNotifyChangedListener(this);
    }
    
    @Override
    public void createPartControl(Composite parent) {
    	
    	mainControl = parent;
        String title =  Messages.modelEditor;
        this.setPartName(title);
        this.setContentDescription(title);

        toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createScrolledForm(parent);
        toolkit.decorateFormHeading(form.getForm());        

        GridLayoutFactory.fillDefaults().applyTo(form.getBody());

        hyperLinkActionManager = new ModelEditorHyperlinkManager(this);
        
        buildModelEditorPanel(false);
    }

    private void buildModelEditorPanel(boolean doLayout) {
        // insert sections
        contentsPanel = getToolkit().createComposite(form.getBody());
        GridLayoutFactory.fillDefaults().numColumns(1).spacing(0, 0).applyTo(contentsPanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(contentsPanel);
        
        createOverviewSection(contentsPanel);

        String desc = "MODEL >> " + SPACE + SPACE + 
        		modelResource.getItemName() + TAB + SPACE + SPACE + 
        		Messages.location + SPACE + 
        		modelResource.getPath().removeLastSegments(1).toString();
    	form.setText(desc); //Messages.modelEditor);
    	form.setImage(null);
    	
        if( doLayout ) {
        	contentsPanel.layout(true);
        }
    }

    /**
     * @param parent
     * @param toolkit
     */
    private void createOverviewSection(Composite parent) {
        
        Composite twoColumnPanel = getToolkit().createComposite(parent);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).spacing(5, 5).applyTo(twoColumnPanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(twoColumnPanel);

    	createVerticalToolbar(twoColumnPanel);
    	
        this.hSplitter = new SashForm(twoColumnPanel, SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(hSplitter);
        
    	createTreeViewerPanel(hSplitter);
    	
    	createTabbedPanel(hSplitter);

    	hSplitter.setWeights(new int[] {4, 5});
        
        resetDescription();
    }
    
    @SuppressWarnings("unused")
	private void createVerticalToolbar(Composite parent) {
        Composite vertToolbarContainer = getToolkit().createComposite(parent);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(1, 1).applyTo(vertToolbarContainer);
        GridDataFactory.fillDefaults().hint(40, -1).grab(false, true).applyTo(vertToolbarContainer);
        
        Composite vertToolbar = getToolkit().createComposite(vertToolbarContainer);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(2, 2).applyTo(vertToolbar);
        GridDataFactory.fillDefaults().hint(40, -1).grab(false, true).applyTo(vertToolbar);
        
        ADD_BUTTONS: {
        	// PREVIEW
        	previewButton = getToolkit().createButton(vertToolbar, BLANK, SWT.PUSH);
        	previewButton.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.PREVIEW_DATA_ICON));
        	GridDataFactory.fillDefaults().grab(true, false).applyTo(previewButton);
        	previewButton.setEnabled(false);
        	previewButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					SortableSelectionAction action = ModelerSpecialActionManager.getAction(Special.PREVIEW_DATA);
					if( action != null ) {
						ISelection selection = modelTreeViewer.getSelection();
						Object sel = SelectionUtilities.getSelectedEObject(selection);
						action.setSelection(new StructuredSelection(sel));
						action.run();
					}
					
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
        	previewButton.setToolTipText(org.teiid.designer.ui.common.actions.Messages.PREVIEW_DATA_TOOLTIP);
        	
    		Composite topSep = getToolkit().createCompositeSeparator(vertToolbar);
    		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
    		layoutData.horizontalSpan = 1;
    		layoutData.heightHint = 2;
    		topSep.setLayoutData(layoutData);
        	
        	if( isRelational) {
	        	Button addTableButton = getToolkit().createButton(vertToolbar, BLANK, SWT.PUSH);
	        	if( isVirtual ) {
	        		addTableButton.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.NEW_VIRTUAL_TABLE_ICON));
	        	} else {
	        		addTableButton.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.NEW_TABLE_ICON));
	        	}
	        	GridDataFactory.fillDefaults().grab(true, false).applyTo(addTableButton);
	        	addTableButton.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						if( isVirtual ) {
							CreateViewTableAction action = new CreateViewTableAction();
							action.run(modelResource);
						} else {
							CreateRelationalTableAction action = new CreateRelationalTableAction();
							action.run(modelResource);
						}
						
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
	        	if( isVirtual ) {
	        		addTableButton.setToolTipText(org.teiid.designer.ui.common.actions.Messages.NEW_VIEW_TOOLTIP);
	        	} else {
	        		addTableButton.setToolTipText(org.teiid.designer.ui.common.actions.Messages.NEW_TABLE_TOOLTIP);
	        	}
	
	        	Button addProcedureButton = getToolkit().createButton(vertToolbar, BLANK, SWT.PUSH);
	        	if( isVirtual ) {
	        		addProcedureButton.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.NEW_VIRTUAL_PROCEDURE_ICON));
	        	} else {
	        		addProcedureButton.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.NEW_PROCECDURE_ICON));
	        	}
	        	GridDataFactory.fillDefaults().grab(true, false).applyTo(addProcedureButton);
	        	addProcedureButton.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						if( isVirtual ) {
							CreateViewProcedureAction action = new CreateViewProcedureAction();
							action.run(modelResource);
						} else {
							CreateRelationalProcedureAction action = new CreateRelationalProcedureAction();
							action.run(modelResource);
						}
						
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});

	        	addProcedureButton.setToolTipText(org.teiid.designer.ui.common.actions.Messages.NEW_PROCEDURE_TOOLTIP);

	        	Button addIndexButton = getToolkit().createButton(vertToolbar, BLANK, SWT.PUSH);
	        	addIndexButton.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.NEW_INDEX_ICON));
	        	GridDataFactory.fillDefaults().grab(true, false).applyTo(addIndexButton);
	        	addIndexButton.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						if( isVirtual ) {
							CreateViewIndexAction action = new CreateViewIndexAction();
							action.run(modelResource);
						} else {
							CreateRelationalIndexAction action = new CreateRelationalIndexAction();
							action.run(modelResource);
						}
						
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
	        	addIndexButton.setToolTipText(org.teiid.designer.ui.common.actions.Messages.NEW_INDEX_TOOLTIP);
        	}
        	
    		Composite bottomSep = getToolkit().createCompositeSeparator(vertToolbar);
    		layoutData = new GridData(GridData.FILL_HORIZONTAL);
    		layoutData.horizontalSpan = 1;
    		layoutData.heightHint = 2;
    		bottomSep.setLayoutData(layoutData);
        	
        	// PREVIEW
        	generateDataServiceButton = getToolkit().createButton(vertToolbar, BLANK, SWT.PUSH);
        	generateDataServiceButton.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.GENERATE_DATA_SERVICE));
        	GridDataFactory.fillDefaults().grab(true, false).applyTo(previewButton);
        	generateDataServiceButton.setEnabled(false);
        	generateDataServiceButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					SortableSelectionAction action = ModelerSpecialActionManager.getAction(ModelActionConstants.Special.GENERATE_DATA_SERVICE);
					if( action != null ) {
						ISelection selection = modelTreeViewer.getSelection();
						Object sel = SelectionUtilities.getSelectedEObject(selection);
						action.setSelection(new StructuredSelection(sel));
						action.run();
					}
					
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
        	generateDataServiceButton.setToolTipText(org.teiid.designer.ui.common.actions.Messages.GENERATE_DATA_SERVICE_TOOLTIP);
        }
    }
    
    private void createTreeViewerPanel(Composite parent) {
        Composite mainContainer = getToolkit().createComposite(parent);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(1, 1).applyTo(mainContainer);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainContainer);
    	
    	FilteredTree fTree = createFilteredTree(mainContainer);

        modelTreeViewer = fTree.getViewer(); //new TreeViewer(modelTree);
        
        ITreeContentProvider contentProvider = new ModelOutlineContentProvider(getEditorInput());
        modelTreeViewer.setContentProvider(contentProvider);

        ILabelDecorator decorator = UiUtil.getWorkbench().getDecoratorManager().getLabelDecorator();
        ILabelProvider labelProvider = new DecoratingLabelProvider(new ModelExplorerLabelProvider(), decorator);
        modelTreeViewer.setLabelProvider(labelProvider);
        modelTreeViewer.setInput(getEditorInput());
        modelTreeViewer.expandToLevel(2);
        modelTreeViewer.reveal(contentProvider.getElements(getEditorInput())[0]);
        
        final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
			public void menuAboutToShow( IMenuManager theMenuMgr ) {
            	objectActionService.contributeToContextMenu(menuMgr, modelTreeViewer.getSelection());
            	
            	// Add Collapse ALL and Expand ALL actions
            	menuMgr.add(new Separator());
            	menuMgr.add(collapseAll);
            	menuMgr.add(expandAll);
            	
            }
        });
        modelTreeViewer.getTree().setMenu(menuMgr.createContextMenu(modelTreeViewer.getTree()));
        
        
        modelTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				objectActionService.selectionChanged(event);
				hyperLinkActionManager.selectionChanged(event);
				
				SortableSelectionAction action = ModelerSpecialActionManager.getAction(Special.PREVIEW_DATA);
				if( action != null ) {
					previewButton.setEnabled(action.isApplicable(event.getSelection()));
				}
				action = ModelerSpecialActionManager.getAction(ModelActionConstants.Special.GENERATE_DATA_SERVICE);
				if( action != null ) {
					generateDataServiceButton.setEnabled(action.isApplicable(event.getSelection()));
				}
				
				propertiesPanel.selectionChanged(event);
				resetDescription();
			}
		});
        
        modelTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
                ISelection selection = event.getSelection();
                if (SelectionUtilities.isSingleSelection(selection) && SelectionUtilities.getSelectedEObject(selection) != null) {
                    EObject eObj = SelectionUtilities.getSelectedEObject(selection);
                    ModelEditorManager.open(eObj, true, org.teiid.designer.ui.UiConstants.ObjectEditor.REFRESH_EDITOR_IF_OPEN);
                } else {
                    ModelEditorSelectionSynchronizer.handleDoubleClick(event);
                }
				
			}
		});

        createNotifyChangedListener(modelTreeViewer);

    }
    
	private FilteredTree createFilteredTree(Composite parent) {
		int style = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
		FilteredTree transfersTree = new FilteredTree(parent, style,
				new PatternFilter(), true) {
			@Override
			protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
				return new TreeViewer(parent, style);
			}
		};
		return transfersTree;
	}

	private void createTabbedPanel(Composite parent) {
        Composite mainContainer = getToolkit().createComposite(parent);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(1, 1).applyTo(mainContainer);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainContainer);
        
        TabFolder tabFolder = new TabFolder(mainContainer, SWT.TOP | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(tabFolder);

        createActionsTab(tabFolder);
        
		createPropertiesTab(tabFolder);
		
		createDescriptionTab(tabFolder);
	}
    
	private void createPropertiesTab(TabFolder folderParent) {
		Composite thePanel = createPropertiesPanel(folderParent);

	    this.propertiesTab = new TabItem(folderParent, SWT.NONE);
	    this.propertiesTab.setControl(thePanel);
	    this.propertiesTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.PROPERTIES));
	}
	
	private void createActionsTab(TabFolder folderParent) {
		Composite thePanel = createActionListPanel(folderParent);

	    this.propertiesTab = new TabItem(folderParent, SWT.NONE);
	    this.propertiesTab.setControl(thePanel);
	    this.propertiesTab.setText(Messages.actions);
	}
	
	private void createDescriptionTab(TabFolder folderParent) {
		Composite thePanel = createDescriptionPanel(folderParent);
		
	    this.propertiesTab = new TabItem(folderParent, SWT.NONE);
	    this.propertiesTab.setControl(thePanel);
	    this.propertiesTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DESCRIPTION));
	}
	
    private Composite createActionListPanel(Composite parent ) {
        Composite rightActionList = getToolkit().createComposite(parent);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(2, 2).applyTo(rightActionList);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(rightActionList);

        hyperLinkActionManager.addPrimaryHyperLinkActions(rightActionList);
        
        if( isRelational && !isVirtual ) {

        	Label separator = getToolkit().createSeparator(rightActionList, SWT.HORIZONTAL);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(separator);
	        hyperLinkActionManager.addConnectionHyperLinkActions(rightActionList);
        }
        
        Label separator = getToolkit().createSeparator(rightActionList, SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(separator);
        hyperLinkActionManager.addGeneralHyperLinkActions(rightActionList);
        
        return rightActionList;
    }
    
    private Composite createPropertiesPanel(Composite parent ) {
        Composite composite = getToolkit().createComposite(parent);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5).spacing(5, 5).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
    	this.propertiesPanel = new ModelObjectPropertiesPanel(composite, false);
    	return composite;
    }

    private Composite createDescriptionPanel(Composite parent) {
        Composite composite = getToolkit().createComposite(parent);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5).spacing(5, 5).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
        
        editDescriptionHL = getToolkit().createHyperlink(composite, Messages.edit, SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(GridData.END, GridData.CENTER).grab(true, false).applyTo(editDescriptionHL);
        editDescriptionHL.addHyperlinkListener(new HyperlinkAdapter() {
          
        @Override
        public void linkActivated(HyperlinkEvent e) {
              	// Launch Edit Description Dialog
          		editDescription();
          	}
        });
        
        textViewerPanel = new StyledTextEditor(composite, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        textViewerPanel.setAllowCut(false);
        textViewerPanel.setAllowPaste(false);
        textViewerPanel.setAllowUndoRedo(false);
        textViewerPanel.setAllowFind(true);
        
        GridData tvGD = new GridData(GridData.FILL_BOTH);
        tvGD.horizontalSpan = 2;
        textViewerPanel.setLayoutData(tvGD);
        textViewerPanel.setEditable(false);
        Color newColor = UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
        textViewerPanel.getTextWidget().setBackground(newColor);
        
        return composite;
    }
    
    protected FormToolkit getToolkit() {
    	return this.toolkit;
    }
    
    @Override
    public void setFocus() {
    	// NOOP
    }

    @Override
    public void doSave(IProgressMonitor monitor) {

    }

    @Override
    public void doSaveAs() {
        // do nothing
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    private void resetDescription() {
    	if( this.textViewerPanel == null || this.textViewerPanel.isDisposed() ) return;
    	
        String description = Messages.undefined;

    	if( getSelectedObject() instanceof EObject ) {
    		String value = ModelObjectUtilities.getDescription((EObject)getSelectedObject());
    		if( StringUtilities.isNotEmpty(value) ) {
    			description = value;
    		}
    		
    	} else if( getSelectedObject() instanceof IFile ) {
    		String value = null;
	        try {
	            value = modelResource.getDescription();
	        } catch (ModelerCoreException theException) {
	            UiConstants.Util.log(IStatus.ERROR, theException.getMessage());
	        } 
	        if( StringUtilities.isNotEmpty(value)) {
	        	description = value;
	        }
    	}

        this.textViewerPanel.setText(description);
    }
    
    private Object getSelectedObject() {
    	IStructuredSelection selection = (IStructuredSelection)modelTreeViewer.getSelection();
    	if( selection.isEmpty() ) return null;
    	
    	return selection.getFirstElement();
    }
    
    private String getModelDescription() {
        String description = Messages.undefined;

        try {
            description = modelResource.getDescription();
            if( description == null ) {
            	description = Messages.undefined;
            }
            
        } catch (ModelerCoreException theException) {
            UiConstants.Util.log(IStatus.ERROR, theException.getMessage());
        }
        return description;
    }
    
    private void editDescription() {
    	
    	Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    	// Get Selection and lauch dialog
    	
    	if( getSelectedObject() instanceof EObject ) {
    		EObject eObj = (EObject)getSelectedObject();
    		String name = ModelObjectUtilities.getName(eObj);
    		String desc = ModelObjectUtilities.getDescription(eObj);
        	EditDescriptionDialog dialog = new EditDescriptionDialog(shell, name, desc);

            if (dialog.open() == Window.OK) {
            	String newDescription = dialog.getChangedDescription();
            	ModelObjectUtilities.setDescription(eObj, newDescription, this);
                resetDescription();
            }
    	} else if( getSelectedObject() instanceof IFile ) {
	    	EditDescriptionDialog dialog = new EditDescriptionDialog(shell, modelResource.getItemName(), getModelDescription());
	
	        if (dialog.open() == Window.OK) {
	        	String newDescription = dialog.getChangedDescription();
	            ModelUtilities.setModelDescription(modelResource, newDescription);
	            resetDescription();
	        }
        }
    }


	@Override
    public boolean canDisplay(IEditorInput input) {
        if (input instanceof IFileEditorInput) {
            IFile theFile = ((IFileEditorInput)input).getFile();
            
            boolean isValidFile = ModelUtilities.isModelFile(theFile);
            
            return isValidFile;
        }
        return false;
    }
	
	@Override
    public boolean canOpenContext(Object input) {
        boolean canOpen = false;
        if( input instanceof ModelResource ) {
            canOpen = true;
        } else if( input instanceof IFile ) {
            canOpen = modelResource != null;
        }

        return canOpen;
    }

	@Override
    public void openContext(final Object input) {
        if( input instanceof IResource ) {
            // Do this
        } else if( input instanceof EObject ) {
            
        } else if( input instanceof ModelResource) {
            
        }
    }

	@Override
    public void openContext(Object input, boolean forceRefresh) {
        openContext(input);
	}

	@Override
	public void initializeEditorPage() {
		// NO IMPLEMENTATION
	}

	@Override
	public Control getControl() {
		return mainControl;
	}

	@Override
	public ISelectionProvider getModelObjectSelectionProvider() {
		// NO IMPLEMENTATION
		return null;
	}

	@Override
	public ISelectionChangedListener getModelObjectSelectionChangedListener() {
		// NO IMPLEMENTATION
		return null;
	}

	@Override
	public AbstractModelEditorPageActionBarContributor getActionBarContributor() {
		// NO IMPLEMENTATION
		return null;
	}

	@Override
	public void setLabelProvider(ILabelProvider provider) {
		// NO IMPLEMENTATION
		
	}

	@Override
	public INotifyChangedListener getNotifyChangedListener() {
		// // NO IMPLEMENTATION
		return null;
	}

	@Override
	public ModelEditorPageOutline getOutlineContribution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateReadOnlyState(boolean isReadOnly) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTitleText(String title) {
		// NO IMPLEMENTATION
		
	}

	@Override
	public void preDispose() {
		this.propertiesPanel.dispose();
		
	}

	@Override
	public void openComplete() {
		// NO IMPLEMENTATION
		
	}

	@Override
	public boolean isSelectedFirst(IEditorInput input) {
		// NO IMPLEMENTATION
		return false;
	}
	
    public void resourceChanged(IResourceChangeEvent theEvent) {
        switch( theEvent.getType() ) {
            case IResourceChangeEvent.POST_CHANGE: {
                
            }
        }
    }


    @SuppressWarnings("rawtypes")
	public void notifyChanged(Notification theNotification) {
        
        if (theNotification instanceof SourcedNotification) {
            Object source = ((SourcedNotification)theNotification).getSource();
            if( source == null || (source != null && !source.equals(this))) {
				Collection notifications = ((SourcedNotification)theNotification).getNotifications();
                Iterator iter = notifications.iterator();
                Notification nextNotification = null;
                
                while (iter.hasNext() ) {
                    nextNotification = (Notification)iter.next();
                    
                    handleNotification(nextNotification);
                }
            }
        } else {
            Object targetObject = ModelerCore.getModelEditor().getChangedObject(theNotification);
            if(    targetObject != null  ) {
                if( targetObject instanceof EObject  ) {
                    // If notification is from another "model resource" we don't care for Coarse
                    // Mapping diagram.  All objects are in same model.
                    // Check here if the targetObject and document have the same resource, then set to TRUE;
                    ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)targetObject);
                    if( mr != null && mr.equals(this.modelResource)) {
                        handleNotification(theNotification);
                    }
                } else if( targetObject instanceof Resource ) {
                    Resource targetResource = (Resource)targetObject;
                    if( targetResource.equals(this.modelResource))
                        handleNotification(theNotification);
                }
            }
        }
    }
    
    private void handleNotification(Notification notification) {
        // If we get here, we need to make sure the 
        
        switch( notification.getEventType() ) {
            case Notification.ADD:
            case Notification.ADD_MANY:
            case Notification.REMOVE:
            case Notification.REMOVE_MANY:{
                EObject eObj = NotificationUtilities.getEObject(notification);
                if( eObj != null && ! (eObj instanceof Diagram || eObj instanceof DiagramEntity) ) {
	                Runnable work = new Runnable() {
	                    @Override
	        			public void run() {
	    	                modelTreeViewer.refresh();
	                    }
	                };

	                UiUtil.runInSwtThread(work, true);
                }
            } break;
            
            case Notification.SET: {
                EObject eObj = NotificationUtilities.getEObject(notification);
                if( eObj != null ) {
	                Runnable work = new Runnable() {
	                    @Override
	        			public void run() {
	    	                if( eObj instanceof ModelAnnotation || eObj instanceof Annotation) {
	    	                    resetDescription();
	    	                } else {
	    	                	if( getSelectedObject() != null ) {
	    	                		propertiesPanel.selectionChanged(new SelectionChangedEvent(ModelEditorMainPage.this, new StructuredSelection(getSelectedObject())) );
	    	                	}
	    	                }
	                    }
	                };

	                UiUtil.runInSwtThread(work, true);
                }
            } break;
        }
    }
    

    public void processEvent(EventObject obj) {
        if( obj instanceof ModelResourceEvent ) {
            ModelResourceEvent event = (ModelResourceEvent) obj;
            
            // Need to update Ui components for content
            
            switch( event.getType() ) {
                case ModelResourceEvent.RELOADED:
                case ModelResourceEvent.CHANGED: {
                    // Update ALL
                	// TODO: resetAllData();
                } break;
                case ModelResourceEvent.MOVED: {
                    // Update General:Location field
                	// TODO: resetLocation();
                } break;
                case ModelResourceEvent.REBUILD_IMPORTS: {
                    // Update Imports List
                	// TODO: resetImports();
                } break;
                
                default: 
                    break;
                
            }

        }
    }
    
    
    public String getDateAsString( long timestamp ) {
        Date theDate = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss aaa z"); //$NON-NLS-1$
        formatter.setLenient(false);
        
        return formatter.format(theDate);
    }

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {

	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(new Object());
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {

	}

	@Override
	public void setSelection(ISelection selection) {
		System.out.println(" MEMP >> Selection ="+  selection);
	}
}