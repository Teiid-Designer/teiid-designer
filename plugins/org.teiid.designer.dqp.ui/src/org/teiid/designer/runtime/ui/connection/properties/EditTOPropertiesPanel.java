package org.teiid.designer.runtime.ui.connection.properties;

import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.RESTORE_DEFAULT_VALUE;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.translators.TranslatorOverrideProperty;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.common.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.viewsupport.TranslatorOverridePropertyEditingSupport;
import org.teiid.designer.ui.viewsupport.TranslatorPropertyLabelProvider;
import org.teiid.designer.vdb.ui.VdbUiPlugin;

public class EditTOPropertiesPanel {

    static final String PREFIX = I18nUtil.getPropertyPrefix(EditTOPropertiesPanel.class);
    
    private final TableViewer propertiesViewer;

    /**
     * @param parent
     * @param vdb
     */
    public EditTOPropertiesPanel( Composite parent, final TranslatorOverride override ) { //ITeiidServer teiidServer, String translatorType, Properties overrideProperties) {

       
        Group mainPanel = WidgetFactory.createGroup(parent, 
        		DqpUiPlugin.UTIL.getString("EditTOPropertiesPanel.0_translatorProperties", override.getType().toUpperCase()));
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainPanel);

        { // right-side is an override description and table with the selected translator's properties
            Composite pnlOverrides = new Composite(mainPanel, SWT.FILL);
            pnlOverrides.setLayout(new GridLayout(2, false));
            pnlOverrides.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            this.propertiesViewer = new TableViewer(pnlOverrides, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));

            ColumnViewerToolTipSupport.enableFor(this.propertiesViewer);
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
                    return override.getProperties();
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
            this.propertiesViewer.setComparator(new ViewerComparator() {
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

            int visibleTableRows = 7;
            Table table = this.propertiesViewer.getTable();
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setLayout(new TableLayout());
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            ((GridData)table.getLayoutData()).horizontalSpan = 2;
            ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * visibleTableRows;

            // create columns
            TableViewerColumn column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
            column.getColumn().setText(DqpUiConstants.UTIL.getString(PREFIX + "propertyColumn.text")); //$NON-NLS-1$
            column.setLabelProvider(new TranslatorPropertyLabelProvider(true)); // PropertyLabelProvider(false));
            column.getColumn().pack();
            column.getColumn().setWidth(400);

            column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
            column.getColumn().setText(DqpUiConstants.UTIL.getString(PREFIX + "valueColumn.text")); //$NON-NLS-1$
            column.setLabelProvider(new TranslatorPropertyLabelProvider(false)); // PropertyLabelProvider(false));
            column.setEditingSupport(new TOPropertyEditingSupport(this.propertiesViewer));
            column.getColumn().pack();
            column.getColumn().setWidth(150);
            if( override.getProperties().length == 0 ) {
            	((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * 4;
            }

            //
            // add note below the table
            //
            Composite toolbarPanel = WidgetFactory.createPanel(pnlOverrides, SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 1);
            
        	org.teiid.designer.ui.common.widget.Label noteLabel = 
        			WidgetFactory.createLabel(toolbarPanel, DqpUiConstants.UTIL.getString(PREFIX + "overridePropertiesNoteLabel")); //$NON-NLS-1$
        	noteLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        	GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(noteLabel);
        	noteLabel.setToolTipText(DqpUiConstants.UTIL.getString(PREFIX + "overridePropertiesNoteTooltip"));  //$NON-NLS-1$

        }

        this.propertiesViewer.setInput(this);
    }
    

    private TranslatorOverrideProperty getSelectedProperty() {
        IStructuredSelection selection = (IStructuredSelection)this.propertiesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (TranslatorOverrideProperty)selection.getFirstElement();
    }
    
    void handleRestorePropertyDefaultValue() {
        assert (!this.propertiesViewer.getSelection().isEmpty());

        TranslatorOverrideProperty prop = getSelectedProperty();
        prop.setValue(null);
        // TODO this needs to dirty VDB
        this.propertiesViewer.refresh(prop);
    }
    
    /**
     * Public access to refresh the contents of this panel based on external changes to the translator override
     * properties
     */
    public void refresh() {
        this.propertiesViewer.setInput(this);
        this.propertiesViewer.refresh();
    }
    
    public boolean canFinish() {
    	return true; // TODO: CALCULATE THIS!!!
    }

    class PropertyLabelProvider extends ColumnLabelProvider {

        private final boolean nameColumn;

        public PropertyLabelProvider( boolean nameColumn ) {
            this.nameColumn = nameColumn;
        }

        /**EditTranslatorOverrideDialog
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;
            String overridenValue = property.getOverriddenValue();
            Image image = null;

            if (!this.nameColumn) {
                if (property.getDefinition().isValidValue(overridenValue) == null) {
                    if (property.hasOverridenValue()) {
                        if (!property.isCustom() || !property.getDefinition().getDefaultValue().equals(overridenValue)) {
                            image = VdbUiPlugin.singleton.getImage(RESTORE_DEFAULT_VALUE);
                        }
                    }
                } else {
                    image = UiPlugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
                }
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
            TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;

            if (this.nameColumn) {
                return property.getDefinition().getDisplayName();
            }

            boolean masked = property.getDefinition().isMasked();
            final String maskedValue = "*****"; //$NON-NLS-1$

            // return override value if it exists
            if (property.hasOverridenValue()) {
                return (masked ? maskedValue : property.getOverriddenValue());
            }

            // return default value
            return (masked ? maskedValue : property.getDefinition().getDefaultValue());
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
         */
        @Override
        public String getToolTipText( Object element ) {
            TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;

            if (this.nameColumn) {
                return property.getDefinition().getDescription();
            }

            if (property.hasOverridenValue()) {
                if (!property.isCustom() || !property.getDefinition().getDefaultValue().equals(property.getOverriddenValue())) {
                    return property.getDefinition().isValidValue(property.getOverriddenValue());
                }
            }

            // default value is being used
            return "<USING DEFAULT PROPERTY VALUE>"; //Util.getString(TranslatorOverridesPanel.PREFIX + "usingPropertyDefaultValue"); //$NON-NLS-1$
        }
    }
    
    class TOPropertyEditingSupport extends TranslatorOverridePropertyEditingSupport {

		public TOPropertyEditingSupport(ColumnViewer viewer) {
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
