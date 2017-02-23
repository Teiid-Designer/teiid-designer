package org.teiid.designer.transformation.ui.editors.summary;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.properties.ModelObjectPropertyDescriptor;
import org.teiid.designer.ui.properties.ModelObjectPropertySourceProvider;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class ModelObjectPropertiesPanel implements UiConstants { //, DataSourcePanelListener {

    private TableViewerBuilder propertiesViewer;

//    private List<IPropertyDesc> propertyItemList = new ArrayList<PropertyItem>();

    private PropertyLabelProvider namePropLabelProvider;
    private PropertyLabelProvider valuePropLabelProvider;
    
	IPropertySource pSource;
    
    private Composite primaryPanel;

	private ModelObjectPropertySourceProvider propertySourceProvider = ModelUtilities.getPropertySourceProvider();
    
    /**
     * DataSourcePropertiesPanel constructor
     * @param parent the parent composite
     * @param teiidImportServer the TeiidServer
     * @param isReadOnly whether the panel is readonly
     * @param isCreateNew 'true' if creating a new source, 'false' if not
     * @param initialSelection the initialSelection to display
     */
    public ModelObjectPropertiesPanel( Composite parent,  boolean isReadOnly ) {
        
    	primaryPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 2);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(primaryPanel);
        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(primaryPanel);


        createTablePanel(primaryPanel,isReadOnly);
    }
    
    public Composite getPrimaryPanel() {
    	return primaryPanel;
    }

    /*
     * Create the properties table panel 
     * @param parent the parent composite
     * @param isReadOnly 'true' if not editable
     */
    private void createTablePanel(Composite parent, boolean isReadOnly) {
        Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        GridDataFactory.fillDefaults().hint(-1, 300).grab(true, false).applyTo(panel);

        // Create Table Viewer
        int tableStyle = SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION;
        this.propertiesViewer = new TableViewerBuilder(panel, tableStyle);

        ColumnViewerToolTipSupport.enableFor(this.propertiesViewer.getTableViewer());
        this.propertiesViewer.setContentProvider(new IStructuredContentProvider() {
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
            	if( inputElement instanceof IPropertySource ) {
            		IPropertySource ips = (IPropertySource)inputElement;
            		
            		return ips.getPropertyDescriptors();
            	}
            	
            	return new Object[0];
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

        Table table = this.propertiesViewer.getTable();
        table.setLayout(new TableLayout());

        final GridData gridData = new GridData(GridData.FILL_BOTH); 
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        table.setLayoutData(gridData);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * 5;
        
        // create columns
        TableViewerColumn column1 = propertiesViewer.createColumn(SWT.LEFT, 80, 400, true);
        configureColumn(column1, 0, "Property                                                                                ", "Property name", true);

        namePropLabelProvider = new PropertyLabelProvider(0);
        column1.setLabelProvider(namePropLabelProvider);


        TableViewerColumn column2 = propertiesViewer.createColumn(SWT.LEFT, 20, 200, true);
        configureColumn(column2, 1, "Value", "Value for this property", true);
        valuePropLabelProvider = new PropertyLabelProvider(1);
        column2.setLabelProvider(valuePropLabelProvider);

        this.propertiesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handlePropertySelected(event);
            }
        });

        this.propertiesViewer.setInput(this.propertySourceProvider);
        updatePropertiesList();
        this.propertiesViewer.refresh();
        
        packTable();
        
        column1.getColumn().setText("Property");
    }
    
    private void configureColumn( TableViewerColumn viewerColumn,
            int columnIndex,
            String headerText,
            String headerToolTip,
            boolean resizable ) {

		TableColumn column = viewerColumn.getColumn();
		column.setText(headerText);
		column.setToolTipText(headerToolTip);
		column.setMoveable(false);
		column.setResizable(resizable);
	}
    
    /*
     * pack the table
     */
    private void packTable() {
        TableColumn[] cols = this.propertiesViewer.getTable().getColumns();
        for(int i=0; i<cols.length; i++) {
            cols[i].pack();
        }
    }
    
//    private boolean valuesSame(String value1, String value2) {
//        if(CoreStringUtil.isEmpty(value1) && CoreStringUtil.isEmpty(value2)) {
//            return true;
//        }
//        if(CoreStringUtil.isEmpty(value1) && !CoreStringUtil.isEmpty(value2)) {
//            return false;
//        }
//        if(CoreStringUtil.isEmpty(value2) && !CoreStringUtil.isEmpty(value1)) {
//            return false;
//        }
//        if(!value1.equalsIgnoreCase(value2)) {
//            return false;
//        }
//        return true;
//    }
    
    /**
     * Get the state of DataSource property changes
     * @return the 'true' if any properties have changed, 'false' if not
     */
//    public boolean hasPropertyChanges() {
//        boolean hasChange = false;
//        for(PropertyItem propItem : this.propertyItemList) {
//            if(propItem.hasChanged()) {
//                hasChange=true;
//                break;
//            }
//        }
//        return hasChange;
//    }
    
    /*
     * Update the PropertyItem list for the currently selected DataSource name - from the TeiidServer
     */
    private void updatePropertiesList( ) {
//        if(this.dataSourceOrDriverName!=null) {
////            if(this.isCreateNew) {
////                this.propertyItemList =  sortPropertyItems(this.dataSourceMgr.getDriverPropertyItems(this.dataSourceOrDriverName));
////            } else {
////                this.propertyItemList =  sortPropertyItems(this.dataSourceMgr.getDataSourcePropertyItems(this.dataSourceOrDriverName));
////            }
//            this.namePropLabelProvider.setPropertyItems(this.propertyItemList);
//            this.valuePropLabelProvider.setPropertyItems(this.propertyItemList);
//        }
    }
    



    /*
     * Handler for selection changed events
     */
    void handlePropertySelected( SelectionChangedEvent event ) {
//        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
//        setPropertyDescriptionText(selection);
//
//        if(isReadOnly) return;
//
//        if (selection.isEmpty()) {
//            if (this.resetButton.isEnabled()) {
//                this.resetButton.setEnabled(false);
//            }
//        } else {
//            PropertyItem prop = (PropertyItem)selection.getFirstElement();
//            boolean enable = prop.hasChanged();
//            if (this.resetButton.isEnabled() != enable) {
//                this.resetButton.setEnabled(enable);
//            }
//        }
    }

    /**
     * Get the current panel Status
     * @return the current Status
     */
    public IStatus getStatus() {
    	IStatus resultStatus = new Status(IStatus.OK, PLUGIN_ID, "OK" /*Messages.dataSourcePropertiesPanelOk*/);

//        for(PropertyItem propObj : this.propertyItemList) {
//            if(isPropertyRequired(propObj,this.propertyItemList) && !propObj.hasValidValue()) {
//        		resultStatus = new Status(IStatus.ERROR, PLUGIN_ID, Messages.dataSourcePropertiesPanel_invalidPropertyMsg);
//            	break;
//            }
//        }
//        
//        if(applyButton.isEnabled()) {
//        	resultStatus = new Status(IStatus.ERROR, PLUGIN_ID, Messages.dataSourcePropertiesPanel_applyPropertyChangesMsg);
//        }
        
        return resultStatus;        
    }
    
    

    
    /**
     * Public access to refresh the contents of this panel based on external changes to the translator override
     * properties
     */
    public void refresh() {
        this.propertiesViewer.setInput(this);
        this.propertiesViewer.refresh();
    }

    /**
     * Label Provider for the Property panel
     */
    class PropertyLabelProvider extends ColumnLabelProvider {
    	int columnNumber = -1;

        public PropertyLabelProvider(int columnNumber) {
        	this.columnNumber = columnNumber;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
        	if( element instanceof IPropertyDescriptor ) {
        		
        		IPropertyDescriptor desc = (IPropertyDescriptor)element;

        		if( columnNumber == 0 ) {
        			if( StringUtilities.isNotEmpty(desc.getDisplayName()) ) {
        				return desc.getDisplayName();
        			}
        		} else if( columnNumber == 1 ) {
        			if( desc instanceof ModelObjectPropertyDescriptor ) {
        				ModelObjectPropertyDescriptor moDesc = (ModelObjectPropertyDescriptor)desc;
        				
	        			String theValue = moDesc.getLabelProvider().getText(pSource.getPropertyValue(moDesc.getId()));
        				return theValue;
//        				ILabelProvider labelProvider = moDesc.getLabelProvider();
//	        			Object objValue = pSource.getEditableValue();
//	        			if( objValue != null ) {
//	        				return labelProvider.getText(objValue);
//	        			}

        			} else {
	        			String theValue = desc.getLabelProvider().getText(pSource.getPropertyValue(desc.getId()));
        				return theValue;
        			}
        		} 
        	}

        	return "";
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
         */
        @Override
        public String getToolTipText( Object element ) {
        	return null;
//            PropertyItem propObj = (PropertyItem)element;
//            if(this.nameColumn) {
//                return propObj.getDescription();
//            }
//            
//            if(propObj.hasValidValue()) {
//                return NLS.bind(Messages.dataSourcePropertiesPanel_validPropertyTooltip, propObj.getDisplayName());
//            }
//            
//            return NLS.bind(Messages.dataSourcePropertiesPanel_invalidPropertyTooltip, propObj.getDisplayName());
        }
    }

    /**
     * SelectionEvent is received from DataSourcePanel
     */

    public void selectionChanged(SelectionChangedEvent event) {
    	EObject eObj = SelectionUtilities.getSelectedEObject(event.getSelection());
    	if( eObj != null ) {
    		pSource = propertySourceProvider.getPropertySource(eObj);
    		propertiesViewer.setInput(pSource);
    		propertiesViewer.refresh();
    	} else {
    		Object obj = SelectionUtilities.getSelectedObject(event.getSelection());
    		pSource = propertySourceProvider.getPropertySource(obj);
    		propertiesViewer.setInput(pSource);
    		propertiesViewer.refresh();
//    		propertiesViewer.setInput(null);
//    		propertiesViewer.getTable().clearAll();
//    		propertiesViewer.refresh();
    	}

        //setDataSourceOrDriverName(dataSourceOrDriverName);
    }
    

}
