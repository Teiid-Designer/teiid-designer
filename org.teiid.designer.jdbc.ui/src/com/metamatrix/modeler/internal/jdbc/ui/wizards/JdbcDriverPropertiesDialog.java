/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui.wizards;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.jdbc.ui.InternalModelerJdbcUiPluginConstants;
import com.metamatrix.modeler.internal.jdbc.ui.util.JdbcUiUtil;
import com.metamatrix.modeler.jdbc.JdbcDriverProperty;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;


/** 
 * A dialog to display the import properties of a JDBC Driver
 * @since 4.2
 */
public class JdbcDriverPropertiesDialog extends Dialog {

    //==================================
    // Constants Methods

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcDriverPropertiesDialog.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String NAME_COLUMN = getString("nameColumn"); //$NON-NLS-1$
    private static final String DESCRIPTION_COLUMN = getString("descriptionColumn"); //$NON-NLS-1$
    private static String NO_PROPERTIES = getString("noProperties"); //$NON-NLS-1$
    
    //==================================
    // Static Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    private static String getString(final String id) {
        return InternalModelerJdbcUiPluginConstants.Util.getString(I18N_PREFIX + id);
    }
    
    //==================================
    // Variables
    
    private JdbcDriverProperty[] properties;
    private String message;
    
    /** 
     * @param parent
     * @since 4.2
     */
    public JdbcDriverPropertiesDialog(Shell parent, JdbcSource jdbcSource) {
        super(parent, TITLE);
        try {
            this.properties = JdbcUiUtil.getJdbcManager().getPropertyDescriptions(jdbcSource);
            sortProperties();
            this.message = InternalModelerJdbcUiPluginConstants.Util.getString(I18N_PREFIX + "propertiesMessage", jdbcSource.getDriverName()); //$NON-NLS-1$
        } catch (Exception err) {
            NO_PROPERTIES = getString("jdbcExceptionMessage"); //$NON-NLS-1$
            InternalModelerJdbcUiPluginConstants.Util.log(err);
        }

        jdbcSource.getName();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
		createButton(
		 			parent,
		 			IDialogConstants.OK_ID,
		 			IDialogConstants.OK_LABEL,
		 			true);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        
        if ( properties != null && properties.length > 0 ) {
            
            WidgetFactory.createLabel(container, message).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            
	        TableViewer tableViewer = new TableViewer(container, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
	        tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	        tableViewer.getTable().setHeaderVisible(true);

	        tableViewer.setContentProvider(new JdbcPropertyContentProvider(this.properties));
	        tableViewer.setLabelProvider(new JdbcPropertyLabelProvider());
	        TableColumn nameColumn = new TableColumn(tableViewer.getTable(), 0);
	        nameColumn.setText(NAME_COLUMN);
	        
	        TableColumn descriptionColumn = new TableColumn(tableViewer.getTable(), 1);
	        descriptionColumn.setText(DESCRIPTION_COLUMN);

	        
	        tableViewer.setInput(properties);

	        nameColumn.pack();
	        descriptionColumn.pack();

	        
        } else {
            WidgetFactory.createLabel(container, NO_PROPERTIES).setLayoutData(new GridData(GridData.FILL_BOTH));
        }
        
        return container;
    }
    
    private void sortProperties() {
        if ( properties != null && properties.length > 0 ) {
            HashMap map = new HashMap();
            TreeSet set = new TreeSet();
            for ( int i=0 ; i<properties.length ; ++i ) {
                set.add(properties[i].getName());
                map.put(properties[i].getName(), properties[i]);
            }
            int i=0;
            for ( Iterator iter = set.iterator() ; iter.hasNext() ; ++i ) {
                properties[i] = (JdbcDriverProperty) map.get(iter.next());
            }
        }
    }
    
}

class JdbcPropertyContentProvider implements IStructuredContentProvider {
    
    private JdbcDriverProperty[] properties;
    
    public JdbcPropertyContentProvider(JdbcDriverProperty[] properties) {
        this.properties = properties;
    }
    public void dispose() {}
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    public Object[] getElements(Object inputElement) { return properties; }
}

class JdbcPropertyLabelProvider implements ITableLabelProvider {
    
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }
    public String getColumnText(Object element, int columnIndex) {
        String result = ""; //$NON-NLS-1$
        if (columnIndex == 0 ) {
            result = ((JdbcDriverProperty) element).getName();
        } else if ( columnIndex == 1 ) {
            result = ((JdbcDriverProperty) element).getDescription();
        }
        return result;
    }
    public void addListener(ILabelProviderListener listener) { }
    
    public void dispose() { }
    
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
    
    public void removeListener(ILabelProviderListener listener) { }
}
