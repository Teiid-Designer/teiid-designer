/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension.ui.actions.dialogs;

import static com.metamatrix.modeler.internal.ui.PluginConstants.Images.EDIT_EXTENSION_PROPERTIES_ICON;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
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
import org.teiid.designer.extension.properties.ExtendedModelObject;
import org.teiid.designer.extension.properties.ModelObjectExtendedProperty;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.Dialog;

public class EditExtensionPropertiesDialog  extends Dialog {

    private ExtendedModelObject extendedModelObject;
    private ExtensionPropertyEditingSupport editingSupport;
    private String objectName;
    private TableViewer propsViewer;
    
    /**
     * 
     * @param parent
     * @param modelName
     * @param props
     */
    public EditExtensionPropertiesDialog( Shell parent, ExtendedModelObject extModelObject) {
        super(parent, NLS.bind(Messages.EditExtensionPropertiesDialog_defaultTitle, null));
        this.objectName = extModelObject.getName();
        this.extendedModelObject = extModelObject;
        this.setTitle(NLS.bind(Messages.EditExtensionPropertiesDialog_title, extModelObject.getHandlerName()));
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
        gd.widthHint = 700;
        gd.heightHint = 400;
        mainPanel.setLayoutData(gd);

        Group messageGroup = WidgetFactory.createGroup(mainPanel, NLS.bind(Messages.EditExtensionPropertiesDialog_messageGroupLabel, this.objectName) , GridData.FILL_BOTH, 1, 1);
        
        StyledText msg = new StyledText(messageGroup, SWT.NONE);
        GridData gdt = new GridData(GridData.FILL_BOTH);
        gdt.widthHint = 300;
        gdt.heightHint = 80;
        msg.setLayoutData(gdt);
        
        msg.setEditable(false);
        msg.setWordWrap(true);
        	//WidgetFactory.createTextField(messageGroup);
        msg.setText(NLS.bind(Messages.EditExtensionPropertiesDialog_messagelabel, this.extendedModelObject.getHandlerName(), this.extendedModelObject.getName()));
        
        // CONSTRUCT REST OF PANEL and CONTENTS HERE
        // ===========>>>>
        Group propsGroup = WidgetFactory.createGroup(mainPanel, NLS.bind(Messages.EditExtensionPropertiesDialog_propertiesGroupLabel, null), GridData.FILL_BOTH, 1, 1);

        final GridData propertiesGridData = new GridData(GridData.FILL_BOTH);
        propertiesGridData.horizontalSpan = 1;
        propertiesGridData.heightHint = 300;
        propertiesGridData.minimumHeight = 220;
        propertiesGridData.grabExcessVerticalSpace = true;
        propsGroup.setLayoutData(propertiesGridData);

        createPropertiesViewer(propsGroup);

        return mainPanel;
    }
    
    private void createPropertiesViewer(Composite parent) {
    	this.propsViewer = new TableViewer(parent, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
        ColumnViewerToolTipSupport.enableFor(this.propsViewer);
        this.propsViewer.setContentProvider(new IStructuredContentProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            @Override
            public void dispose() {
                // nothing to do
            }

            public Object[] getElements( Object inputElement ) {
                return extendedModelObject.getProperties().toArray();
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
        this.propsViewer.setComparator(new ViewerComparator() {
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
            	ModelObjectExtendedProperty prop1 = (ModelObjectExtendedProperty)e1;
            	ModelObjectExtendedProperty prop2 = (ModelObjectExtendedProperty)e2;

                return super.compare(viewer, prop1.getDefinition().getDisplayName(), prop2.getDefinition().getDisplayName());
            }
        });

        Table table = this.propsViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData)table.getLayoutData()).horizontalSpan = 2;

        // create columns
        TableViewerColumn column = new TableViewerColumn(this.propsViewer, SWT.LEFT);
        column.getColumn().setText("Property"); //Util.getString(PREFIX + "propertyColumn.text")); //$NON-NLS-1$
        column.setLabelProvider(new PropertyLabelProvider(true));
        column.getColumn().pack();

        column = new TableViewerColumn(this.propsViewer, SWT.LEFT);
        column.getColumn().setText("Value"); //Util.getString(PREFIX + "valueColumn.text")); //$NON-NLS-1$
        column.setLabelProvider(new PropertyLabelProvider(false));
        editingSupport = new ExtensionPropertyEditingSupport(this.propsViewer, this.extendedModelObject.getResource());
        column.setEditingSupport(editingSupport);
        column.getColumn().pack();

        this.propsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
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
        this.propsViewer.setInput(this.extendedModelObject);
        WidgetUtil.pack(this.propsViewer);
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
    
    public boolean isChanged() {
    	return editingSupport.isChanged();
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
    
    void handlePropertySelected( SelectionChangedEvent event ) {
        // NOOP
    }
    
    class PropertyLabelProvider extends ColumnLabelProvider {

        private final boolean nameColumn;

        public PropertyLabelProvider( boolean nameColumn ) {
            this.nameColumn = nameColumn;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            ModelObjectExtendedProperty property = (ModelObjectExtendedProperty)element;
            Image image = null;

            if (!this.nameColumn) {
                if (property.getValue() != null) {
                    image = UiPlugin.getDefault().getImage(EDIT_EXTENSION_PROPERTIES_ICON);
                }
//                else {
//                    image = UiPlugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
//                }
            }

            return image;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
        	ModelObjectExtendedProperty property = (ModelObjectExtendedProperty)element;

            if (this.nameColumn) {
                return property.getDefinition().getDisplayName();
            }

            if( property.getValue() != null ) {
            	return property.getValue();
            }
            // return default value
            
            return property.getDefinition().getDefaultValue();
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
         */
        @Override
        public String getToolTipText( Object element ) {
            ModelObjectExtendedProperty property = (ModelObjectExtendedProperty)element;

            if (this.nameColumn) {
                return property.getDefinition().getDescription();
            }
            
            if( property.getValue() != null ) {
            	String tempValue = property.getValue();
            	if( tempValue.length() > 25 ) {
            		tempValue = tempValue.substring(0, 25) + "..."; //$NON-NLS-1$
            	}
            	return NLS.bind(Messages.EditExtensionPropertiesDialog_valueTooltip, property.getDefinition().getDisplayName(), tempValue);
            }

            // default value is being used
            return NLS.bind(Messages.EditExtensionPropertiesDialog_defaultTooltip, property.getDefinition().getDisplayName(), property.getDefinition().getDefaultValue());
        }
    }

}