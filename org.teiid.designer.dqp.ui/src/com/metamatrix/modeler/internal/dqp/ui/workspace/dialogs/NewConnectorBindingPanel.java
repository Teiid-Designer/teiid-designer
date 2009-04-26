/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetSorter;

import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.namedobject.BaseID;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.modeler.internal.dqp.ui.workspace.ConnectorBindingPropertySourceProvider;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 5.0
 */
public class NewConnectorBindingPanel extends Composite
    implements ControlListener, IChangeListener, IChangeNotifier, IPropertyChangeListener {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(NewConnectorBindingPanel.class);

    private final static int FILE_NAME_TEXT_WIDTH = (int)(Display.getCurrent().getBounds().width * .25);

    private static String getString( String theKey ) {
        return DqpUiConstants.UTIL.getStringOrKey(PREFIX + theKey);
    }

    private ListenerList changeListeners;

    Map componentTypes;
    List sortedTypes;
    private Combo typeCombo;
    BaseID currentTypeID;
    ComponentType currentType;

    private IChangeListener configListener;

    private Text bindingNameText;

    private Button btnShowExpertProps;

    private PropertySheetPage propertyPage;

    private ConnectorBindingPropertySourceProvider sourceProvider;

    private ConnectorBinding connectorBinding;

    private ConfigurationManager configManager;

    public NewConnectorBindingPanel( Composite theParent ) throws IllegalStateException {
        super(theParent, SWT.NONE);

        this.changeListeners = new ListenerList(ListenerList.IDENTITY);

        configManager = DqpPlugin.getInstance().getConfigurationManager();

        createContents(this);

        this.typeCombo.select(0);
        
        // When Type changes, New Binding is created because properties may be different
        connectorTypeChanged();

        propertyPage.selectionChanged(null, new StructuredSelection(connectorBinding));
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void addChangeListener( IChangeListener theListener ) {
        this.changeListeners.add(theListener);
    }

    /**
     * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
     * @since 4.3
     */
    public void controlMoved( ControlEvent theEvent ) {
    }

    /**
     * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
     * @since 4.3
     */
    public void controlResized( ControlEvent theEvent ) {
    }

    private void createContents( Composite theParent ) {
        GridLayout gridLayout = new GridLayout();
        theParent.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        theParent.setLayoutData(gridData);

        createNameAndTypeGroup(theParent);
        createProperties(theParent);

        updateState(false);

    }

    private void createNameAndTypeGroup( Composite theParent ) {
        Composite nameGroup = WidgetFactory.createGroup(theParent, getString("bindingName"), SWT.FILL, 1, 3); //$NON-NLS-1$

        Label schemaNameLabel = new Label(nameGroup, SWT.NONE);
        schemaNameLabel.setText(getString("name")); //$NON-NLS-1$
        setGridData(schemaNameLabel, GridData.BEGINNING, false, GridData.CENTER, false);

        bindingNameText = WidgetFactory.createTextField(nameGroup, GridData.HORIZONTAL_ALIGN_FILL);
        bindingNameText.setEditable(true);

        // Line Below will maintain White background, if desired.

        GridData fileNameTextGridData = new GridData();
        fileNameTextGridData.widthHint = FILE_NAME_TEXT_WIDTH;
        bindingNameText.setLayoutData(fileNameTextGridData);
        String startName = getString("initialName"); //$NON-NLS-1$
        if (connectorBinding != null) {
            startName = connectorBinding.getName();
        }
        bindingNameText.setText(startName);
        this.bindingNameText.addKeyListener(new KeyListener() {

            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
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
        handleConfigurationChanged();
        this.typeCombo.setVisibleItemCount(this.sortedTypes.size());
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

    /**
     * Handler for when the button to show/hide advanced/expert properties is clicked.
     * 
     * @since 5.0.2
     */
    void handleShowPropertiesSelected() {
        this.sourceProvider.setShowExpertProperties(this.btnShowExpertProps.getSelection());
        this.propertyPage.refresh();
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

        sourceProvider = new ConnectorBindingPropertySourceProvider();

        sourceProvider.addPropertyChangeListener(this);
        sourceProvider.setEditable(true);
        this.propertyPage.setPropertySourceProvider(sourceProvider);
    }

    private void packPropertiesPage() {
        Tree tree = (Tree)this.propertyPage.getControl();
        TreeColumn[] treeCols = tree.getColumns();

        for (int i = 0; i < treeCols.length; ++i) {
            treeCols[i].pack();
        }
    }

    void handleBindingNameChanged() {
        // need to refresh properties panel if binding created after name change
        refreshPropertyPage();

        // // alert listeners
        fireChangeEvent();
    }

    public ConnectorBinding getConnectorBinding() {
        return this.connectorBinding;
    }
    
    private void createConnectorBinding() {
        // bindings must have a type
        if (this.currentType != null) {
            try {
            	this.connectorBinding = configManager.createConnectorBinding(this.currentType, getNewBindingName(), false);
	        } catch (Exception theException) {
	            DqpUiConstants.UTIL.log(theException);
	            theException.printStackTrace();
	            MessageDialog.openError(getShell(), getString("errorDialog.creatingConnector.title"), //$NON-NLS-1$
	                                    getString("errorDialog.creatingConnector.msg")); //$NON-NLS-1$
	        }
        }
    }

    private String getNewBindingName() {
        // System.out.println(" Binding Name = " + bindingNameText.getText());
        return bindingNameText.getText();
    }

    void connectorTypeChanged() {
        int index = this.typeCombo.getSelectionIndex();

        if (index != -1) {
            this.currentType = (ComponentType)this.componentTypes.get(sortedTypes.get(index));

            if ((this.currentTypeID == null) || !(this.currentTypeID == this.currentType.getID())) {
                this.currentTypeID = this.currentType.getID();
            }
        }

        // If Component Type is changed, we need to create a new binding
        createConnectorBinding();

        refreshPropertyPage();

        fireChangeEvent();
    }

    private void handleConfigurationChanged() {
        if (this.typeCombo != null && !this.typeCombo.isDisposed()) {
            loadConnectorTypes();

            final Combo combo = this.typeCombo;
            final String selection = ((this.currentType == null) ? "" : this.currentType.getName()); //$NON-NLS-1$

            getDisplay().syncExec(new Runnable() {
                public void run() {
                    if (!combo.isDisposed()) {
                        WidgetUtil.setComboItems(combo, NewConnectorBindingPanel.this.sortedTypes, null, false, selection);
                        ComponentType type = (ComponentType)NewConnectorBindingPanel.this.componentTypes.get(selection);
                        if (type != null) {
                            NewConnectorBindingPanel.this.currentType = type;
                            NewConnectorBindingPanel.this.currentTypeID = type.getID();
                        }

                        connectorTypeChanged();
                    }
                }
            });
        }
    }

    private void loadConnectorTypes() {
        this.componentTypes = ModelerDqpUtils.getConnectorTypes();
        this.sortedTypes = new ArrayList(new TreeSet(this.componentTypes.keySet()));

        // see if currentType is in new list of types
        if (this.currentType != null) {
            boolean foundIt = false;

            for (int size = this.sortedTypes.size(), i = 0; i < size; ++i) {
                String name = (String)this.sortedTypes.get(i);

                if (this.currentType.getName().equals(name)) {
                    foundIt = true;
                    break;
                }
            }

            if (!foundIt) {
                this.currentType = null;
            }
        }
    }

    public IStatus getStatus() {
        IStatus result = ModelerDqpUtils.isValidBindingName(getNewBindingName());

        if (result.getSeverity() != IStatus.ERROR) {
            int severity = IStatus.ERROR;
            String msg = "Message has not been set"; //$NON-NLS-1$

            if (!ModelerDqpUtils.isUniqueBindingName(getNewBindingName())) {
                // binding with that name already exists //MyCode : need check in the future
                severity = IStatus.ERROR;
                msg = DqpUiConstants.UTIL.getString("duplicateNameMsg", getNewBindingName()); //$NON-NLS-1$
            } else {
                severity = IStatus.OK;
                msg = DqpUiConstants.UTIL.getString("nameIsValidMsg"); //$NON-NLS-1$
            }

            result = new Status(severity, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
        }

        return result;
    }

    /**
     * Notifies all registered listeners of a state change.
     * 
     * @since 4.3
     */
    protected void fireChangeEvent() {
        Object[] listeners = this.changeListeners.getListeners();

        for (int i = 0; i < listeners.length; ++i) {
            ((IChangeListener)listeners[i]).stateChanged(this);
        }
    }

    private void refreshPropertyPage() {
        IStructuredSelection selection = StructuredSelection.EMPTY;

        if (this.connectorBinding != null) {
            selection = new StructuredSelection(this.connectorBinding);
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
     * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     * @since 4.3
     */
    public void propertyChange( PropertyChangeEvent theEvent ) {

        packPropertiesPage();

        // alert listeners something has changed
        Object[] changeListeners = this.changeListeners.getListeners();

        for (int i = 0; i < changeListeners.length; ++i) {
            ((IChangeListener)changeListeners[i]).stateChanged(this);
        }
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void removeChangeListener( IChangeListener theListener ) {
        this.changeListeners.remove(theListener);
    }

    public void save() {
    	// Copy the connector binding so we get the new name
    	try {
			DqpPlugin.getInstance().getConfigurationManager().createConnectorBinding(this.connectorBinding, getNewBindingName());
		} catch (Exception theException) {
			DqpUiConstants.UTIL.log(theException);
           
            MessageDialog.openError(getShell(), getString("errorDialog.creatingConnector.title"), //$NON-NLS-1$
                                    getString("errorDialog.creatingConnector.msg")); //$NON-NLS-1$
		}
    }

    /**
     * @see org.eclipse.swt.widgets.Composite#setFocus()
     * @since 4.3
     */
    @Override
    public boolean setFocus() {
        boolean result = super.setFocus();

        updateState(true);

        return result;
    }

    public void setReadonly( boolean theReadonlyFlag ) {
        // defect 19623 - disable editing, not the table:
        sourceProvider.setEditable(!theReadonlyFlag);
        // force a re-read:
        propertyPage.setPropertySourceProvider(sourceProvider);

        // change color to match enabled or disabled color
        int colorCode = (theReadonlyFlag ? SWT.COLOR_WIDGET_BACKGROUND : SWT.COLOR_WHITE);
        Color bkg = UiUtil.getSystemColor(colorCode);
        propertyPage.getControl().setBackground(bkg);
    }

    /**
     * The normal dispose() method was not getting called when the bindings editor was closed. Needed a way to remove listeners.
     * @throws Exception 
     * 
     * @since 5.5
     */
    public void internalDispose() {

        
        DqpPlugin.getInstance().getConfigurationManager().removeChangeListener(this.configListener);
    }
    
    /**
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     * @since 5.5
     */
    public void stateChanged( IChangeNotifier theSource ) {
        if (!isDisposed()) {
            updateState(true);
        }
    }

    private void updateState( boolean theUpdateDefnFlag ) {

    }

    public int getComponentTypeComboIndex( ComponentTypeID typeID ) {
        String[] items = typeCombo.getItems();

        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(typeID.getName())) {
                return i;
            }
        }

        return -1;
    }

    public void setConnectorType( ComponentTypeID typeID ) {
        int typeIndex = getComponentTypeComboIndex(typeID);
        if (typeIndex > -1) {
            typeCombo.select(typeIndex);
            connectorTypeChanged(); // needed to get the property page to update
        }
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
