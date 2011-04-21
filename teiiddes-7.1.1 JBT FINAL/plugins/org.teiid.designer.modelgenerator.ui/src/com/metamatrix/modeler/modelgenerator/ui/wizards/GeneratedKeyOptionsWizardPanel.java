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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
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
import com.metamatrix.ui.internal.widget.IListPanelController;
import com.metamatrix.ui.internal.widget.ListPanel;
import com.metamatrix.ui.internal.widget.ListPanelAdapter;

/**
 * This panel contains the generated key options.
 */
public class GeneratedKeyOptionsWizardPanel extends Composite
    implements EventObjectListener, ModelGeneratorUiConstants, CoreStringUtil.Constants {

    private static final String DATATYPE_DIALOG_TITLE = Util.getString("GenerationOptions.datatypeDialog.title"); //$NON-NLS-1$
    private static final String DATATYPE_DIALOG_LABEL_TEXT = Util.getString("GenerationOptions.datatypeDialog.label.text"); //$NON-NLS-1$
    private static final String[] NUMBER_KEY_OPTIONS = new String[] {"1", "2", "3", "4", "5", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    };

    /** Title for the panel group */
    private static final String GROUP_TITLE = Util.getString("GeneratedKeyOptionsWizardPanel.title"); //$NON-NLS-1$
    private final static String STRING_TYPE_NAME = "string"; //$NON-NLS-1$

    private final static int MIN_DEFAULT_KEYCOLUMN_LENGTH = 1;
    private final static int MAX_DEFAULT_KEYCOLUMN_LENGTH = 255;

    private GeneratedKeyOptionsWizardPage wizardPage;
    private GeneratorManagerOptions generatorMgrOptions;
    String textEntryDialogResult;

    private ListPanel primaryKeyStereoNameList;
    private DatatypeChooserPanel keysDatatypePanel;
    private Text keyBaseNameField;
    private Combo numberKeysCombo;
    Spinner ispinStringLength;

    public GeneratedKeyOptionsWizardPanel( Composite parent,
                                           GeneratedKeyOptionsWizardPage page,
                                           GeneratorManagerOptions generatorMgrOptions ) {
        super(parent, SWT.NULL);
        this.wizardPage = page;
        this.generatorMgrOptions = generatorMgrOptions;

        initialize();

        // Add this as listener for datatypePanel changes
        this.keysDatatypePanel.addEventListener(this);

        this.wizardPage.validatePage();
    }

    /**
     * Initialize the Panel
     */
    private void initialize() {
        GridLayout layout = new GridLayout();
        this.setLayout(layout);
        createKeyOptionsPanel(this);
        // Initialize the UI and Generator Options from PreferenceStore
        initOptionsFromPrefStore();
    }

    /**
     * Create the Key Options Content Panel
     * 
     * @param parent the parent composite
     * @return the content panel
     */
    private Composite createKeyOptionsPanel( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        // Set grid layout
        GridLayout gridLayout = new GridLayout(2, false);
        panel.setLayout(gridLayout);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Group keysGrp = WidgetFactory.createGroup(panel, GROUP_TITLE, GridData.FILL_BOTH);
        {
            // Set grp layout
            GridLayout grpLayout = new GridLayout();
            grpLayout.numColumns = 2;
            keysGrp.setLayout(grpLayout);

            // Key Column base name
            String title = Util.getString("GeneratedKeyOptionsWizardPanel.colBaseName.title"); //$NON-NLS-1$
            WidgetFactory.createLabel(keysGrp, title);
            this.keyBaseNameField = WidgetFactory.createTextField(keysGrp, GridData.HORIZONTAL_ALIGN_FILL, "default"); //$NON-NLS-1$
            this.keyBaseNameField.addModifyListener(new ModifyListener() {
                public void modifyText( final ModifyEvent event ) {
                    setKeyColumnBaseName();
                }
            });

            // Key Column type
            title = Util.getString("GeneratedKeyOptionsWizardPanel.keyColType.title"); //$NON-NLS-1$
            WidgetFactory.createLabel(keysGrp, title);
            this.keysDatatypePanel = new DatatypeChooserPanel(keysGrp, DATATYPE_DIALOG_TITLE, DATATYPE_DIALOG_LABEL_TEXT);

            // Default Length for key Column
            title = Util.getString("GeneratedKeyOptionsWizardPanel.keyColLength.title"); //$NON-NLS-1$
            WidgetFactory.createLabel(keysGrp, title);
            ispinStringLength = new Spinner(keysGrp, SWT.NONE);
            ispinStringLength.setMinimum(MIN_DEFAULT_KEYCOLUMN_LENGTH);
            ispinStringLength.setMaximum(MAX_DEFAULT_KEYCOLUMN_LENGTH);
            ispinStringLength.setToolTipText(Util.getString("GeneratedKeyOptionsWizardPanel.keyLengthSpinner.toolTip", //$NON-NLS-1$
                                                            ispinStringLength.getMinimum(),
                                                            ispinStringLength.getMaximum()));
            ispinStringLength.addModifyListener(new ModifyListener() {
                public void modifyText( ModifyEvent theEvent ) {
                    setKeyColumnLength();
                }
            });

            // number of keys
            title = Util.getString("GeneratedKeyOptionsWizardPanel.numberKeyCols.title"); //$NON-NLS-1$
            WidgetFactory.createLabel(keysGrp, title);
            createNumberKeysCombo(keysGrp);

            // primary key stereotype names
            title = Util.getString("GeneratedKeyOptionsWizardPanel.primaryKeyStereoNames.title"); //$NON-NLS-1$
            WidgetFactory.createLabel(keysGrp, title);
            final IListPanelController ctrlr = new ListPanelAdapter() {
                @Override
                public Object[] addButtonSelected() {
                    String title = Util.getString("GeneratedKeyOptionsWizardPanel.primaryKeyStereoNames.inputDialog.title"); //$NON-NLS-1$
                    String label = Util.getString("GeneratedKeyOptionsWizardPanel.primaryKeyStereoNames.inputDialog.label"); //$NON-NLS-1$
                    String textEntry = showTextEntryDialog(title, label);
                    handleAddKeyStereotypeButtonSelected(textEntry);
                    return new String[] {textEntry};
                }

                @Override
                public Object[] removeButtonSelected( IStructuredSelection selection ) {
                    Object[] objArray = selection.toArray();
                    handleRemoveKeyStereotypeButtonSelected(objArray);
                    return objArray;
                }
            };
            String listTitle = Util.getString("GeneratedKeyOptionsWizardPanel.primaryKeyStereoNames.listTitle"); //$NON-NLS-1$
            this.primaryKeyStereoNameList = WidgetFactory.createListPanel(keysGrp, listTitle, ctrlr);

        }
        return panel;
    }

    /**
     * handler for Add Ignored Stereotype button
     */
    void handleAddKeyStereotypeButtonSelected( String newItem ) {
        TableItem[] items = this.primaryKeyStereoNameList.getTableViewer().getTable().getItems();
        List stringList = new ArrayList(items.length + 1);
        for (int i = 0; i < items.length; i++) {
            String str = (String)items[i].getData();
            stringList.add(str);
        }
        stringList.add(newItem);
        setPrimaryKeyStereotypes(stringList);
    }

    /**
     * handler for Remove Ignored Stereotype button
     */
    void handleRemoveKeyStereotypeButtonSelected( Object[] objArray ) {
        TableItem[] items = this.primaryKeyStereoNameList.getTableViewer().getTable().getItems();
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
        setPrimaryKeyStereotypes(stringList);
    }

    /**
     * Create the NumberOfKeys combo box
     */
    private void createNumberKeysCombo( Composite parent ) {
        this.numberKeysCombo = WidgetFactory.createCombo(parent, SWT.READ_ONLY);

        this.numberKeysCombo.setItems(NUMBER_KEY_OPTIONS);
        String tooltip = Util.getString("GeneratedKeyOptionsWizardPanel.numberKeyCombo.tooltip"); //$NON-NLS-1$
        this.numberKeysCombo.setToolTipText(tooltip);

        this.numberKeysCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                handleNumberKeysComboPressed();
            }
        });
    }

    /**
     * handler for Number of keys combo selection.
     */
    void handleNumberKeysComboPressed() {
        int selectedIndex = this.numberKeysCombo.getSelectionIndex();
        String itemStr = this.numberKeysCombo.getItem(selectedIndex);
        setNumberOfKeyColumns(Integer.valueOf(itemStr).intValue());
    }

    /**
     * Method that handles Events from the DatatypeChooserPanels
     * 
     * @param e the EventObject
     */
    public void processEvent( EventObject e ) {
        Object source = e.getSource();
        if (source != null) {
            if (source.equals(this.keysDatatypePanel)) {
                EObject keysDatatype = this.keysDatatypePanel.getSelectedDatatype();
                Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
                options.setTypeOfKeyColumns(keysDatatype);

                // enable the spinner if the default type supports variable lengthf
                ispinStringLength.setEnabled(keyColumnDataTypeHasVariableLength());

                // Update the PreferenceStore
                String typeName = ModelerCore.getWorkspaceDatatypeManager().getName(keysDatatype);
                IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();
                prefStore.setValue(PluginConstants.Prefs.ModelGenerator.KEY_COLUMN_TYPE, typeName);
                ModelerModelGeneratorUiPlugin.getDefault().savePreferences();
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
        // GENERATED KEY OPTIONS
        // ==============================================================

        // ---------------------------------------
        // Key Column Datatype
        // ---------------------------------------
        String datatypeStr = prefStore.getString(PluginConstants.Prefs.ModelGenerator.KEY_COLUMN_TYPE);
        EObject keyColType = null;
        try {
            keyColType = ModelerCore.getWorkspaceDatatypeManager().findDatatype(datatypeStr);
        } catch (ModelerCoreException e) {
            Util.log(e);
        }
        // Set generator Option
        options.setTypeOfKeyColumns(keyColType);

        // Set UI component
        this.keysDatatypePanel.setSelectedDatatype(keyColType);

        // ---------------------------------------
        // Key Column Default length
        // ---------------------------------------
        int defaultLength = prefStore.getInt(PluginConstants.Prefs.ModelGenerator.KEY_COLUMN_LENGTH);
        // Set generator Option
        options.setKeyColumnLength(defaultLength);

        // Set UI component
        this.ispinStringLength.setSelection(defaultLength);

        // ---------------------------------------
        // PrimaryKey Stereotypes List
        // ---------------------------------------
        String listString = prefStore.getString(PluginConstants.Prefs.ModelGenerator.PRIMARY_KEY_STEREOTYPES);
        if (listString != null) {
            String[] array = parseList(listString);
            // Set generator Option
            options.setPrimaryKeyStereotypeNames(Arrays.asList(array));
            // Set UI Component
            this.primaryKeyStereoNameList.addItems(array);
        }

        // ---------------------------------------
        // Number of Generated Key Columns
        // ---------------------------------------
        int numberKeyCols = prefStore.getInt(PluginConstants.Prefs.ModelGenerator.NUMBER_KEY_COLUMNS);
        // Set generator Option
        options.setNumberOfKeyColumns(numberKeyCols);
        // Set UI component
        String[] items = this.numberKeysCombo.getItems();
        for (int i = 0; i < items.length; i++) {
            String itemStr = items[i];
            if (itemStr.equalsIgnoreCase(String.valueOf(numberKeyCols))) {
                this.numberKeysCombo.select(i);
                break;
            }
        }

        // ---------------------------------------
        // Key Base Name
        // ---------------------------------------
        String baseName = prefStore.getString(PluginConstants.Prefs.ModelGenerator.KEY_COLUMN_BASE_NAME);
        // Set generator Option
        options.setKeyColumnBaseName(baseName);
        // Set UI Component
        this.keyBaseNameField.setText(baseName);
    }

    /**
     * Set the Primary Key Stereotype names
     * 
     * @param pKeyNames the List of Primary Key stereotype names
     */
    private void setPrimaryKeyStereotypes( List pKeyNames ) {
        // Set the Generator Option
        Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
        options.setPrimaryKeyStereotypeNames(pKeyNames);

        // Update the PreferenceStore
        String listString = serializeList(pKeyNames);
        IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();
        prefStore.setValue(PluginConstants.Prefs.ModelGenerator.PRIMARY_KEY_STEREOTYPES, listString);
        ModelerModelGeneratorUiPlugin.getDefault().savePreferences();
        // Validate the page
        this.wizardPage.validatePage();
    }

    /**
     * Set the Number of KeyColumns to be generated
     * 
     * @param nKeyCols the number of Key columns
     */
    private void setNumberOfKeyColumns( int nKeyCols ) {
        // Set the Generator Option
        Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
        options.setNumberOfKeyColumns(nKeyCols);

        // Update the PreferenceStore
        IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();
        prefStore.setValue(PluginConstants.Prefs.ModelGenerator.NUMBER_KEY_COLUMNS, nKeyCols);
        ModelerModelGeneratorUiPlugin.getDefault().savePreferences();
        // Validate the page
        this.wizardPage.validatePage();
    }

    /**
     * Set the KeyColumn Base name for generated keys
     * 
     * @param baseName the key column baseName
     */
    void setKeyColumnBaseName() {
        String baseName = this.keyBaseNameField.getText();
        if (baseName != null && baseName.trim().length() > 0) {
            // Set the Generator Option
            Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
            options.setKeyColumnBaseName(baseName);

            // Update the PreferenceStore
            IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();
            prefStore.setValue(PluginConstants.Prefs.ModelGenerator.KEY_COLUMN_BASE_NAME, baseName);
            ModelerModelGeneratorUiPlugin.getDefault().savePreferences();
        }
        // Validate the page
        this.wizardPage.validatePage();
    }

    /**
     * Set the Key Column Length for type defaulted columns
     */
    void setKeyColumnLength() {
        int nLength = ispinStringLength.getSelection();

        // Set the Generator Option
        Uml2RelationalOptions options = this.generatorMgrOptions.getUml2RelationalOptions();
        
        if (nLength != options.getKeyColumnLength()) {
            options.setKeyColumnLength(nLength);
        }

        // Update the PreferenceStore
        IPreferenceStore prefStore = ModelerModelGeneratorUiPlugin.getDefault().getPreferenceStore();
        
        if (nLength != prefStore.getInt(PluginConstants.Prefs.ModelGenerator.KEY_COLUMN_LENGTH)) {
            prefStore.setValue(PluginConstants.Prefs.ModelGenerator.KEY_COLUMN_LENGTH, nLength);
            ModelerModelGeneratorUiPlugin.getDefault().savePreferences();
        }

        // Validate the page
        this.wizardPage.validatePage();
    }

    @Override
    public void setVisible( boolean b ) {
        super.setVisible(b);

        // enable the spinner if the default type supports variable length
        ispinStringLength.setEnabled(keyColumnDataTypeHasVariableLength());
    }

    private boolean keyColumnDataTypeHasVariableLength() {
        // jhTODO: possibly replace the following with logic to determine if the new time
        // can have a variable length...

        // if Type is now String, enable the Default Length control; otherwise disable it
        EObject keyColDatatype = this.keysDatatypePanel.getSelectedDatatype();
        String typeName = ModelerCore.getWorkspaceDatatypeManager().getName(keyColDatatype);

        if (typeName != null) {
            return (typeName.equals(STRING_TYPE_NAME));
        }
        return false;
    }

    /**
     * Show a simple text entry dialog and get the result
     * 
     * @param title the title of the dialog
     * @param label the label text
     * @return the entered string data
     */
    String showTextEntryDialog( final String title,
                                final String label ) {
        // Dialog for string entry
        Shell shell = ModelerModelGeneratorUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        final Dialog dlg = new Dialog(shell, title) {
            @Override
            protected Control createDialogArea( final Composite parent ) {
                final Composite dlgPanel = (Composite)super.createDialogArea(parent);
                WidgetFactory.createLabel(dlgPanel, label);
                final Text nameText = WidgetFactory.createTextField(dlgPanel, GridData.FILL_HORIZONTAL);
                nameText.setSelection(0);
                nameText.addModifyListener(new ModifyListener() {
                    public void modifyText( final ModifyEvent event ) {
                        handleModifyText(nameText);
                    }
                });
                return dlgPanel;
            }

            @Override
            protected void createButtonsForButtonBar( final Composite parent ) {
                super.createButtonsForButtonBar(parent);
                getButton(IDialogConstants.OK_ID).setEnabled(false);
            }

            void handleModifyText( Text nameText ) {
                final String newName = nameText.getText();
                final boolean valid = (newName.length() > 0);
                getButton(IDialogConstants.OK_ID).setEnabled(valid);
                if (valid) {
                    textEntryDialogResult = nameText.getText();
                }
            }
        };
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

}// end GeneratedKeyOptionsWizardPanel
