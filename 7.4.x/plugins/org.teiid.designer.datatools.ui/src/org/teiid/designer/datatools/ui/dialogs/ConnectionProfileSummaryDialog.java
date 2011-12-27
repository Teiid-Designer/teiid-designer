package org.teiid.designer.datatools.ui.dialogs;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;

public class ConnectionProfileSummaryDialog extends Dialog {
	private static final int WIDTH = 700;
    private static final int HEIGHT = 400;
    
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ConnectionProfileSummaryDialog.class);
    private static final String TITLE = getString("title"); //$NON-NLS-1$
    
    private Properties properties;
    private String modelName;
    private TableViewer propsViewer;
    private TableContentProvider propertiesContentProvider;
    
    private static String getString( final String id ) {
        return DatatoolsUiConstants.UTIL.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
            final Object value ) {
    	return DatatoolsUiConstants.UTIL.getString(I18N_PREFIX + id, value);
    }
    
    /**
     * 
     * @param parent
     * @param modelName
     * @param props
     */
    public ConnectionProfileSummaryDialog( Shell parent, String modelName, Properties props) {
        super(parent, TITLE);
        this.modelName = modelName;
        this.properties = props;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {

        Composite mainPanel = (Composite)super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        mainPanel.setLayout(gridLayout);
        gridLayout.numColumns = 1;

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = WIDTH;
        gd.heightHint = HEIGHT;
        mainPanel.setLayoutData(gd);

        Group messageGroup = WidgetFactory.createGroup(mainPanel, getString("messageGroup.label"), GridData.FILL_BOTH, 1, 1); //$NON-NLS-1$
        
        StyledText msg = new StyledText(messageGroup, SWT.NONE);
        GridData gdt = new GridData(GridData.FILL_BOTH);
        gdt.widthHint = 400;
        //gdt.heightHint = 200;
        msg.setLayoutData(gdt);
        
        msg.setEditable(false);
        msg.setWordWrap(true);
        	//WidgetFactory.createTextField(messageGroup);
        msg.setText(getString("messageGroup.message", modelName)); //$NON-NLS-1$
        
        // CONSTRUCT REST OF PANEL and CONTENTS HERE
        // ===========>>>>
        Group propsGroup = WidgetFactory.createGroup(mainPanel, getString("propsGroup.label"), GridData.FILL_BOTH, 2, 2); //$NON-NLS-1$

        final GridData propertiesGridData = new GridData(GridData.FILL_BOTH);
        propertiesGridData.horizontalSpan = 2;
        propertiesGridData.heightHint = 220;
        propertiesGridData.minimumHeight = 220;
        propertiesGridData.grabExcessVerticalSpace = true;
        propsGroup.setLayoutData(propertiesGridData);

        int tableStyle = SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION;
        propsViewer = new TableViewer(propsGroup, tableStyle);
        Table table = propsViewer.getTable();

        final GridData gridData = new GridData(GridData.FILL_BOTH); 
        gridData.grabExcessHorizontalSpace = true;
        table.setLayoutData(gridData);

        /*** Tree table specific code starts ***/

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableColumn column1 = new TableColumn(table, SWT.NONE);
        column1.setText(getString("properties.name")); //$NON-NLS-1$
        column1.setWidth(200);
        TableColumn column2 = new TableColumn(table, SWT.NONE);
        column2.setText(getString("properties.value")); //$NON-NLS-1$
        column2.setWidth(50);
        table.pack();

        GridData columnData = new GridData(GridData.FILL_BOTH);
        table.setLayoutData(columnData);

        // table.setLayout(layout);

        propertiesContentProvider = new TableContentProvider();
        propsViewer.setContentProvider(new ArrayContentProvider());
        propsViewer.setLabelProvider(propertiesContentProvider);
        
        if (properties != null && !properties.isEmpty()) {
            Collection<StringKeyValuePair> propsColl = new ArrayList<StringKeyValuePair>();
            for (Object key : properties.keySet()) {
                String keyStr = (String)key;
                String value = (String)properties.getProperty((String)key);
                propsColl.add(new StringKeyValuePair(keyStr, value));
            }
            propsViewer.setInput(propsColl);
        }

        return mainPanel;
    }
    
    /**
     * @see org.eclipse.jface.window.Window#create()
     * @since 5.0
     */
    @Override
    public void create() {
        super.create();
        setOkEnabled(true);
    }

    /**
     * Allows setting OK button (i.e. Close) enablement state
     * 
     * @param enabled
     * @since 5.0
     */
    public void setOkEnabled( boolean enabled ) {
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }
    
    class StringKeyValuePair {

        private String key;
        private String value;

        public StringKeyValuePair( String key,
                                   String value ) {
            this.key = key;
            this.value = value;
        }

        /**
         * @return key
         */
        public String getKey() {
            return key;
        }

        /**
         * @return value
         */
        public String getValue() {
            return value;
        }

    }
    
    class TableContentProvider implements ITableLabelProvider {

        public Image getColumnImage( Object theElement,
                                     int theIndex ) {
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            if (theElement instanceof StringKeyValuePair) {
                StringKeyValuePair prop = (StringKeyValuePair)theElement;
                if (theColumnIndex == 0) {
                    return prop.getKey();
                }

                return prop.getValue();
            }

            return StringUtilities.EMPTY_STRING;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override
        public void addListener( ILabelProviderListener listener ) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
         */
        @Override
        public boolean isLabelProperty( Object element,
                                        String property ) {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override
        public void removeListener( ILabelProviderListener listener ) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
         */
        @Override
        public void dispose() {
        }

    }
}
