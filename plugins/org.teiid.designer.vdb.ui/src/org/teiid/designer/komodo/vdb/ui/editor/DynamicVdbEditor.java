/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb.ui.editor;

import static org.teiid.designer.vdb.Vdb.Event.CLOSED;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.ADD;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.REMOVE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelWorkspace;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.komodo.vdb.Model;
import org.teiid.designer.komodo.vdb.Vdb;
import org.teiid.designer.komodo.vdb.dynamic.DynamicVdb;
import org.teiid.designer.komodo.vdb.ui.editor.panels.DescriptionPanel;
import org.teiid.designer.komodo.vdb.ui.editor.panels.ModelDetailsPanel;
import org.teiid.designer.komodo.vdb.ui.editor.panels.PropertiesPanel;
import org.teiid.designer.komodo.vdb.ui.editor.panels.TranslatorOverridesPanel;
import org.teiid.designer.komodo.vdb.ui.editor.panels.UserDefinedPropertiesPanel;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.util.ErrorHandler;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.ui.Messages;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.VdbUiPlugin;

/**
 * @author blafond
 *
 */
public class DynamicVdbEditor extends EditorPart implements IResourceChangeListener {
    static final String INVALID_INTEGER_INPUT_TITLE = i18n("invalidVdbVersionValueTitle"); //$NON-NLS-1$
    static final String INVALID_INTEGER_INPUT_MESSAGE = i18n("invalidVdbVersionValueMessage"); //$NON-NLS-1$
    static final int MODELS_PANEL_WIDTH_HINT = 300;  // Models Panel Overall Width
    static final int MODELS_PANEL_IMAGE_COL_WIDTH = 50;  // Image Cols Width
    static final int MODELS_PANEL_MODELNAME_COL_WIDTH_MIN = 200;  // Min ModelName Width
    
    static String i18n( final String id ) {
        return VdbUiConstants.Util.getString(id);
    }
    
    private DynamicVdb vdb;
    private IFile dynamicVdbFile;
    Exception vdbLoadingException = null;


    private Label validationDateTimeLabel;
    private Label validationVersionLabel;
    private PropertyChangeListener vdbListener;

//    private DataRolesPanel dataRolesPanel;
//    VdbDataRoleResolver dataRoleResolver;
    TranslatorOverridesPanel pnlTranslatorOverrides;

    @SuppressWarnings( "unused" )
    private PropertiesPanel propertiesPanel;
    
    @SuppressWarnings( "unused" )
    private UserDefinedPropertiesPanel userDefinedPropertiesPanel;

    DescriptionPanel descriptionPanel;
    ModelDetailsPanel modelDetailsPanel;
    

    ListViewer modelsViewer;
    List<String> models = new ArrayList<String>();
	Button addModelButton;
	Button removeModelButton;
    
    boolean disposed = false;
    
	/**
	 * 
	 */
	public DynamicVdbEditor() {
		// TODO Auto-generated constructor stub
	}
	
    /**
     * @return the VDB being edited
     */
    public DynamicVdb getVdb() {
        return vdb;
    }

	/** (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/** (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor arg0) {
		// TODO Auto-generated method stub
		
	}

	/** (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	/** (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) {
		


        IResource theFile = ((IFileEditorInput)input).getFile();
        theFile.getFullPath().makeAbsolute();
        dynamicVdbFile = (IFile)theFile;
        File actualFile = dynamicVdbFile.getRawLocation().makeAbsolute().toFile();

        try {
            ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(theFile, false);
            IFile wsFile = ResourcesPlugin.getWorkspace().getRoot().getFile(theFile.getFullPath());
    		if( !VdbUtil.isDynamicVdb(dynamicVdbFile) ) {
    			throw new PartInitException("File " + dynamicVdbFile.getName() + " is not a dynamic VDB");
    		}
    		
        		vdb = new DynamicVdb(actualFile);
        		vdb.load();
//        		vdbListener = new PropertyChangeListener() {
//                /**
//                 * {@inheritDoc}
//                 *
//                 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
//                 */
//                @Override
//                public void propertyChange(final PropertyChangeEvent event) {
//                    UiUtil.runInSwtThread(new Runnable() {
//                        /**
//                         * {@inheritDoc}
//                         *
//                         * @see java.lang.Runnable#run()
//                         */
//                        @Override
//                        public void run() {
//                            if (!disposed) {
//                                vdbNotification(event.getPropertyName());
//                            }
//                        }
//                    }, true);
//                }
//            };
//            vdb.addChangeListener(vdbListener);

        } catch (Exception ex) {
            ErrorHandler.toExceptionDialog(ex);
            vdbLoadingException  = ex;
        }

        setSite(site);
        setInput(input);
        setPartName(dynamicVdbFile.getName());

	}

	/** (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	/** (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	/** (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout());
        parent.setLayoutData(new GridData());

        if (vdb == null) {
            createErrorPanel(parent);
            return;
        }
      
        { // Header Panel
	        Composite headerPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL, 1, 4);
	        Label projectLabel = new Label(headerPanel, SWT.NONE);
	        projectLabel.setText(Messages.vdbEditor_location);
	        
	        Label project = new Label(headerPanel, SWT.NONE);
	        project.setText(dynamicVdbFile.getParent().getFullPath().toString());
	        project.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
	        
	        Label vdbVersionLabel = WidgetFactory.createLabel(headerPanel, "Version"); //$NON-NLS-1$
	    	GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(vdbVersionLabel);
	    	
	    	final Text vdbVersionText = WidgetFactory.createTextField(headerPanel);
	    	GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(vdbVersionText);
	    	((GridData)vdbVersionText.getLayoutData()).widthHint = 30;
	    	vdbVersionText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					try {
	                    int versionValue = Integer.parseInt(vdbVersionText.getText());
	                    if (versionValue > -1) {
	                        getVdb().setVersion(versionValue);
						}
					} catch (NumberFormatException ex) {
						MessageDialog.openWarning(Display.getCurrent().getActiveShell(),
	                            INVALID_INTEGER_INPUT_TITLE,
	                            INVALID_INTEGER_INPUT_MESSAGE);
						vdbVersionText.setText(Integer.toString(getVdb().getVersion()));
					}
					
				}
			});
	    	vdbVersionText.setText(Integer.toString(getVdb().getVersion()));
        }

        
        // So create another Tab Folder (bottom oriented)
        CTabFolder tabFolder = WidgetFactory.createTabFolder(parent);
        tabFolder.setTabPosition(SWT.BOTTOM);
        { // models tab
            CTabItem leftTab = new CTabItem(tabFolder, SWT.NONE);
            leftTab.setText(Messages.vdbEditor_content_tab_label);
            leftTab.setToolTipText(Messages.vdbEditor_content_tab_tooltip);
            Composite leftPanel = createEditorContentTab(tabFolder);
            
            leftTab.setControl(leftPanel);
        }
        { // advanced tab
            CTabItem rightTab = new CTabItem(tabFolder, SWT.NONE);
            rightTab.setText(Messages.vdbEditor_advanced_tab_label);
            rightTab.setToolTipText(Messages.vdbEditor_advanced_tab_tooltip);
            Composite rightPanel = createEditorAdancedTab(tabFolder);

            rightTab.setControl(rightPanel);
        }
        
        tabFolder.setSelection(0);

        ModelerCore.getWorkspace().addResourceChangeListener(this);
		
	}
	
	private Composite createEditorContentTab(Composite parent) {
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
			pnlModels
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			SashForm splitter = new SashForm(pnlModels, SWT.HORIZONTAL);
			splitter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			Composite pnlModelsList = new Composite(splitter, SWT.NONE);
			pnlModelsList.setLayout(new GridLayout());
			pnlModelsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
					true));
			((GridData) pnlModelsList.getLayoutData()).widthHint = MODELS_PANEL_WIDTH_HINT;

			createModelsSection(pnlModelsList);

			this.modelDetailsPanel = new ModelDetailsPanel(splitter, getVdb());
			splitter.setWeights(new int[] { 35, 65 });

			modelsTab.setControl(pnlModels);

		}

		{ // description tab
			CTabItem descriptionTab = new CTabItem(tabFolder, SWT.NONE);
			descriptionTab.setText(i18n("descriptionTab")); //$NON-NLS-1$
			descriptionTab.setToolTipText(i18n("descriptionTabToolTip")); //$NON-NLS-1$
			Composite pnlDescription = new Composite(tabFolder, SWT.NONE);
			pnlDescription.setLayout(new GridLayout());
			pnlDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					true));
			descriptionTab.setControl(pnlDescription);
			descriptionPanel = new DescriptionPanel(pnlDescription,	getVdb());
		}

		tabFolder.setSelection(0);

		return pnlTop;
	}
	
	private void createModelsSection( Composite parent ) {
		// List Box of <model> entries
		// Name and ICON only
		// Models can be Physical or Virtual
		Group languageGroup = WidgetFactory.createGroup(parent, "Models", SWT.FILL, 1, 1);  //$NON-NLS-1$
		GridData gd_2 = new GridData(GridData.FILL_BOTH);
		gd_2.widthHint = 220;
//		gd_2.horizontalSpan = 2;
		languageGroup.setLayoutData(gd_2);
		// Add a simple list box entry form with String contents
    	this.modelsViewer = new ListViewer(languageGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=2;
        this.modelsViewer.getControl().setLayoutData(data);
        
        this.modelsViewer.setContentProvider(new IStructuredContentProvider() {
        	@Override
			public Object[] getElements(Object inputElement) {
        		return getVdb().getModels();
        	}

        	@Override
			public void dispose() {
        	}

        	@Override
			public void inputChanged(
        			Viewer viewer,
        			Object oldInput,
        			Object newInput) {
        	}
        });
        
        this.modelsViewer.setLabelProvider(new ILabelProvider() {
			
			@Override
			public void removeListener(ILabelProviderListener arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isLabelProperty(Object arg0, String arg1) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addListener(ILabelProviderListener arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getText(Object element) {
				if( element instanceof Model ) {
					return ((Model)element).getName();
				}
				return StringConstants.EMPTY_STRING;
			}
			
			@Override
			public Image getImage(Object element) {
				if( element instanceof Model ) {
					if( ((Model)element).getModelType() == Model.Type.PHYSICAL ) {
						return ModelIdentifier.getImage(ModelIdentifier.RELATIONAL_SOURCE_MODEL_ID);
					}
					return ModelIdentifier.getImage(ModelIdentifier.RELATIONAL_VIEW_MODEL_ID);
				}
				return null;
			}
		});
          
        this.modelsViewer.setInput(vdb.getModels()); 
        
//        for( String value : vdb.getAllowedLanguages() ) {
//        		this.languages.add(value);
//        }
        
        this.modelsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
            	handleModelSelected();
            }
        });
        this.modelsViewer.refresh();
        
        Composite toolbarPanel = WidgetFactory.createPanel(languageGroup, SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 2);
        
        this.addModelButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.addModelButton.setImage(VdbUiPlugin.singleton.getImage(ADD));
//        this.addModelButton.setToolTipText(prefixedI18n("addModelButton.tooltip")); //$NON-NLS-1$
        this.addModelButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleAddModel();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        this.removeModelButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.removeModelButton.setImage(VdbUiPlugin.singleton.getImage(REMOVE));
//        this.removeModelButton.setToolTipText(prefixedI18n("removeLangueButton.tooltip")); //$NON-NLS-1$
        this.removeModelButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRemoveModel();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        this.removeModelButton.setEnabled(false);
	}
	
	void handleModelSelected() {
		boolean hasSelection = !this.modelsViewer.getSelection().isEmpty();
		this.removeModelButton.setEnabled(hasSelection);
	}
	
    private Model getSelectedModel() {
        IStructuredSelection selection = (IStructuredSelection)this.modelsViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (Model)selection.getFirstElement();
    }
    
    void handleAddModel() {
        assert (!this.modelsViewer.getSelection().isEmpty());

//        AddLanguagePropertyDialog dialog = new AddLanguagePropertyDialog(modelsViewer.getControl().getShell(), 
//        				vdb.getAllowedLanguages());
//
//
//        if (dialog.open() == Window.OK) {
//            // update model
//            String language = dialog.getLanguage();
//
//            vdb.addAllowedLanguage(language);
//            
//            this.modelsViewer.refresh();
//
//            // select the new property
//            
//            
//            VdbEntry model = null;
//            
//            for(String item : this.modelsViewer.getList().getItems() ) {
//            	if( item.equals(language) ) {
//            		model = item;
//            		break;
//            	}
//            }
//
//            if( model != null ) {
//                this.modelsViewer.setSelection(new StructuredSelection(model), true);
//            }
//        }
    }
    
    void handleRemoveModel() {
        Model selectedModel = getSelectedModel();
        assert (selectedModel != null);

        // update model
        this.vdb.removeModel(selectedModel.getName());

        // update UI
        this.modelsViewer.refresh();
    }
	
	private Composite createEditorAdancedTab(Composite parent) {
        Composite pnlBottom = new Composite(parent, SWT.BORDER);
        pnlBottom.setLayout(new GridLayout());
        pnlBottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        CTabFolder tabFolder = WidgetFactory.createTabFolder(pnlBottom);

//        { // roles tab
//            CTabItem rolesTab = new CTabItem(tabFolder, SWT.NONE);
//            rolesTab.setText(i18n("rolesTab")); //$NON-NLS-1$
//            rolesTab.setToolTipText(i18n("rolesTabToolTip")); //$NON-NLS-1$
//            Composite pnlRoles = new Composite(tabFolder, SWT.NONE);
//            pnlRoles.setLayout(new GridLayout());
//            pnlRoles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//            rolesTab.setControl(pnlRoles);
//            dataRolesPanel = new DataRolesPanel(pnlRoles, this);
//        }

        { // properties tab
            CTabItem propertiesTab = new CTabItem(tabFolder, SWT.NONE);
            propertiesTab.setText(i18n("propertiesTab")); //$NON-NLS-1$
            propertiesTab.setToolTipText(i18n("propertiesTabToolTip")); //$NON-NLS-1$
            Composite pnlProperties = new Composite(tabFolder, SWT.NONE);
            pnlProperties.setLayout(new GridLayout());
            pnlProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            propertiesTab.setControl(pnlProperties);
            propertiesPanel = new PropertiesPanel(pnlProperties, getVdb());
        }
        
        { // properties tab
            CTabItem tab = new CTabItem(tabFolder, SWT.NONE);
            tab.setText(i18n("userDefinedPropertiesTab")); //$NON-NLS-1$
            tab.setToolTipText(i18n("userDefinedPropertiesTabToolTip")); //$NON-NLS-1$
            Composite pnlProperties = new Composite(tabFolder, SWT.NONE);
            pnlProperties.setLayout(new GridLayout());
            pnlProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            tab.setControl(pnlProperties);
            userDefinedPropertiesPanel = new UserDefinedPropertiesPanel(pnlProperties, getVdb());
        }

//        { // translator overrides tab
//            CTabItem translatorOverridesTab = new CTabItem(tabFolder, SWT.NONE);
//            translatorOverridesTab.setText(i18n("translatorOverridesTab")); //$NON-NLS-1$
//            translatorOverridesTab.setToolTipText(i18n("translatorOverridesTabToolTip")); //$NON-NLS-1$
//            pnlTranslatorOverrides = new TranslatorOverridesPanel(tabFolder, this.vdb);
//            translatorOverridesTab.setControl(pnlTranslatorOverrides);
//        }

        tabFolder.setSelection(0);
        
        return pnlBottom;
	}
    
    private void createErrorPanel(Composite parent) {
        Color bgColour = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
        parent.setBackground(bgColour);

        final Composite errorPanel = WidgetFactory.createPanel(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(errorPanel);

        Composite imgMsgPanel = WidgetFactory.createPanel(errorPanel);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(imgMsgPanel);

        Label imageLabel = WidgetFactory.createLabel(imgMsgPanel, SWT.NONE);
        imageLabel.setBackground(bgColour);
        Image image = parent.getDisplay().getSystemImage(SWT.ICON_ERROR);
        if (image != null) {
            image.setBackground(bgColour);
            imageLabel.setImage(image);
            GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(imageLabel);
        }

        Label errLabel = WidgetFactory.createLabel(imgMsgPanel, Messages.vdbEditor_loadingErrMessage);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(errLabel);

        final Button detailsButton = WidgetFactory.createButton(errorPanel, SWT.PUSH);
        GridDataFactory.swtDefaults().grab(true, false).applyTo(detailsButton);
        detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
        detailsButton.setData(Boolean.FALSE);

        final Composite detailsComposite = WidgetFactory.createPanel(errorPanel);
        GridDataFactory.defaultsFor(detailsComposite).applyTo(detailsComposite);
        GridLayoutFactory.fillDefaults().margins(25, 25).applyTo(detailsComposite);

        detailsButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (Boolean.TRUE.equals(detailsButton.getData())) {
                    // Hide the details
                    for (Control control : detailsComposite.getChildren())
                        control.dispose();

                    detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
                    detailsButton.setData(Boolean.FALSE);
                } else {
                    // Show the details
                    String trace = CoreStringUtil.getStackTrace(vdbLoadingException);
                    Text exceptionText = WidgetFactory.createTextBox(detailsComposite, SWT.READ_ONLY, SWT.FILL, 1, trace);
                    GridDataFactory.fillDefaults().grab(true, true).hint(200, 400).applyTo(exceptionText);

                    detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
                    detailsButton.setData(Boolean.TRUE);
                }

                errorPanel.layout(true);
                errorPanel.getParent().layout(true);
            }
        });
        detailsButton.setVisible(vdbLoadingException != null);
    }

	/** (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	

    void vdbNotification( final String property ) {
        if (CLOSED.equals(property)) return;

//    	String dateTimeString = i18n("undefined"); //$NON-NLS-1$
//    	if( getVdb().getValidationDateTime() != null ) {
//    		dateTimeString = getVdb().getValidationDateTime().toString();
//    	}
//    	this.validationDateTimeLabel.setText(dateTimeString);
//    	String validationString = i18n("undefined"); //$NON-NLS-1$
//    	if( getVdb().getValidationVersion() != null ) {
//    		validationString = getVdb().getValidationVersion();
//    	}
//    	this.validationVersionLabel.setText(validationString);
    	
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

}
