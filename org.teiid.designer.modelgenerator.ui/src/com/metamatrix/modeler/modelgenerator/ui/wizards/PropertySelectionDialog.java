/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * PropertySelectionDialog This dialog is used by the CustomProperties table in the UML-Relational ModelGenerator wizard. It is
 * used to select an existing relational property for a UML Property, or enter a user-defined property name.
 */
public class PropertySelectionDialog extends ListDialog implements ModelGeneratorUiConstants {

    private static final String SELECT_RELATIONAL_RADIO_TEXT = Util.getString("PropertySelectionDialog.selectRelationalRadio.text"); //$NON-NLS-1$
    private static final String SELECT_RELATIONAL_RADIO_TIP = Util.getString("PropertySelectionDialog.selectRelationalRadio.tip"); //$NON-NLS-1$
    private static final String SELECT_USERDEFINED_RADIO_TEXT = Util.getString("PropertySelectionDialog.selectUserDefinedRadio.text"); //$NON-NLS-1$
    private static final String SELECT_USERDEFINED_RADIO_TIP = Util.getString("PropertySelectionDialog.selectUserDefinedRadio.tip"); //$NON-NLS-1$

    private static final String PROPERTY_STR = "Property"; //$NON-NLS-1$
    private static final String CLASS_STR = "Classifier"; //$NON-NLS-1$
    private Button selectRelationalRadio;
    private Button selectUserDefinedRadio;
    private Text userDefinedTextField;
    private boolean selectRelationalSelected = true;
    private String userDefinedPropText;

    /**
     * Construct an instance of PropertySelectionDialog.
     * 
     * @param parent the Shell for this dialog
     */
    public PropertySelectionDialog( final Shell parent ) {
        this(parent, null);
        createDialogArea(parent);
    }

    /**
     * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setFont(parent.getFont());

        // RadioButton for selection of Relational Property
        this.selectRelationalRadio = WidgetFactory.createRadioButton(composite, SELECT_RELATIONAL_RADIO_TEXT, true);
        this.selectRelationalRadio.setToolTipText(SELECT_RELATIONAL_RADIO_TIP);

        // Create the standard area for ListDialog - the table selector
        super.createDialogArea(composite);

        // RadioButton for selection of user-defined text entry
        this.selectUserDefinedRadio = WidgetFactory.createRadioButton(composite, SELECT_USERDEFINED_RADIO_TEXT, false);
        this.selectUserDefinedRadio.setToolTipText(SELECT_USERDEFINED_RADIO_TIP);

        // TextField for user-defined text entry
        this.userDefinedTextField = WidgetFactory.createTextField(composite);

        // Set the initial selections and widget states
        List selectedElems = getInitialElementSelections();

        // Initial selections - Set selections and widget states
        if (!selectedElems.isEmpty()) {
            Object selectedElem = selectedElems.get(0);
            // non-null selection
            if (selectedElem != null) {
                // Initial selection is a String
                if (selectedElem instanceof String) {
                    // Set radioButton selection
                    this.selectUserDefinedRadio.setSelection(true);
                    this.selectRelationalRadio.setSelection(false);
                    // init textField text
                    this.userDefinedTextField.setText((String)selectedElem);
                    // set enabled states
                    getTableViewer().getTable().setEnabled(false);
                    this.userDefinedTextField.setEnabled(true);
                    // Initial selection is a PropertyDescriptor
                } else if (selectedElem instanceof IPropertyDescriptor) {
                    // Set radioButton selection
                    this.selectRelationalRadio.setSelection(true);
                    // enable the table
                    getTableViewer().getTable().setEnabled(true);
                    // Id of the selected property
                    Object selectedID = ((IPropertyDescriptor)selectedElem).getId();
                    // Iterate table and look for the matching property
                    TableItem[] items = getTableViewer().getTable().getItems();
                    for (int i = 0; i < items.length; i++) {
                        TableItem item = items[i];
                        Object data = item.getData();
                        if (data instanceof IPropertyDescriptor) {
                            Object currentID = ((IPropertyDescriptor)data).getId();
                            // if match found, select the table row and break
                            if (currentID.equals(selectedID)) {
                                getTableViewer().getTable().select(i);
                                break;
                            }
                        }
                    }
                    // text area is disabled
                    this.userDefinedTextField.setEnabled(false);
                }
                // null selection - set property table as selected
            } else {
                getTableViewer().getTable().setEnabled(true);
                this.userDefinedTextField.setEnabled(false);
            }
            // No initial selections - just set widget states
        } else {
            getTableViewer().getTable().setEnabled(true);
            this.userDefinedTextField.setEnabled(false);
        }

        // Listener for changes in radio button selection
        this.selectRelationalRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                handleSelectRelationalRadioSelected();
            }
        });

        // Listener for changes in textField
        this.userDefinedTextField.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleUserDefinedTextChange();
            }
        });

        return composite;
    }

    /**
     * Construct an instance of PropertySelectionDialog.
     * 
     * @param parent the Shell for this dialog
     * @param objectForType an EObject that should be displayed in the message for setting the type on or <code>null</code>
     */
    public PropertySelectionDialog( final Shell parent,
                                    final PropBinding propBinding ) {
        super(parent);
        setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements( Object inputElement ) {
                Object[] result = getProperties(propBinding);
                return result;
            }

            public void dispose() {
            }

            public void inputChanged( Viewer v,
                                      Object o,
                                      Object o2 ) {
            }
        });
        setLabelProvider(new LabelProvider() {

            @Override
            public String getText( final Object node ) {
                String text = null;
                if (node instanceof IPropertyDescriptor) {
                    text = ((IPropertyDescriptor)node).getDisplayName();
                }
                return text;
            }
        });
        setAddCancelButton(true);
        setTitle(Util.getString("PropertySelectionDialog.selectPropertyTitle")); //$NON-NLS-1$

        setInput(Collections.EMPTY_LIST);
    }

    /**
     * Handler for selection/deselection of selectRelationalRadio
     */
    void handleSelectRelationalRadioSelected() {
        // If selected, enable Model Chooser, otherwise disable
        if (this.selectRelationalRadio.getSelection()) {
            getTableViewer().getTable().setEnabled(true);
            this.userDefinedTextField.setEnabled(false);
            this.selectRelationalSelected = true;
            // Set OK button enabled state
            boolean tableHasSelection = !getTableViewer().getSelection().isEmpty();
            if (tableHasSelection) {
                getOkButton().setEnabled(true);
            } else {
                getOkButton().setEnabled(false);
            }
        } else {
            getTableViewer().getTable().setEnabled(false);
            this.userDefinedTextField.setEnabled(true);
            this.selectRelationalSelected = false;
            // set OK button enabled state
            if (this.userDefinedPropText != null && this.userDefinedPropText.trim().length() > 0) {
                getOkButton().setEnabled(true);
            } else {
                getOkButton().setEnabled(false);
            }
        }
    }

    /**
     * Handler for user defined text changes
     */
    void handleUserDefinedTextChange() {
        this.userDefinedPropText = this.userDefinedTextField.getText();
        // set OK button enabled state
        if (!isSelectRelationalRadioSelected() && this.userDefinedPropText != null
            && this.userDefinedPropText.trim().length() > 0) {
            getOkButton().setEnabled(true);
        } else {
            getOkButton().setEnabled(false);
        }
    }

    /**
     * Determine if the SelectRelational radio button is selected
     * 
     * @return 'true' if the selectRelational radio is selected, 'false' if not.
     */
    public boolean isSelectRelationalRadioSelected() {
        return this.selectRelationalSelected;
    }

    /**
     * Get the text currently in the user-defined property TextField
     * 
     * @return the user-defined property text
     */
    public String getUserDefinedPropertyText() {
        return this.userDefinedPropText;
    }

    /**
     * Get the properties for the supplied PropBinding. The properties for the appropriate relational metaclass are returned.
     * 
     * @param propBinding the property binding
     * @return the array of properties
     */
    public Object[] getProperties( final PropBinding propBinding ) {
        List resultList = new ArrayList();
        if (propBinding != null) {
            // Get the propBinding source
            Object source = propBinding.getSourceProp();
            // ensure that it's type propery
            if (source instanceof Property) {
                Property prop = (Property)source;
                // the parent is the stereotype
                Object parent = prop.getOwner();
                if (parent instanceof Stereotype) {
                    Stereotype stereo = (Stereotype)parent;
                    // Get the stereotype metaclass
                    List extendedMetaclasses = stereo.getExtendedMetaclasses();
                    // Return the corresponding relational properties for the metaclass
                    if (extendedMetaclasses.size() != 0) {
                        Object[] umlMetaclasses = extendedMetaclasses.toArray();
                        Class metaclass = (Class)umlMetaclasses[0];
                        resultList = getRelationalProperties(metaclass, prop);
                    }

                }
            }
        }
        return resultList.toArray();
    }

    /**
     * get the relational entity properties for the relational entity which corresponds to the supplied UML metaclass
     * 
     * @param umlMetaclass the supplied uml metaclass
     * @return the corresponding relational properties
     */
    private List getRelationalProperties( Class umlMetaclass,
                                          Property prop ) {
        List resultList = new ArrayList();
        String umlMetaclassName = umlMetaclass.getName();
        if (CLASS_STR.equals(umlMetaclassName)) {
            BaseTable sampleTable = RelationalFactory.eINSTANCE.createBaseTable();
            IPropertySource propSource = ModelUtilities.getEmfPropertySourceProvider().getPropertySource(sampleTable);
            IPropertyDescriptor[] baseTableProps = propSource.getPropertyDescriptors();
            if (baseTableProps != null) {
                for (int i = 0; i < baseTableProps.length; i++) {
                    // Only add relational property to the list if it's datatype is compatible with source
                    resultList.add(baseTableProps[i]);
                }
            }
        } else if (PROPERTY_STR.equals(umlMetaclassName)) {
            Column sampleColumn = RelationalFactory.eINSTANCE.createColumn();
            IPropertySource propSource = ModelUtilities.getEmfPropertySourceProvider().getPropertySource(sampleColumn);
            IPropertyDescriptor[] columnProps = propSource.getPropertyDescriptors();
            if (columnProps != null) {
                for (int i = 0; i < columnProps.length; i++) {
                    resultList.add(columnProps[i]);
                }
            }
        }

        return resultList;
    }

    /**
     * @see org.eclipse.jface.window.Window#create()
     * @since 4.2
     */
    @Override
    public void create() {
        super.create();

        // set OK button enable state to false if nothing selected
        if (getInitialElementSelections().isEmpty()) {
            getOkButton().setEnabled(false);
        }

        // setup selection listening in order to enable OK button when selection occurs
        getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                boolean tableHasSelection = !getTableViewer().getSelection().isEmpty();
                if (isSelectRelationalRadioSelected() && tableHasSelection) {
                    getOkButton().setEnabled(true);
                } else {
                    getOkButton().setEnabled(false);
                }
            }
        });
    }
}
