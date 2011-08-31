/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertyDescriptor;
import com.metamatrix.modeler.internal.ui.properties.udp.ExtensionPropertyDescriptor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.table.TableViewerSorter;

/**
 * ModelObjectTableModel
 */
public class ModelObjectTableModel implements UiConstants, EObjectPropertiesOrderPreferencesListener, DisposeListener {

    private ArrayList instanceList;
    private ArrayList rowList;
    private HashMap rowMap = new HashMap();
    private ArrayList propertyList;
    private HashMap propertyIdMap;
    private Map propIdDescriptorMap; // key=property ID, value=IPropertyDescriptor
    private IPropertySourceProvider propertySourceProvider;
    private IPropertyDescriptor[] properties = new IPropertyDescriptor[0];
    private TableViewer tableViewer;
    private boolean columnsBuilt = false;
    private boolean supportsDescription = true;
    private String eObject;
    private ArrayList tableColumns;

    /**
     * Obtains localized display text for the specified property.
     * @param theProperty the property whose text is being requested
     * @return the localized property text
     */
    public static String getPropertyLabel(String theProperty) {
        if (theProperty.equals(DESCRIPTION_KEY)) {
            return DESCRIPTION_KEY;
        } else if (theProperty.equals(LOCATION_KEY)) {
            return LOCATION_KEY;
        } else {
            return theProperty;
        }
    }

    public ModelObjectTableModel(String eObject, ArrayList instanceList,  boolean supportsDescription) {
        UiPlugin.getDefault().getEObjectPropertiesOrderPreferences().addEObjectPropertiesOrderPreferencesListener(this);
        this.eObject = eObject;
        this.instanceList = instanceList;
        this.supportsDescription = supportsDescription;
        refreshProperties();
        buildPropertyIdMap();
    }

    public boolean canView() {
        // DON'T call buildColumns();
        return properties.length > 0;
    }

    public Object getPropertyId(String property) {
        // DON'T call buildColumns();
        return propertyIdMap.get(property);
    }

    public Object getPropertyIdAtIndex(int index) {
        // DON'T call buildColumns();
        return propertyIdMap.get(propertyList.get(index));
    }
    
    private void buildRowList() {
        // DON'T call buildColumns();
        rowList = new ArrayList(instanceList.size());
        for ( int i=0 ; i<instanceList.size() ; ++i ) {
            ModelRowElement row = new ModelRowElement((EObject) instanceList.get(i), this);
            rowList.add(row);
            rowMap.put(instanceList.get(i), row);
        }
    }

    private void buildPropertyIdMap() {
        // DON'T call buildColumns();
        propertyList = new ArrayList(properties.length); // add space for location & description
        propertyIdMap = new HashMap();
        propIdDescriptorMap = new HashMap();
        
        propertyIdMap.put(LOCATION_KEY, LOCATION_KEY);
        propertyList.add(LOCATION_KEY);
            
        for ( int size=properties.length, i=0 ; i<size ; ++i ) {
            propertyIdMap.put(properties[i].getDisplayName(), properties[i].getId());
            propertyList.add(properties[i].getDisplayName());
            propIdDescriptorMap.put(properties[i].getId(), properties[i]);
        }
        if (supportsDescription) {
            propertyIdMap.put(DESCRIPTION_KEY, DESCRIPTION_KEY);
        	propertyList.add(DESCRIPTION_KEY);
        }
        
        //Sort Property List Based on Preferences
        ArrayList sortedColumns = UiPlugin.getDefault().getEObjectPropertiesOrderPreferences().getOrderedPropertyList(this.eObject);
        ArrayList newList = new ArrayList();
        ArrayList hiddenList = new ArrayList();
        for (int i = 0; i < sortedColumns.size(); i++) {
            PropertyOrder columnOrder = (PropertyOrder) sortedColumns.get(i);
            if (propertyList.contains(columnOrder.getName())) {
                if (columnOrder.isVisible()) {
                    newList.add(columnOrder.getName());
                } else {
                    hiddenList.add(columnOrder.getName());
                }
            }
        }
        for (int i = 0; i < propertyList.size(); i++) {
            if (!newList.contains(propertyList.get(i)) && !hiddenList.contains(propertyList.get(i))) {
                newList.add(propertyList.get(i));
            }
        }
        //Always Create One Column, Should Never Happen
        if (newList.size() == 0) {
             newList.add(propertyList.get(0));
        }
        propertyList = newList;
	}
    
    public void refreshProperties() {
        if (instanceList.size() > 0) {
            IPropertySource propertySource = getPropertySource((EObject)instanceList.get(0));
            properties = propertySource.getPropertyDescriptors();
            for (int i = 0; i < properties.length; ++i) {
                if (properties[i] instanceof ModelObjectPropertyDescriptor) {
                    ((ModelObjectPropertyDescriptor)properties[i]).setShowReadOnlyDialog(false);
                    ((ModelObjectPropertyDescriptor)properties[i]).setLazyLoadValues(true);
                } else if (properties[i] instanceof ExtensionPropertyDescriptor) {
                    ((ExtensionPropertyDescriptor)properties[i]).setShowReadOnlyDialog(false);
                }
            }
        }
    }

    public boolean isLocationColumn(Object object) {
        return LOCATION_KEY.equals(object);
    }
    
    public boolean isDescriptionColumn(Object object) {
        return DESCRIPTION_KEY.equals(object);
    }

    /**
     * Gets the <code>IPropertyDescriptor</code> for the given property identifier.
     * @param thePropertyId the identifier of the descriptor being requested
     * @return the descriptor or <code>null</code> if not found
     */
    public IPropertyDescriptor getPropertyDescriptor(Object thePropertyId) {
        return (IPropertyDescriptor)propIdDescriptorMap.get(thePropertyId);
    }
    
    public void setTableViewer(TableViewer tableViewer) {
        // DON'T call buildColumns();
        if ( tableViewer == null || tableViewer.getTable().isDisposed() ) {
            return;
        }

        this.tableViewer = tableViewer;
        ((Table) this.tableViewer.getControl()).addDisposeListener(this);
    }
    
    /**
     * Builds the table columns. This should be done lazily only when the table is first shown.
     * Calling this method more than once nothing.
     *
     */
    public void buildColumns() {
        if (!columnsBuilt && (tableViewer != null)) {
            
//swjTODO: instrument the buildColumns method and find out why it is being called so often
//    and where the large model performance bottlenecks are
//
//            final IRunnableWithProgress op = new IRunnableWithProgress() {
//                public void run(final IProgressMonitor monitor) throws InterruptedException, InvocationTargetException {
//                    monitor.beginTask("Building Table Columns...", 100); //Util.getString("ModelObjectTableModel.buildColumns"), 100);  //$NON-NLS-1$
                    buildColumns(tableViewer.getTable());
//                }
//            };
//        
//            try {
//                final ProgressMonitorDialog dlg = new ProgressMonitorDialog(tableViewer.getControl().getShell());
//                dlg.run(false, false, op);
//            } catch (final InterruptedException ignored) {
//            } catch (final Exception err) {
//            }

            // need to hook up sorter now that columns are built
            TableViewerSorter sorter = (TableViewerSorter)tableViewer.getSorter();
            
            if (sorter != null) {
                sorter.setSortListener();
            } 
        }
    }

    private void buildColumns(Table table) {
        if (!columnsBuilt) {
            columnsBuilt = true;
            if (this.tableColumns != null  && this.tableColumns.size() > 0) {
                for (int i = this.tableColumns.size() - 1; i > -1; i--) {
                    TableColumn column = (TableColumn) this.tableColumns.get(i);
                    column.dispose();
                }
            }
            this.tableColumns = new ArrayList();
            if ( propertyList.size() > 0 ) {
                buildRowList();

                String[] columnProperties = new String[propertyList.size()];
    
                for ( int i=0 ; i< propertyList.size(); ++i ) {
                    TableColumn column = new TableColumn(table, SWT.NONE);
                    this.tableColumns.add(column);
                    String columnHeader = (String)propertyList.get(i);
                    columnProperties[i] = columnHeader;
                    column.setText(columnHeader); 
                    //swjTODO: set column data on this instead of hardcoding 80 pixels
                    column.setWidth(80);
                }            
                tableViewer.setColumnProperties(columnProperties);
                rebuildColumnEditors(table);
            }

            boolean startedTxn = ModelerCore.startTxn(false, false, null, null);
            try {
                tableViewer.setInput(this);
            } finally {
                if ( startedTxn ) {
                    ModelerCore.commitTxn();
                }
            }
        }
    }
    
    public CellEditor getCellEditor(int theColumn) {
        buildColumns();
        return tableViewer.getCellEditors()[theColumn];
    }
    
    public Object[] getElements(){
        buildColumns();
        return rowList.toArray();
    }

    public IPropertySource getPropertySource(EObject modelObject) {
//        buildColumns();
        if ( propertySourceProvider == null ) {
            propertySourceProvider = ModelUtilities.getPropertySourceProvider();
        }
        return propertySourceProvider.getPropertySource(modelObject);
    }
    
    public ModelRowElement getRowElementForInstance(EObject instance) {
        buildColumns();
        return (ModelRowElement) rowMap.get(instance);
    }
    
    public void addRows(Collection eObjects) {
        buildColumns();
        if ( tableViewer == null || tableViewer.getTable().isDisposed() ) {
            return;
        }
        
        for ( Iterator iter = eObjects.iterator() ; iter.hasNext() ; ) {
            EObject eObject = (EObject) iter.next();
            instanceList.add(eObject);
            ModelRowElement row = new ModelRowElement(eObject, this);
            rowList.add(row);
            rowMap.put(eObject, row);
            tableViewer.add(row);
        }
    }
    
    public void removeRows(Collection eObjects) {
        buildColumns();
        if ( tableViewer == null || tableViewer.getTable().isDisposed() ) {
            return;
        }

        for ( Iterator iter = eObjects.iterator() ; iter.hasNext() ; ) {
            EObject eObject = (EObject) iter.next();
            instanceList.remove(eObject);
            ModelRowElement row = (ModelRowElement) rowMap.get(eObject);
            if (row != null) {
                rowList.remove(row);
                rowMap.remove(eObject);
                tableViewer.remove(row);
            } // endif
        }
    }
    
    public void setValueAt(Object theNewValue,
                           int theRow,
                           int theColumn) {
        buildColumns();
        CellEditor editor = getCellEditor(theColumn);

        // jh Defect 19246: added this nullcheck
        if ( editor != null ) {
            ICellEditorValidator validator = editor.getValidator();
            
            if ((validator == null) || (validator.isValid(theNewValue) == null)) {
                ModelRowElement rowElement = (ModelRowElement)rowList.get(theRow);
                Object propID = getPropertyIdAtIndex(theColumn);
                if(propID instanceof String) {
                	rowElement.setValue((String)propID, theNewValue);
                } else if(propID instanceof ExtensionPropertyDescriptor) {
                	rowElement.setValue((ExtensionPropertyDescriptor)propID, theNewValue);
                }
                tableViewer.refresh(rowElement);
            }
        }
     }
     
    
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.table.ModelTableColumnPreferencesListener#columnsChanged()
     */
    public void propertiesChanged(List eObjects) {
        if (eObjects.contains(this.eObject)) {
            this.columnsBuilt = false;
            buildPropertyIdMap();
            buildColumns();
        }
    }
    
    /** Rebuild the columns.
      * This can be useful if something large has changed, such
      *  as the backing file's read-only state.
      */
    public void rebuildColumnEditors(Table table) {
        int size = propertyList.size();
        if ( size > 0 ) {
            CellEditor[] cellEditors = new CellEditor[size];

            for ( int i=0 ; i< size; ++i ) {
                if (isLocationColumn(propertyList.get(i))) {
                    cellEditors[i] = new TextCellEditor(table);
                } else if (isDescriptionColumn(propertyList.get(i))) {
                    cellEditors[i] = new TextCellEditor(table);
                } else {
                    cellEditors[i] = ((IPropertyDescriptor) propIdDescriptorMap.get(propertyIdMap.get(propertyList.get(i)))).createPropertyEditor(table);
                }
            }            
            tableViewer.setCellEditors(cellEditors);
        }
    }

    public void widgetDisposed(DisposeEvent e) {
        UiPlugin.getDefault().getEObjectPropertiesOrderPreferences().removeEObjectPropertiesOrderPreferencesListener(this);
    }
}
