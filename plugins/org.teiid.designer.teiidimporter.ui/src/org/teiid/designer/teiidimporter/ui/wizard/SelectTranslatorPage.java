/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.teiidimporter.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.translators.TranslatorOverrideProperty;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.teiidimporter.ui.panels.ImportPropertiesPanel;
import org.teiid.designer.teiidimporter.ui.panels.TranslatorHelper;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.TranslatorOverridePropertyEditingSupport;
import org.teiid.designer.ui.viewsupport.TranslatorPropertyLabelProvider;


/**
 * SelectTranslatorPage 
 * TeiidImportWizard page - for selection of the translator
 * 
 * @since 8.1
 */
public class SelectTranslatorPage extends AbstractWizardPage implements UiConstants {

    private static final String EMPTY_STR = ""; //$NON-NLS-1$
    private static final String SERVER_PREFIX = "Default Server: "; //$NON-NLS-1$

    private TeiidImportManager importManager;

    private Text dataSourceNameText;
    private Text dataSourceDriverText;
    
    private Collection<String> translatorNames = new ArrayList<String>();
    private Combo translatorNameCombo;
    
    private TableViewerBuilder propertiesViewerBuilder;

    /**
     * SelectedTranslatorAndTargetPage Constructor
     * @param importManager the TeiidImportManager for the wizard
     */
    public SelectTranslatorPage( TeiidImportManager importManager ) {
        super(SelectTranslatorPage.class.getSimpleName(), Messages.SelectTranslatorPage_title); 
        this.importManager = importManager;
    }

    @Override
    public void createControl( Composite theParent ) {
        final Composite hostPanel = new Composite(theParent, SWT.NONE);
        hostPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        hostPanel.setLayout(new GridLayout(1, false));
        
        // Create page            
        DefaultScrolledComposite scrolledComposite = new DefaultScrolledComposite(hostPanel, SWT.H_SCROLL | SWT.V_SCROLL);
    	scrolledComposite.setExpandHorizontal(true);
    	scrolledComposite.setExpandVertical(true);
        GridLayoutFactory.fillDefaults().equalWidth(false).applyTo(scrolledComposite);
        GridDataFactory.fillDefaults().grab(true,  false);

        final Composite mainPanel = scrolledComposite.getPanel(); //new Composite(scrolledComposite, SWT.NONE);
        mainPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        mainPanel.setLayout(new GridLayout(1, false));

        setControl(hostPanel);
        
        // Must have a running 8+ server to use this wizard.
        if(!importManager.isValidImportServer()) {
            setErrorMessage(Messages.selectDataSourcePage_InvalidServerMsg); 
            setPageComplete(false);
            return;
        }
        
        Label serverNameLabel = new Label(mainPanel,SWT.NONE);
        String serverString;
        try {
            serverString = importManager.getDisplayName();
        } catch (Exception ex) {
            serverString = "Unknown"; //$NON-NLS-1$
        }
        serverNameLabel.setText(SERVER_PREFIX + serverString);
        serverNameLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        Font bannerFont = JFaceResources.getBannerFont();
        serverNameLabel.setFont(bannerFont);

        // Group for selection of the Connections
        createDataSourceAndTranslatorPanel(mainPanel);
        
        createImportPropertiesPanel(mainPanel);
        
        // Panel for Optional Properties
        new ImportPropertiesPanel(mainPanel, importManager);
        
        scrolledComposite.sizeScrolledPanel();

        // Validate the page
        validatePage();
    }


    /*
     * Panel for selection of the Connection Type
     * @param parent the parent Composite
     */
    private void createDataSourceAndTranslatorPanel(Composite parent) {
        // -------------------------------------
        // Create the Source Definition Group
        // -------------------------------------
        Group sourceGroup = WidgetFactory.createGroup(parent, Messages.SelectTranslatorPage_SrcDefnGroup, SWT.NONE, 1, 2);
        sourceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // -------------------------------------
        // DataSource Name
        // -------------------------------------
        Label dsNameLabel = new Label(sourceGroup,SWT.NONE);
        dsNameLabel.setText(Messages.SelectTranslatorPage_dsNameLabel);
        
        dataSourceNameText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
        dataSourceNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        String dsName = this.importManager.getDataSourceName();
        if(dsName!=null) dataSourceNameText.setText(dsName);
        dataSourceNameText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        dataSourceNameText.setEditable(false);

        // -----------------------
        // DataSource Driver Name
        // -----------------------
        Label dsDriverLabel = new Label(sourceGroup,SWT.NONE);
        dsDriverLabel.setText(Messages.SelectTranslatorPage_dsTypeLabel);            

        dataSourceDriverText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
        dataSourceDriverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        String dsDriver = this.importManager.getDataSourceDriverName();
        if(dsDriver!=null) dataSourceDriverText.setText(dsDriver);
        dataSourceDriverText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        dataSourceDriverText.setEditable(false);
        
        // -------------------------------------
        // Combo for Translator selection
        // -------------------------------------

        Label translatorLabel = new Label(sourceGroup,SWT.NONE);
        translatorLabel.setText(Messages.SelectTranslatorPage_translatorLabel);
        
        refreshTranslators();
        
        this.translatorNameCombo = WidgetFactory.createCombo(sourceGroup,
                                                                 SWT.READ_ONLY,
                                                                 GridData.FILL_HORIZONTAL,
                                                                 translatorNames.toArray());
        this.translatorNameCombo.setVisibleItemCount(8);
        this.translatorNameCombo.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleTranslatorChanged();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
                
    }
    
    private void createImportPropertiesPanel(Composite parent) {
        Group mainPanel = WidgetFactory.createGroup(parent, Messages.SelectTranslatorPage_importPropertiesLabel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainPanel);

        { // right-side is an override description and table with the selected translator's properties
            Composite pnlOverrides = new Composite(mainPanel, SWT.FILL);
            pnlOverrides.setLayout(new GridLayout(1, false));
            pnlOverrides.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            propertiesViewerBuilder = new TableViewerBuilder(pnlOverrides, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

            ColumnViewerToolTipSupport.enableFor(this.propertiesViewerBuilder.getTableViewer());
            this.propertiesViewerBuilder.setContentProvider(new IStructuredContentProvider() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
                 */
                @Override
                public void dispose() {
                    // nothing to do
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
                 */
                @Override
                public Object[] getElements( Object inputElement ) {
                	TranslatorOverrideProperty[] props = importManager.getTranslatorOverride().getProperties();
                	for( TranslatorOverrideProperty top : props ) {
//                		System.out.println(" TOP:  ID = " + top.getDefinition().getId());
                		if( top.getDefinition().getId().equalsIgnoreCase(JdbcTranslatorKeys.TABLE_TYPES)) {
                			if( top.getOverriddenValue() == null ) {
                				top.setValue(JdbcTranslatorDefaults.TABLE_TYPES);
                			}
                		} else if( top.getDefinition().getId().equalsIgnoreCase(JdbcTranslatorKeys.USE_QUALIFIED_NAME)) {
                			if( top.getOverriddenValue() == null || top.getOverriddenValue().equalsIgnoreCase(Boolean.toString(true)) ) {
                				top.setValue(JdbcTranslatorDefaults.USE_QUALIFIED_NAME);
                			}
                		} 
                		
                	}
                	
                    return importManager.getTranslatorOverride().getProperties();
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
                 *      java.lang.Object)
                 */
                @Override
                public void inputChanged( Viewer viewer,
                                          Object oldInput,
                                          Object newInput ) {
                    // nothing to do
                }
            });

            // sort the table rows by display name
            this.propertiesViewerBuilder.setComparator(new ViewerComparator() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
                 *      java.lang.Object)
                 */
                @Override
                public int compare( Viewer viewer,
                                    Object e1,
                                    Object e2 ) {
                    TranslatorOverrideProperty prop1 = (TranslatorOverrideProperty)e1;
                    TranslatorOverrideProperty prop2 = (TranslatorOverrideProperty)e2;

                    return super.compare(viewer, prop1.getDefinition().getDisplayName(), prop2.getDefinition().getDisplayName());
                }
            });

            // create columns
            TableViewerColumn column = propertiesViewerBuilder.createColumn(SWT.LEFT, 70, 100, true);
            column.getColumn().setText("Property"); //$NON-NLS-1$
            column.setLabelProvider(new TranslatorPropertyLabelProvider(true));

            column = propertiesViewerBuilder.createColumn(SWT.LEFT, 30, 100, true);
            column.getColumn().setText("Value"); //$NON-NLS-1$
            column.setLabelProvider(new TranslatorPropertyLabelProvider(false));
            column.setEditingSupport(new ImportPropertyEditingSupport(this.propertiesViewerBuilder.getTableViewer()));

            //
            // add note below the table
            //
            Composite toolbarPanel = WidgetFactory.createPanel(pnlOverrides, SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 1);
            
        	org.teiid.designer.ui.common.widget.Label noteLabel = 
        			WidgetFactory.createLabel(toolbarPanel, "Note: See Teiid documentation for details on importer properties"); //$NON-NLS-1$
        	noteLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        	GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(noteLabel);

        }

    }
    
    /*
     * Get the initial Translator selection.  Attempts to choose the best option,
     * based on the name of the driver being used.
     * @return the translator name
     */
    private String getInitialTranslatorSelection() {
    	if( importManager.getTranslatorName() != null ) {
    		return importManager.getTranslatorName();
    	}
    	
        String driverName = importManager.getDataSourceDriverName();
        if(translatorNames.isEmpty() || CoreStringUtil.isEmpty(driverName)) {
            return null;
        }
		ITeiidServerVersion teiidVersion = null;
        try {
			teiidVersion = importManager.getTeiidServerVersion();
		} catch (Exception ex) {
            UTIL.log(ex);
		}
        
        // Now we need to set the proper translator name if possible
        // NOte that driverName is good for JDBC jar-related data sources
        // For Resource Adapters, the "class-name" data source property should be used
        // String classNameValue = getPropertyValue("class-name", propertyItems);  //$NON-NLS-1$
        
        return TranslatorHelper.getTranslator(driverName, importManager.getDataSourceClassName(), translatorNames, teiidVersion);
    }
    
    /*
     * Refresh the list of currently available translators on the server
     */
    private void refreshTranslators() {
        try {
            translatorNames.clear();
            Collection<ITeiidTranslator> availableTranslators = importManager.getTranslators();
            for(ITeiidTranslator translator: availableTranslators) {
                translatorNames.add(translator.getName());
            }
        } catch (Exception ex) {
            translatorNames.clear();
            UTIL.log(ex);
        }
    }
    
    /*
     * Handler for Translator Name Combo Selection
     */
    private void handleTranslatorChanged( ) { 
        // Need to sync the worker with the current profile
        int selIndex = translatorNameCombo.getSelectionIndex();
        String translatorName = translatorNameCombo.getItem(selIndex);
        
        if( !translatorName.equalsIgnoreCase(importManager.getTranslatorName()) ) {
	        importManager.setTranslatorName(translatorName);
	        
	        importManager.getOptionalImportProps().clear();
	        
	        this.propertiesViewerBuilder.setInput(this);
	        
	        // Validate the page
	        validatePage();
        }
    }
    
    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
            String dsName = importManager.getDataSourceName();
            String dsType = importManager.getDataSourceDriverName();
            if(!StringUtilities.isEmpty(dsName) ) {
            	this.dataSourceNameText.setText(dsName);
            }
            if(!StringUtilities.isEmpty(dsType) ) {
            	this.dataSourceDriverText.setText(dsType);
            }
            // Set initial translator selection
            String initialTranslatorSelection = getInitialTranslatorSelection();
            if(initialTranslatorSelection!=null) {
                int indx = this.translatorNameCombo.indexOf(initialTranslatorSelection);
                if(indx!=-1) {
                    this.translatorNameCombo.select(indx);
                    this.importManager.setTranslatorName(initialTranslatorSelection);

                    this.propertiesViewerBuilder.setInput(this);
                    
        	        // TODO: Get Import Properties and add to importManager
                    
                } else {
                    this.importManager.setTranslatorName(null);
                }
            }

            validatePage();
            getControl().setVisible(visible);
        } else {
            super.setVisible(visible);
        }
    }
    

    
    /*
     * Page Validation
     * @return 'true' if the page is valid, 'false' if not
     */
    private boolean validatePage() {
        // Name, Driver and Translator validation
        boolean nameDriverTranslatorValid = validateNameDriverTranslator();
        if(!nameDriverTranslatorValid) return false;

        setThisPageComplete(EMPTY_STR, NONE);
        return true;
    }
    
    /*
     * This method validates the Source name and driver, plus the translator selection.
     * @return 'true' if the entries are non-null, 'false' if not.
     */
    private boolean validateNameDriverTranslator() {
        String dsName = this.importManager.getDataSourceName();
        String dsDriver = this.importManager.getDataSourceDriverName();
        String dsTranslator = this.importManager.getTranslatorName();
        
        if(CoreStringUtil.isEmpty(dsName)) {
            setThisPageComplete(Messages.SelectTranslatorPage_NoDataSourceNameMsg, ERROR);
            return false;
        }
        
        if(CoreStringUtil.isEmpty(dsDriver)) {
            setThisPageComplete(Messages.SelectTranslatorPage_NoDataSourceDriverMsg, ERROR);
            return false;
        }

        if(CoreStringUtil.isEmpty(dsTranslator)) {
            setThisPageComplete(Messages.SelectTranslatorPage_NoTranslatorMsg, ERROR);
            return false;
        }

        return true;
    }
        
    private void setThisPageComplete( String message, int severity) {
        WizardUtil.setPageComplete(this, message, severity);
    }
    
    class ImportPropertyEditingSupport extends TranslatorOverridePropertyEditingSupport {

    	/*
    	 * Overriding super method to determine wither or not a property was changed so we can force the redeployment of a dynamic VDB
    	 * Without this knowledge, clicking back in the wizard and changing import properties would have no effect.
    	 * 
    	 * (non-Javadoc)
    	 * @see org.teiid.designer.ui.viewsupport.TranslatorOverridePropertyEditingSupport#setElementValue(java.lang.Object, java.lang.String)
    	 */
		@Override
		protected void setElementValue(Object element, String newValue) {
	        TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;
	        String currentValue = property.getOverriddenValue();
	        boolean doIt = false;

	        if (StringUtilities.isEmpty(newValue)) {
	            if (!StringUtilities.isEmpty(currentValue)) {
	                doIt = true;
	            }
	        } else {
	            String defaultValue = property.getDefinition().getDefaultValue();

	            // new value is not empty
	            // current value is empty
	            // set value if new value is not the default value
	            if (StringUtilities.isEmpty(currentValue)) {
	                if (StringUtilities.isEmpty(defaultValue) || !defaultValue.equals(newValue)) {
	                    doIt = true;
	                }
	            } else {
	                // new value is not empty
	                // current value is not empty
	                // set if new value != current value
	                // set if new value != default value
	                // if new value == default value set to null
	                if (!newValue.equals(currentValue)) {
	                    doIt = true;

	                    if (!StringUtilities.isEmpty(defaultValue) && defaultValue.equals(newValue)) {
	                        newValue = null;
	                    }
	                }
	            }
	        }

	        if (doIt) {
	            property.setValue(newValue);
	            // cause a selection event to be fired so that actions can set their enablement
	            getViewer().setSelection(new StructuredSelection(element));
	        }
	        
	        importManager.setRedeploy(doIt);
		}

		public ImportPropertyEditingSupport(ColumnViewer viewer) {
			super(viewer, null);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean canEdit(Object element) {
			// TODO Auto-generated method stub
			return true;
		}
    	
    }
}
