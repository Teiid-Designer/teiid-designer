/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetSorter;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.modeler.internal.dqp.ui.workspace.RuntimePropertySourceProvider;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 5.0
 */
public class NewConnectorPanel extends Composite implements IChangeNotifier, IPropertyChangeListener {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(NewConnectorPanel.class);

    private final static int FILE_NAME_TEXT_WIDTH = (int)(Display.getCurrent().getBounds().width * .25);

    private static String getString( String theKey ) {
        return DqpUiConstants.UTIL.getStringOrKey(PREFIX + theKey);
    }

    private static String getString( String theKey,
                                     Object obj ) {
        return DqpUiConstants.UTIL.getString(PREFIX + theKey, obj);
    }

    private ListenerList changeListeners;
    private final List<ConnectorType> connectorTypes;
    private Combo typeCombo;
    private String currentName;
    private final Properties currentProperties;
    private ConnectorType currentType;
    private Text bindingNameText;
    private Button btnShowExpertProps;
    private PropertySheetPage propertyPage;
    private RuntimePropertySourceProvider sourceProvider;
    private final ExecutionAdmin admin;

    /**
     * @param parent this panel's container
     * @param admin the server's execution admin (never <code>null</code>)
     * @param type the initial connector type (can be <code>null</code>)
     */
    public NewConnectorPanel( Composite parent,
                              ExecutionAdmin admin,
                              ConnectorType type ) {
        super(parent, SWT.NONE);
        CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$

        this.changeListeners = new ListenerList(ListenerList.IDENTITY);
        this.admin = admin;
        this.connectorTypes = new ArrayList<ConnectorType>(this.admin.getConnectorTypes());
        this.currentType = (this.currentType == null) ? this.connectorTypes.get(0) : type;
        this.currentProperties = new Properties();

        // create UI
        createContents(this);

        // needed to get the property page to update
        connectorTypeChanged();
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void addChangeListener( IChangeListener theListener ) {
        this.changeListeners.add(theListener);
    }

    void connectorTypeChanged() {
        int index = this.typeCombo.getSelectionIndex();
        this.currentType = (index == -1) ? null : this.connectorTypes.get(index);

        // change properties to match new type and let listeners know of the type change
        refreshPropertyPage();
        fireChangeEvent();
    }

    private void createContents( Composite theParent ) {
        GridLayout gridLayout = new GridLayout();
        theParent.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        theParent.setLayoutData(gridData);

        createNameAndTypeGroup(theParent);
        createProperties(theParent);
    }

    private void createNameAndTypeGroup( Composite theParent ) {
        Composite nameGroup = WidgetFactory.createGroup(theParent, getString("bindingName"), SWT.FILL, 1, 3); //$NON-NLS-1$

        // Label schemaNameLabel = new Label(nameGroup, SWT.NONE);
        //schemaNameLabel.setText(getString("name")); //$NON-NLS-1$
        // setGridData(schemaNameLabel, GridData.BEGINNING, false, GridData.CENTER, false);

        this.bindingNameText = WidgetFactory.createTextField(nameGroup, GridData.HORIZONTAL_ALIGN_FILL);
        this.bindingNameText.setEditable(true);

        GridData fileNameTextGridData = new GridData();
        fileNameTextGridData.widthHint = FILE_NAME_TEXT_WIDTH;
        this.bindingNameText.setLayoutData(fileNameTextGridData);
        this.bindingNameText.setText(getString("initialName")); //$NON-NLS-1$
        this.bindingNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                handleBindingNameChanged();
            }
        });

        WidgetFactory.createLabel(this, getString("lblType")); //$NON-NLS-1$
        this.typeCombo = WidgetFactory.createCombo(this, SWT.READ_ONLY, GridData.FILL_HORIZONTAL);

        this.typeCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected( SelectionEvent e ) {
                connectorTypeChanged();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                connectorTypeChanged();
            }
        });

        // load combo
        List<String> typeNames = new ArrayList<String>(this.connectorTypes.size());

        for (ConnectorType type : this.connectorTypes) {
            typeNames.add(type.getName());
        }

        Collections.sort(typeNames);
        WidgetUtil.setComboItems(this.typeCombo, typeNames, null, false, this.currentType.getName());
        this.typeCombo.setVisibleItemCount(Math.min(10, this.connectorTypes.size()));
        // Now we need to re-set the connectorTypes array to be compatible with combo box?

        List<ConnectorType> sortedTypes = new ArrayList<ConnectorType>();
        for (String name : typeNames) {
            for (ConnectorType type : this.connectorTypes) {
                if (type.getName().equals(name)) {
                    sortedTypes.add(type);
                    break;
                }
            }
        }

        this.connectorTypes.clear();
        this.connectorTypes.addAll(sortedTypes);
    }

    private void createProperties( Composite theParent ) {

        Composite propertyGroup = WidgetFactory.createGroup(theParent, getString("lblBindingProperties"), SWT.FILL, 1, 2); //$NON-NLS-1$

        GridLayout gridLayout = new GridLayout();
        propertyGroup.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        propertyGroup.setLayoutData(gridData);

        // toggle button to show/hide expert properties
        this.btnShowExpertProps = new Button(propertyGroup, SWT.CHECK);
        this.btnShowExpertProps.setEnabled(true);
        this.btnShowExpertProps.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        this.btnShowExpertProps.setText(getString("btnShowExpertProps.text")); //$NON-NLS-1$
        this.btnShowExpertProps.setToolTipText(getString("btnShowExpertProps.tooTip")); //$NON-NLS-1$
        this.btnShowExpertProps.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent theE ) {
                handleShowPropertiesSelected();
            }
        });

        this.propertyPage = new PropertySheetPage() {

            @Override
            public void createControl( Composite parent ) {
                GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
                gd.horizontalSpan = 2;
                Composite border = new Composite(parent, SWT.BORDER);
                border.setLayoutData(gd);
                GridLayout gridLayout = new GridLayout();
                gridLayout.marginHeight = 0;
                gridLayout.marginWidth = 0;
                border.setLayout(gridLayout);
                super.createControl(border);

                // override the default sorter
                setSorter(new NoSortingPropertySorter());
            }
        };

        this.propertyPage.createControl(propertyGroup);
        this.propertyPage.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

        this.sourceProvider = new RuntimePropertySourceProvider();

        this.sourceProvider.addPropertyChangeListener(this);
        this.sourceProvider.setEditable(true);
        this.propertyPage.setPropertySourceProvider(this.sourceProvider);
    }

    /**
     * Notifies all registered listeners of a state change.
     * 
     * @since 4.3
     */
    protected void fireChangeEvent() {
        for (Object listener : this.changeListeners.getListeners()) {
            ((IChangeListener)listener).stateChanged(this);
        }
    }

    /**
     * @return the new connector name
     * @since 7.0
     */
    public String getConnectorName() {
        return this.currentName;
    }

    public Properties getConnectorProperties() {
        return this.currentProperties;
    }

    public ConnectorType getConnectorType() {
        return this.currentType;
    }

    public IStatus getStatus() {
        IStatus result = ModelerDqpUtils.isValidBindingName(getConnectorName());

        if (result.getSeverity() != IStatus.ERROR) {
            int severity = IStatus.ERROR;
            String msg = "Message has not been set"; //$NON-NLS-1$

            if (this.admin.getConnector(getConnectorName()) != null) {
                // binding with that name already exists //MyCode : need check in the future
                severity = IStatus.ERROR;
                msg = getString("duplicateNameMsg", getConnectorName()); //$NON-NLS-1$
            } else {
                severity = IStatus.OK;
                msg = getString("nameIsValidMsg"); //$NON-NLS-1$
            }

            result = new Status(severity, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
        }

        return result;
    }

    void handleBindingNameChanged() {
        this.currentName = this.bindingNameText.getText();
        fireChangeEvent(); // alert listeners
    }

    /**
     * Handler for when the button to show/hide advanced/expert properties is clicked.
     * 
     * @since 5.0.2
     */
    void handleShowPropertiesSelected() {
        this.sourceProvider.setShowExpertProperties(this.btnShowExpertProps.getSelection());
        this.propertyPage.refresh();
    }

    private void packPropertiesPage() {
        Tree tree = (Tree)this.propertyPage.getControl();
        TreeColumn[] treeCols = tree.getColumns();

        for (int i = 0; i < treeCols.length; ++i) {
            treeCols[i].pack();
        }
    }

    /**
     * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     * @since 4.3
     */
    public void propertyChange( PropertyChangeEvent theEvent ) {
        this.currentProperties.setProperty(theEvent.getProperty(), (String)theEvent.getNewValue());
        packPropertiesPage();

        // alert listeners something has changed
        fireChangeEvent();
    }

    private void refreshPropertyPage() {
        IStructuredSelection selection = StructuredSelection.EMPTY;

        if (this.currentType != null) {
            selection = new StructuredSelection(this.currentType);
        }

        if (this.propertyPage != null) {
            // notify property page of new selection
            this.propertyPage.selectionChanged(null, selection);

            // pack the property page columns. couldn't find a better way to do this.
            Control c = this.propertyPage.getControl();

            if (c instanceof Tree) {
                TreeColumn[] cols = ((Tree)c).getColumns();

                for (int i = 0; i < cols.length; ++i) {
                    cols[i].pack();
                }
            }
        }
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void removeChangeListener( IChangeListener theListener ) {
        this.changeListeners.remove(theListener);
    }

    /**
     * Attaches the given layout specification to the <code>component</code>.
     * 
     * @param component the component
     * @param horizontalAlignment horizontal alignment
     * @param grabExcessHorizontalSpace grab excess horizontal space
     * @param verticalAlignment vertical alignment
     * @param grabExcessVerticalSpace grab excess vertical space
     */
    private void setGridData( Control component,
                              int horizontalAlignment,
                              boolean grabExcessHorizontalSpace,
                              int verticalAlignment,
                              boolean grabExcessVerticalSpace ) {
        GridData gd = new GridData();
        gd.horizontalAlignment = horizontalAlignment;
        gd.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
        gd.verticalAlignment = verticalAlignment;
        gd.grabExcessVerticalSpace = grabExcessVerticalSpace;
        component.setLayoutData(gd);
    }

    public void setReadonly( boolean theReadonlyFlag ) {
        // defect 19623 - disable editing, not the table:
        this.sourceProvider.setEditable(!theReadonlyFlag);
        // force a re-read:
        this.propertyPage.setPropertySourceProvider(this.sourceProvider);

        // change color to match enabled or disabled color
        int colorCode = (theReadonlyFlag ? SWT.COLOR_WIDGET_BACKGROUND : SWT.COLOR_WHITE);
        Color bkg = UiUtil.getSystemColor(colorCode);
        this.propertyPage.getControl().setBackground(bkg);
    }

    /**
     * This sorter does not doing any sorting. The sorting is done when the descriptors are retrieved. Needed to override the
     * default sorter.
     * 
     * @since 5.5
     */
    class NoSortingPropertySorter extends PropertySheetSorter {
        @Override
        public int compare( IPropertySheetEntry theEntryA,
                            IPropertySheetEntry theEntryB ) {
            return 0;
        }
    }
}
