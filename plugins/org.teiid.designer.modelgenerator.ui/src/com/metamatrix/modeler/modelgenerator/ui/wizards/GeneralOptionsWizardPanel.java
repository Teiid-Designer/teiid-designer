/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.modeler.modelgenerator.ui.ModelerModelGeneratorUiPlugin;
import com.metamatrix.modeler.modelgenerator.ui.PluginConstants;
import com.metamatrix.modeler.modelgenerator.uml2.processor.Uml2RelationalOptions;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.internal.widget.EditableIntegerSpinner;
import com.metamatrix.ui.internal.widget.GridPanel;
import com.metamatrix.ui.internal.widget.IListPanelController;
import com.metamatrix.ui.internal.widget.ListPanel;
import com.metamatrix.ui.internal.widget.ListPanelAdapter;

/**
 * GeneralOptionsWizardPanel. This panel contains the general options.
 */
public class GeneralOptionsWizardPanel extends Composite
    implements EventObjectListener, ModelGeneratorUiConstants, CoreStringUtil.Constants {

    private static final String GENERATE_ALL_REACHABLE_RECURSIVE_STR = Util.getString("GeneralOptionsWizardPanel.reachConstraint.generateAll.text"); //$NON-NLS-1$
    private static final String IGNORE_REACHABLE_NOT_IN_SELECTION_STR = Util.getString("GeneralOptionsWizardPanel.reachConstraint.ignoreUnselected.text"); //$NON-NLS-1$
    private static final String DATATYPE_DIALOG_TITLE = Util.getString("GenerationOptions.datatypeDialog.title"); //$NON-NLS-1$
    private static final String DATATYPE_DIALOG_LABEL_TEXT = Util.getString("GenerationOptions.datatypeDialog.label.text"); //$NON-NLS-1$
    private static final String[] REACHABILITY_OPTIONS = new String[] {GENERATE_ALL_REACHABLE_RECURSIVE_STR,
        IGNORE_REACHABLE_NOT_IN_SELECTION_STR};
    private final static String STRING_TYPE_NAME = "string"; //$NON-NLS-1$
    static final String EMPTY_STEREOTYPE_ERROR = Util.getString("GeneralOptionsWizardPanel.emptyStereotypeError"); //$NON-NLS-1$
    static final String DUPLICATE_STEREOTYPE_ERROR = Util.getString("GeneralOptionsWizardPanel.duplicateStereotypeError"); //$NON-NLS-1$

    private final static int MIN_DEFAULT_COLUMN_LENGTH = 1;
    private final static int MAX_DEFAULT_COLUMN_LENGTH = 255;

    /** Title for the panel group */
    private static final String GROUP_TITLE = Util.getString("GeneralOptionsWizardPanel.title"); //$NON-NLS-1$

    private GeneralOptionsWizardPage wizardPage;

    private GeneratorManagerOptions generatorMgrOptions;
    String textEntryDialogResult;

    ListPanel ignoredStereoList;
    ListPanel readOnlyStereoList;
    private DatatypeChooserPanel relationalColDatatypePanel;
    private Combo packageUsageCombo;
    private Combo reachabilityConstrCombo;
    EditableIntegerSpinner ispinStringLength;

    public GeneralOptionsWizardPanel( Composite parent,
                                      GeneralOptionsWizardPage page,
                                      GeneratorManagerOptions generatorMgrOptions ) {
        super(parent, SWT.NULL);
        this.wizardPage = page;
        this.generatorMgrOptions = generatorMgrOptions;

        initialize();

        // Add this as listener for datatypePanel changes
        this.relationalColDatatypePanel.addEventListener(this);

        this.wizardPage.validatePage();
    }

    /**
     * Initialize the Panel
     */
    private void initialize() {
        GridLayout layout = new GridLayout();
        this.setLayout(layout);
        createGeneralOptionsPanel(this);
        // Initialize the UI and Generator Options from PreferenceStore
        initOptionsFromPrefStore();
    }

    /**
     * Create the General Options Content Panel
     * 
     * @param parent the parent composite
     * @return the content panel
     */
    private Composite createGeneralOptionsPanel( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        // Set grid layout
        GridLayout gridLayout = new GridLayout();
        panel.setLayout(gridLayout);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Group generalGrp = WidgetFactory.createGroup(panel, GROUP_TITLE, GridData.FILL_BOTH);
        {
            // Set grp layout
            GridLayout grpLayout = new GridLayout();
            grpLayout.numColumns = 2;
            generalGrp.setLayout(grpLayout);

            // Package Usage
            GridPanel pnlPackage = new GridPanel(generalGrp, SWT.NONE, 2);
            pnlPackage.setColumnsEqualWidth(false);
            GridData gdPackage = new GridData(GridData.FILL_HORIZONTAL);
            gdPackage.horizontalSpan = 2;
            pnlPackage.setLayoutData(gdPackage);

            String title = Util.getString("GeneralOptionsWizardPanel.packageUsage.title"); //$NON-NLS-1$
            WidgetFactory.createLabel(pnlPackage, title);
            createPackageUsageCombo(pnlPackage);

            // Default Relational Column Type
            title = Util.getString("GeneralOptionsWizardPanel.relColumnType.title"); //$NON-NLS-1$
            WidgetFactory.createLabel(generalGrp, title);
            this.relationalColDatatypePanel = new DatatypeChooserPanel(generalGrp, DATATYPE_DIALOG_TITLE,
                                                                       DATATYPE_DIALOG_LABEL_TEXT);

            // Default Length for Type Defaulted Column
            title = Util.getString("GeneralOptionsWizardPanel.defaultStringLength.title"); //$NON-NLS-1$
            WidgetFactory.createLabel(generalGrp, title);
            ispinStringLength = new EditableIntegerSpinner(generalGrp, MIN_DEFAULT_COLUMN_LENGTH, MAX_DEFAULT_COLUMN_LENGTH);
            ispinStringLength.setWrap(false);

            ispinStringLength.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    setDefaultStringLength(ispinStringLength.getIntegerValue());
                }
            });

            // Class Stereotypes to Ignore
            final IListPanelController ctrlr = new ListPanelAdapter() {
                @Override
                public Object[] addButtonSelected() {
                    String title = Util.getString("GeneralOptionsWizardPanel.ignoredStereotypes.inputDialog.title"); //$NON-NLS-1$
                    String label = Util.getString("GeneralOptionsWizardPanel.ignoredStereotypes.inputDialog.label"); //$NON-NLS-1$
                    String textEntry = showTextEntryDialog(title, label, new IgnoreStereotypeValidator());
                    if (isValidStereotypeAddition(textEntry)) {
                        handleAddIgnoredStereotypeButtonSelected(textEntry);
                        return new String[] {textEntry};
                    }
                    return new String[] {};
                }

                @Override
                public Object[] removeButtonSelected( IStructuredSelection selection ) {
                    Object[] objArray = selection.toArray();
                    handleRemoveIgnoredStereotypeButtonSelected(objArray);
                    return objArray;
                }
            };
            String listTitle = Util.getString("GeneralOptionsWizardPanel.ignoredStereotypes.listTitle"); //$NON-NLS-1$
            this.ignoredStereoList = WidgetFactory.createListPanel(generalGrp, listTitle, ctrlr);
            GridData gdIgnored = new GridData(GridData.FILL_BOTH);
            gdIgnored.horizontalSpan = 2;
            ignoredStereoList.setLayoutData(gdIgnored);

            // Class Stereotypes to set ReadOnly
            final IListPanelController ctrlReadOnly = new ListPanelAdapter() {
                @Override
                public Object[] addButtonSelected() {
                    String title = Util.getString("GeneralOptionsWizardPanel.readOnlyStereotypes.inputDialog.title"); //$NON-NLS-1$
                    String label = Util.getString("GeneralOptionsWizardPanel.readOnlyStereotypes.inputDialog.label"); //$NON-NLS-1$
                    String textEntry = showTextEntryDialog(title, label, new ReadOnlyStereotypeValidator());
                    if (isValidStereotypeAddition(textEntry)) {
                        handleAddReadOnlyStereotypeButtonSelected(textEntry);
                        return new String[] {textEntry};
                    }
                    return new String[] {};
                }

                @Override
                public Object[] removeButtonSelected( IStructuredSelection selection ) {
                    Object[] objArray = selection.toArray();
                    handleRemoveReadOnlyStereotypeButtonSelected(objArray);
                    return objArray;
                }
            };
            String readOnlyListTitle = Util.getString("GeneralOptionsWizardPanel.readOnlyStereotypes.listTitle"); //$NON-NLS-1$
            this.readOnlyStereoList = WidgetFactory.createListPanel(generalGrp, readOnlyListTitle, ctrlReadOnly);
            GridData gdReadOnly = new GridData(GridData.FILL_BOTH);
            gdReadOnly.horizontalSpan = 2;
            readOnlyStereoList.setLayoutData(gdReadOnly);

            // Reachability Constraint
            GridPanel pnlReachability = new GridPanel(generalGrp, SWT.NONE, 2);
            pnlReachability.setColumnsEqualWidth(false);
            GridData gdReachability = new GridData(GridData.FILL_HORIZONTAL);
            gdReachability.horizontalSpan = 2;
            pnlReachability.setLayoutData(gdReachability);
            title = Util.getString("GeneralOptionsWizardPanel.reachConstraint.title"); //$NON-NLS-1$
            WidgetFactory.createLabel(pnlReachability, title);
            createReachabilityConstraintCombo(pnlReachability);

        }
        return panel;
    }

    boolean isValidStereotypeAddition( String newValue ) {
        return (newValue != null && !newValue.trim().equals(""));//$NON-NLS-1$
    }

    @Override
    public void setVisible( boolean b ) {
        super.setVisible(b);

        // enable the spinner if the default type supports variable length
        ispinStringLength.setEnabled(relationalColumnDataTypeHasVariableLength());
    }

    private boolean relationalColumnDataTypeHasVariableLength() {

        // jhTODO: possibly replace the following with logic to determine if the new time
        // can have a variable length...

        // if Type is now String, enable the Default Length control; otherwise disable it
        EObject relationalColDatatype = this.relationalColDatatypePanel.getSelectedDatatype();
        String typeName = ModelerCore.getWorkspaceDatatypeManager().getName(relationalColDatatype);

        if (typeName != null) {
            return (typeName.equals(STRING_TYPE_NAME));
        }
        return false;
    }

    /**
     * handler for Add Key Stereotype button
     */
    void handleAddIgnoredStereotypeButtonSelected( String newItem ) {
        TableItem[] items = this.ignoredStereoList.getTableViewer().getTable().getItems();
        List stringList = new ArrayList(items.length + 1);
        for (int i = 0; i < items.length; i++) {
            String str = (String)items[i].getData();
            stringList.add(str);
        }
        stringList.add(newItem);
        setIgnoredClassStereotypes(stringList);
    }

    /**
     * handler for Remove Key Stereotype button
     */
    void handleRemoveIgnoredStereotypeButtonSelected( Object[] objArray ) {
        TableItem[] items = this.ignoredStereoList.getTableViewer().getTable().getItems();
        List stringList = new ArrayList(items.length + 1);
        for (int i = 0; i < items.length; i++) {
            String str = (String)items[i].getData();
            stringList.add(str);
        }
        for (int i = 0; i < objArray.length; i++) {
            String str = (String)objArray[i];
            if (stringList.contains(str)) {
                stringList.remove(str);
            }
        }
        setIgnoredClassStereotypes(stringList);
    }

    /**
     * handler for Add Key Stereotype button
     */
    void handleAddReadOnlyStereotypeButtonSelected( String newItem ) {
        TableItem[] items = this.readOnlyStereoList.getTableViewer().getTable().getItems();
        List stringList = new ArrayList(items.length + 1);
        for (int i = 0; i < items.length; i++) {
            String str = (String)items[i].getData();
            stringList.add(str);
        }
        stringList.add(newItem);
        setReadOnlyClassStereotypes(stringList);
    }

    /**
     * handler for Remove Key Stereotype button
     */
    void handleRemoveReadOnlyStereotypeButtonSelected( Object[] objArray ) {
        TableItem[] items = this.readOnlyStereoList.getTableViewer().getTable().getItems();
        List stringList = new ArrayList(items.length + 1);
        for (int i = 0; i < items.length; i++) {
            String str = (String)items[i].getData();
            stringList.add(str);
        }
        for (int i = 0; i < objArray.length; i++) {
            String str = (String)objArray[i];
            if (stringList.contains(str)) {
                stringList.remove(str);
            }
        }
        setReadOnlyClassStereotypes(stringList);
    }

    /**
     * Create the PackageUsage combo box
     */
    private void createPackageUsageCombo( Composite parent ) {
        this.packageUsageCombo = WidgetFactory.createCombo(parent, SWT.READ_ONLY);

        this.packageUsageCombo.setItems(new String[] {Uml2RelationalOptions.PackageUsage.FLATTEN_LITERAL.getName(),
            Uml2RelationalOptions.PackageUsage.IGNORE_LITERAL.getName()});
        String tooltip = Util.getString("GeneralOptionsWizardPanel.packageUsageCombo.tooltip"); //$NON-NLS-1$
        this.packageUsageCombo.setToolTipText(tooltip);
        GridData gdPackage = new GridData(GridData.FILL_HORIZONTAL);
        packageUsageCombo.setLayoutData(gdPackage);

        this.packageUsageCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                handlePackageUsageComboPressed();
            }
        });
    }

    /**
     * Create the ReachabilityConstraint combo box
     */
    private void createReachabilityConstraintCombo( Composite parent ) {
        this.reachabilityConstrCombo = WidgetFactory.createCombo(parent, SWT.READ_ONLY);

        this.reachabilityConstrCombo.setItems(REACHABILITY_OPTIONS);
        String tooltip = Util.getString("GeneralOptionsWizardPanel.reachabilityConstrCombo.tooltip"); //$NON-NLS-1$
        this.reachabilityConstrCombo.setToolTipText(tooltip);
        GridData gdReachability = new GridData(GridData.FILL_HORIZONTAL);
        reachabilityConstrCombo.setLayoutData(gdReachability);

        this.reachabilityConstrCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                handleReachabilityConstrComboPressed();
            }
        });
    }

    /**
     * handler for package usage combo selection.
     */
    void handlePackageUsageComboPressed() {
        int selectedIndex = this.packageUsageCombo.getSelectionIndex();
        String itemStr = this.packageUsageCombo.getItem(selectedIndex);
        if (itemStr.equalsIgnoreCase(Uml2RelationalOptions.PackageUsage.FLATTEN_LITERAL.getName())) {
            setPackageUsage(Uml2RelationalOptions.PackageUsage.FLATTEN);
        } else if (itemStr.equalsIgnoreCase(Uml2RelationalOptions.PackageUsage.IGNORE_LITERAL.getName())) {
            setPackageUsage(Uml2RelationalOptions.PackageUsage.IGNORE);
        }
    }

    /**
     * handler for reachability constraint combo selection.
     */
    void handleReachabilityConstrComboPressed() {
        int selectedIndex = this.reachabilityConstrCombo.getSelectionIndex();
        String itemStr = this.reachabilityConstrCombo.getItem(selectedIndex);
        if (itemStr.equalsIgnoreCase(GENERATE_ALL_REACHABLE_RECURSIVE_STR)) {
            setReachabilityConstraint(Uml2RelationalOptions.GENERATE_ALL_REACHABLE_RECURSIVE);
        } else if (itemStr.equalsIgnoreCase(IGNORE_REACHABLE_NOT_IN_SELECTION_STR)) {
            setReachabilityConstraint(Uml2RelationalOptions.IGNORE_REACHABLE_NOT_IN_SELECTION);
        }
    }

    /**
     * Method that handles Events from the DatatypeChooserPanels
     * 
     * @param e the EventObject
     */
    public void processEvent( EventObject e ) {
        Object source = e.getSource();
        if (source != null) {
            if (source.equals(this.relationalColDatatypePanel)) {
                EObject relationalColDatatype = this.relationalColDatatypePanel.getSelectedDatatype();
                Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
                options.setDefaultRelationalColumnType(relationalColDatatype);

                // enable the spinner if the default type supports variable lengthf
                ispinStringLength.setEnabled(relationalColumnDataTypeHasVariableLength());

                // Update the PreferenceStore
                String typeName = ModelerCore.getWorkspaceDatatypeManager().getName(relationalColDatatype);
                IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();
                prefStore.setValue(PluginConstants.Prefs.ModelGenerator.RELATIONAL_COLUMN_TYPE, typeName);
                ModelerModelGeneratorUiPlugin.getDefault().savePluginPreferences();
            }
            // Validate the page
            this.wizardPage.validatePage();
        }
    }

    /**
     * Initialize all of the generator options from the preference store. Initializes the generator option and sets the widget
     * selection for each option.
     */
    private void initOptionsFromPrefStore() {
        // Set initial selections from preferences
        IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();
        Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();

        // ==============================================================
        // GENERAL OPTIONS
        // ==============================================================

        // ---------------------------------------
        // Relational Column Datatype
        // ---------------------------------------
        String datatypeStr = prefStore.getString(PluginConstants.Prefs.ModelGenerator.RELATIONAL_COLUMN_TYPE);
        EObject relationalColType = null;
        try {
            relationalColType = ModelerCore.getWorkspaceDatatypeManager().findDatatype(datatypeStr);
        } catch (ModelerCoreException e) {
            Util.log(e);
        }
        // Set generator Option
        options.setDefaultRelationalColumnType(relationalColType);

        // Set UI component
        this.relationalColDatatypePanel.setSelectedDatatype(relationalColType);

        // ---------------------------------------
        // Relational Column Default length
        // ---------------------------------------
        int defaultLength = prefStore.getInt(PluginConstants.Prefs.ModelGenerator.DEFAULT_STRING_LENGTH);
        // Set generator Option
        options.setGeneratedStringTypeColumnDefaultLength(defaultLength);

        // Set UI component
        this.ispinStringLength.setValue(defaultLength);

        // ---------------------------------------
        // Package Usage Option
        // ---------------------------------------
        int usageOption = prefStore.getInt(PluginConstants.Prefs.ModelGenerator.PACKAGE_USAGE);
        // Set generator Option
        String packageUsageOptionStr = null;
        if (usageOption == Uml2RelationalOptions.PackageUsage.FLATTEN) {
            packageUsageOptionStr = Uml2RelationalOptions.PackageUsage.FLATTEN_LITERAL.getName();
            options.setPackageUsage(Uml2RelationalOptions.PackageUsage.FLATTEN_LITERAL);
        } else if (usageOption == Uml2RelationalOptions.PackageUsage.IGNORE) {
            packageUsageOptionStr = Uml2RelationalOptions.PackageUsage.IGNORE_LITERAL.getName();
            options.setPackageUsage(Uml2RelationalOptions.PackageUsage.IGNORE_LITERAL);
        }
        // Set UI component
        String[] items = this.packageUsageCombo.getItems();
        for (int i = 0; i < items.length; i++) {
            String itemStr = items[i];
            if (itemStr.equalsIgnoreCase(packageUsageOptionStr)) {
                this.packageUsageCombo.select(i);
                break;
            }
        }

        // ---------------------------------------
        // Reachability Constraint Option
        // ---------------------------------------
        int reachConstr = prefStore.getInt(PluginConstants.Prefs.ModelGenerator.REACHABILITY_CONTRAINT);
        // Set generator Option
        String reachabilityOptionStr = null;
        if (reachConstr == Uml2RelationalOptions.GENERATE_ALL_REACHABLE_RECURSIVE) {
            reachabilityOptionStr = GENERATE_ALL_REACHABLE_RECURSIVE_STR;
            options.setReachabilityConstraint(reachConstr);
        } else if (reachConstr == Uml2RelationalOptions.IGNORE_REACHABLE_NOT_IN_SELECTION) {
            reachabilityOptionStr = IGNORE_REACHABLE_NOT_IN_SELECTION_STR;
            options.setReachabilityConstraint(reachConstr);
        }
        // Set UI component
        items = this.reachabilityConstrCombo.getItems();
        for (int i = 0; i < items.length; i++) {
            String itemStr = items[i];
            if (itemStr.equalsIgnoreCase(reachabilityOptionStr)) {
                this.reachabilityConstrCombo.select(i);
                break;
            }
        }

        // ---------------------------------------
        // Class Ignored Stereotypes List
        // ---------------------------------------
        String listString = prefStore.getString(PluginConstants.Prefs.ModelGenerator.CLASS_IGNORED_STEREOTYPES);
        if (listString != null) {
            String[] array = parseList(listString);
            // Set generator Option
            options.setClassStereotypesToIgnore(Arrays.asList(array));
            // Set UI Component
            this.ignoredStereoList.addItems(array);
        }

    }

    /**
     * Set the PackageUsage option
     * 
     * @param packageUsage the package usage option
     */
    private void setPackageUsage( int usageOption ) {
        // Set the Generator Option
        Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
        if (usageOption == Uml2RelationalOptions.PackageUsage.FLATTEN) {
            options.setPackageUsage(Uml2RelationalOptions.PackageUsage.FLATTEN_LITERAL);
        } else if (usageOption == Uml2RelationalOptions.PackageUsage.IGNORE) {
            options.setPackageUsage(Uml2RelationalOptions.PackageUsage.IGNORE_LITERAL);
        }

        // Update the PreferenceStore
        if (usageOption == Uml2RelationalOptions.PackageUsage.FLATTEN || usageOption == Uml2RelationalOptions.PackageUsage.IGNORE) {
            IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();
            prefStore.setValue(PluginConstants.Prefs.ModelGenerator.PACKAGE_USAGE, usageOption);
            ModelerModelGeneratorUiPlugin.getDefault().savePluginPreferences();
        }
        // Validate the page
        this.wizardPage.validatePage();
    }

    /**
     * Set the Reachability Constraint option
     * 
     * @param constraint the constraint option
     */
    private void setReachabilityConstraint( int constraint ) {
        // Set the Generator Option
        Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
        options.setReachabilityConstraint(constraint);

        // Update the PreferenceStore
        IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();
        prefStore.setValue(PluginConstants.Prefs.ModelGenerator.REACHABILITY_CONTRAINT, constraint);
        ModelerModelGeneratorUiPlugin.getDefault().savePluginPreferences();
        // Validate the page
        this.wizardPage.validatePage();
    }

    /**
     * Set the Class Stereotype names to ignore
     * 
     * @param ignoredStereos the List of stereotypes to ignore
     */
    private void setIgnoredClassStereotypes( List ignoredStereos ) {
        // Set the Generator Option
        Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
        options.setClassStereotypesToIgnore(ignoredStereos);

        // Update the PreferenceStore
        String listString = serializeList(ignoredStereos);
        IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();
        prefStore.setValue(PluginConstants.Prefs.ModelGenerator.CLASS_IGNORED_STEREOTYPES, listString);
        ModelerModelGeneratorUiPlugin.getDefault().savePluginPreferences();

        // Validate the page
        this.wizardPage.validatePage();
    }

    /**
     * Set the Class Stereotype names to set Read Only
     * 
     * @param ignoredStereos the List of stereotypes to ignore
     */
    private void setReadOnlyClassStereotypes( List readOnlyStereos ) {
        // Set the Generator Option
        Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
        options.setReadOnlyStereoTypeNames(readOnlyStereos);

        // Update the PreferenceStore
        String listString = serializeList(readOnlyStereos);
        IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();

        prefStore.setValue(PluginConstants.Prefs.ModelGenerator.CLASS_READONLY_STEREOTYPES, listString);
        ModelerModelGeneratorUiPlugin.getDefault().savePluginPreferences();

        // Validate the page
        this.wizardPage.validatePage();
    }

    /**
     * Set the Default String Length for type defaulted columns
     * 
     * @param nKeyCols the number of Key columns
     */
    void setDefaultStringLength( int nStringLength ) {
        // Set the Generator Option
        Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
        options.setGeneratedStringTypeColumnDefaultLength(nStringLength);

        // Update the PreferenceStore
        IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();
        prefStore.setValue(PluginConstants.Prefs.ModelGenerator.DEFAULT_STRING_LENGTH, nStringLength);
        ModelerModelGeneratorUiPlugin.getDefault().savePluginPreferences();

        // Validate the page
        this.wizardPage.validatePage();
    }

    /**
     * Show a simple text entry dialog and get the result
     * 
     * @param title the title of the dialog
     * @param label the label text
     * @return the entered string data
     */
    String showTextEntryDialog( final String title,
                                final String label,
                                final StereotypeValidator validator ) {
        // Dialog for string entry
        Shell shell = ModelerModelGeneratorUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        final Dialog dlg = new Dialog(shell, title) {
            @Override
            protected Control createDialogArea( final Composite parent ) {
                final Composite dlgPanel = (Composite)super.createDialogArea(parent);
                WidgetFactory.createLabel(dlgPanel, label);
                final Text nameText = WidgetFactory.createTextField(dlgPanel, GridData.FILL_HORIZONTAL);
                final CLabel errorLabel = WidgetFactory.createLabel(dlgPanel, GridData.FILL_HORIZONTAL);
                nameText.setSelection(0);
                nameText.addModifyListener(new ModifyListener() {

                    public void modifyText( final ModifyEvent event ) {
                        String newName = nameText.getText();
                        boolean valid = (newName.length() > 0);

                        // Check for empty
                        if (!valid) {
                            errorLabel.setText(EMPTY_STEREOTYPE_ERROR);
                        } else {
                            valid = validator.validate(newName);
                            // Check for duplicate
                            if (!valid) {
                                errorLabel.setText(DUPLICATE_STEREOTYPE_ERROR);
                            }
                        }

                        getButton(IDialogConstants.OK_ID).setEnabled(valid);
                        if (!valid) {
                            errorLabel.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
                        } else {
                            errorLabel.setImage(null);
                            errorLabel.setText(null);
                            GeneralOptionsWizardPanel.this.textEntryDialogResult = nameText.getText();
                        }
                    }
                });
                return dlgPanel;
            }

            @Override
            protected void createButtonsForButtonBar( final Composite parent ) {
                super.createButtonsForButtonBar(parent);
                getButton(IDialogConstants.OK_ID).setEnabled(false);
            }

            @Override
            protected Button getButton( int id ) {
                return super.getButton(id);
            }
        };
        dlg.setSizeRelativeToScreen(20, 20);
        if (dlg.open() == Window.OK) {
            return this.textEntryDialogResult;
        }
        return null;
    }

    /**
     * Parses the comma separated string into an array of strings
     * 
     * @return list
     */
    private static String[] parseList( String listString ) {
        List list = new ArrayList(10);
        StringTokenizer tokenizer = new StringTokenizer(listString, ","); //$NON-NLS-1$
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            list.add(token);
        }
        return (String[])list.toArray(new String[list.size()]);
    }

    /**
     * Serializes the array of strings into one comma separated string.
     * 
     * @param list array of strings
     * @return a single string composed of the given list
     */
    private static String serializeList( List stringList ) {
        if (stringList == null || stringList.size() == 0) {
            return ""; //$NON-NLS-1$
        }
        StringBuffer buffer = new StringBuffer();
        Iterator iter = stringList.iterator();
        while (iter.hasNext()) {
            String str = (String)iter.next();
            buffer.append(str);
            if (iter.hasNext()) {
                buffer.append(',');
            }
        }
        return buffer.toString();
    }

    interface StereotypeValidator {
        public boolean validate( String name );
    }

    class IgnoreStereotypeValidator implements StereotypeValidator {
        public boolean validate( String name ) {
            return !ignoredStereoList.contains(name);
        }
    }

    class ReadOnlyStereotypeValidator implements StereotypeValidator {
        public boolean validate( String name ) {
            return !readOnlyStereoList.contains(name);
        }
    }

}// end GeneralOptionsWizardPanel
