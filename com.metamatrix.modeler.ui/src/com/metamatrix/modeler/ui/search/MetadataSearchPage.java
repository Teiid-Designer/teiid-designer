/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.ui.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.xsd.util.RuntimeType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.search.MetadataSearch;
import com.metamatrix.modeler.internal.ui.search.SearchPageUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeSelectionDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.MetamodelSelectionDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.MetamodelTreeViewer;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * The <code>MetadataSearchPage</code> is the UI to perform workspace Metadata searching.
 * 
 * @since 6.0.0
 */
public final class MetadataSearchPage extends DialogPage implements ISearchPage, UiConstants {

    // ===========================================================================================================================
    // Interfaces
    // ===========================================================================================================================

    /**
     * Constants used in the {@link IDialogSettings}.
     * 
     * @since 6.0.0
     */
    interface DialogSettingsConstants {
        String ANY_OBJ_TYPE = "anyObjectType"; //$NON-NLS-1$
        String METACLASS_TYPE = "metaclassType"; //$NON-NLS-1$
        String METACLASS_MRU = "metaclassMru"; //$NON-NLS-1$
        String LAST_USED_METACLASS = "lastUsedMetaclass"; //$NON-NLS-1$
        String ANY_DATA_TYPE = "anyDataType"; //$NON-NLS-1$
        String SIMPLE_DATA_TYPE = "simpleDataType"; //$NON-NLS-1$
        String SELECTED_SIMPLE_TYPE = "selectedSimpleType"; //$NON-NLS-1$
        String INCLUDE_SUBTYPES = "includeSubTypes"; //$NON-NLS-1$
        String RUNTIME_TYPE = "runtimeType"; //$NON-NLS-1$
        String SELECTED_RUNTIME_TYPE = "selectedRuntimeType"; //$NON-NLS-1$
        String INCLUDE_PROPERTY = "includeProperty"; //$NON-NLS-1$
        String SELECTED_PROPERTY = "selectedProperty"; //$NON-NLS-1$
        String CONTAINS_TEXT = "containsText"; //$NON-NLS-1$
        String NOT_CONTAIN_TEXT = "notContainText"; //$NON-NLS-1$
        String EXACT_MATCH = "exactMatch"; //$NON-NLS-1$
        String TEXT_PATTERN = "textPattern"; //$NON-NLS-1$
        String LAST_USED_TEXT_PATTERN = "lastUsedTextPattern"; //$NON-NLS-1$
        int MRU_LIMIT = 20;
    }

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(MetadataSearchPage.class);

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private ISearchPageContainer searchPageContainer;

    /** The currently selected metaclass. */
    private EClass metaclass;

    /** MRU of metaclass URIs. */
    private List metaclassMru;

    /** Label provider for metaclass. */
    private ILabelProvider labelProvider;

    /** The currently selected runtime type. */
    private String runtimeType;

    /** Search business object that executes search. */
    private MetadataSearch searchMgr;

    /** Persisted dialog settings. */
    private IDialogSettings settings;

    /** The currently selected simple type. */
    private XSDSimpleTypeDefinition simpleType;

    // ===========================================================================================================================
    // Controls
    // ===========================================================================================================================

    /** radio button for selecting any data type. */
    private Button btnAnyDataType;

    /** radio button for selecting any object type. */
    private Button btnAnyObjectType;

    /** push button to show metaclass chooser dialog. */
    private Button btnBrowseMetaclass;

    /** push button to show simple type chooser dialog. */
    private Button btnBrowseSimpleType;

    /** radio button for selecting objects with specified property containing specified text pattern. */
    private Button btnContains;

    /** radio button for selecting objects having a specified metaclass. */
    private Button btnMetaclass;

    /** radio button for selecting objects with specified property that does not contain the specified text pattern. */
    private Button btnNotContain;

    /** radio button for selecting objects having a specified runtime type. */
    private Button btnRuntimeType;

    /** radio button for selecting objects having a specified simple type. */
    private Button btnSimpleType;

    private Combo cbxMetaclass;

    /** combobox for listing available properties/features to search for. */
    private Combo cbxProperties;

    /** combobox for listing available runtime types to search for. */
    private Combo cbxRuntimeType;

    /** combobox for listing most recently used property text patterns to search for. */
    private Combo cbxTextPattern;

    private Button chkExactMatch;

    private Button chkIncludeProperty;

    private Button chkIncludeSubtypes;

    private Text txfSimpleType;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs a <code>MetadataSearchPage</code>.
     */
    public MetadataSearchPage() {
        this.searchMgr = ModelerCore.createMetadataSearch();
        this.metaclassMru = new ArrayList();
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {
        initializeDialogSettings();
        setControl(createControlImpl(parent));
        restoreState();
    }

    private Composite createControlImpl( Composite theParent ) {
        //
        // Create main container
        //

        final int MAIN_COLS = 1;
        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        pnlMain.setLayout(new GridLayout(MAIN_COLS, false));

        //
        // pnlMain contents
        //

        //
        // ROW 1 - object type group
        //

        final int OBJ_TYPE_COLS = 3;
        Group pnl = WidgetFactory.createGroup(pnlMain, Util.getString(PREFIX + "group.objectType"), //$NON-NLS-1$
                                              GridData.FILL_BOTH,
                                              MAIN_COLS,
                                              OBJ_TYPE_COLS);
        pnl.setFont(JFaceResources.getDefaultFont()); // undo bold font WidgetFactory assigned
        createObjectTypePanelContents(pnl, OBJ_TYPE_COLS);

        //
        // ROW 2 - data type group
        //

        final int DATA_TYPE_COLS = 3;
        pnl = WidgetFactory.createGroup(pnlMain, Util.getString(PREFIX + "group.dataType"), //$NON-NLS-1$
                                        GridData.FILL_BOTH,
                                        MAIN_COLS,
                                        DATA_TYPE_COLS);
        pnl.setFont(JFaceResources.getDefaultFont()); // undo bold font WidgetFactory assigned
        createDataTypePanelContents(pnl, DATA_TYPE_COLS);

        //
        // ROW 3 - properties group
        //

        final int PROPERTIES_COLS = 4;
        pnl = WidgetFactory.createGroup(pnlMain, Util.getString(PREFIX + "group.properties"), //$NON-NLS-1$
                                        GridData.FILL_BOTH,
                                        MAIN_COLS,
                                        PROPERTIES_COLS);
        pnl.setFont(JFaceResources.getDefaultFont()); // undo bold font WidgetFactory assigned
        createPropertiesPanelContents(pnl, PROPERTIES_COLS);

        //
        // ROW 4 Scope is added by SearchPage framework
        //

        return pnlMain;
    }

    /**
     * Constructs the panel containing the controls for managing the UI for the object type criteria.
     * 
     * @param theParent the panel's parent container
     * @param theColumnCount the parent's column count
     */
    private void createDataTypePanelContents( Composite theParent,
                                              int theColumnCount ) {
        //
        // ROW 1
        //

        // any object type radio button
        this.btnAnyDataType = WidgetFactory.createRadioButton(theParent, Util.getString(PREFIX + "radioButton.anyDataType"), //$NON-NLS-1$
                                                              0,
                                                              theColumnCount,
                                                              false);
        this.btnAnyDataType.setToolTipText(Util.getString(PREFIX + "radioButton.anyDataType.tip")); //$NON-NLS-1$
        this.btnAnyDataType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleAnyDataTypeSelected();
            }
        });

        //
        // ROW 2
        //

        // simple type radio button (selected as default)
        this.btnSimpleType = WidgetFactory.createRadioButton(theParent, Util.getString(PREFIX + "radioButton.simpleType"), //$NON-NLS-1$
                                                             false);
        this.btnSimpleType.setToolTipText(Util.getString(PREFIX + "radioButton.simpleType.tip")); //$NON-NLS-1$
        this.btnSimpleType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleSimpleTypeSelected();
            }
        });

        // textfield for simple type
        this.txfSimpleType = WidgetFactory.createTextField(theParent, GridData.FILL_HORIZONTAL);
        this.txfSimpleType.setToolTipText(Util.getString(PREFIX + "text.simpleType.tip")); //$NON-NLS-1$
        this.txfSimpleType.setEditable(false);
        this.txfSimpleType.setEnabled(false);
        this.txfSimpleType.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleSimpleTypeChanged();
            }
        });

        // browse simple type button
        this.btnBrowseSimpleType = WidgetFactory.createButton(theParent, InternalUiConstants.Widgets.BROWSE_BUTTON);
        this.btnBrowseSimpleType.setToolTipText(Util.getString(PREFIX + "button.browseSimpleType.tip")); //$NON-NLS-1$
        this.btnBrowseSimpleType.setEnabled(false);
        this.btnBrowseSimpleType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleBrowseSimpleType();
            }
        });

        //
        // ROW 3
        //

        // include subtype checkbox
        this.chkIncludeSubtypes = WidgetFactory.createCheckBox(theParent, Util.getString(PREFIX + "checkBox.includeSubtypes"), //$NON-NLS-1$
                                                               SWT.LEFT,
                                                               theColumnCount);
        ((GridData)this.chkIncludeSubtypes.getLayoutData()).horizontalIndent = 20;
        this.chkIncludeSubtypes.setToolTipText(Util.getString(PREFIX + "checkBox.includeSubtypes.tip")); //$NON-NLS-1$
        this.chkIncludeSubtypes.setEnabled(false);
        this.chkIncludeSubtypes.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleIncludeSubtypesSelected();
            }
        });

        //
        // ROW 4
        //

        // runtime type radio button
        this.btnRuntimeType = WidgetFactory.createRadioButton(theParent, Util.getString(PREFIX + "radioButton.runtimeType")); //$NON-NLS-1$
        this.btnRuntimeType.setToolTipText(Util.getString(PREFIX + "radioButton.runtimeType.tip")); //$NON-NLS-1$
        this.btnRuntimeType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleRuntimeTypeSelected();
            }
        });

        // combo for runtime type list
        this.cbxRuntimeType = WidgetFactory.createCombo(theParent,
                                                        SWT.READ_ONLY,
                                                        GridData.HORIZONTAL_ALIGN_FILL,
                                                        (theColumnCount - 1));
        this.cbxRuntimeType.setToolTipText(Util.getString(PREFIX + "combo.runtimeType.tip")); //$NON-NLS-1$
        this.cbxRuntimeType.setEnabled(false);
        WidgetUtil.setComboItems(cbxRuntimeType, RuntimeType.VALUES, new LabelProvider(), true);
        this.cbxRuntimeType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleRuntimeTypeChanged();
            }
        });

    }

    /**
     * Constructs the panel containing the controls for managing the UI for the object type criteria.
     * 
     * @param theParent the panel's parent container
     * @param theColumnCount the parent's column count
     */
    private void createObjectTypePanelContents( Composite theParent,
                                                int theColumnCount ) {
        //
        // ROW 1
        //

        // any object type radio button
        this.btnAnyObjectType = WidgetFactory.createRadioButton(theParent, Util.getString(PREFIX + "radioButton.anyObjectType"), //$NON-NLS-1$
                                                                0,
                                                                theColumnCount,
                                                                false);
        this.btnAnyObjectType.setToolTipText(Util.getString(PREFIX + "radioButton.anyObjectType.tip")); //$NON-NLS-1$
        this.btnAnyObjectType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleAnyObjectTypeSelected();
            }
        });

        //
        // ROW 2
        //

        // metaclass radio button
        this.btnMetaclass = WidgetFactory.createRadioButton(theParent, Util.getString(PREFIX + "radioButton.metaclass")); //$NON-NLS-1$
        this.btnMetaclass.setToolTipText(Util.getString(PREFIX + "radioButton.metaclass.tip")); //$NON-NLS-1$
        this.btnMetaclass.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleMetaclassSelected();
            }
        });

        // textfield for runtime type
        this.cbxMetaclass = WidgetFactory.createCombo(theParent, SWT.READ_ONLY, GridData.FILL_HORIZONTAL);
        this.cbxMetaclass.setToolTipText(Util.getString(PREFIX + "combo.metaclass.tip")); //$NON-NLS-1$
        this.cbxMetaclass.setEnabled(false);
        this.cbxMetaclass.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleMetaclassChanged();
            }
        });

        // browse runtime type button
        this.btnBrowseMetaclass = WidgetFactory.createButton(theParent, InternalUiConstants.Widgets.BROWSE_BUTTON);
        this.btnBrowseMetaclass.setToolTipText(Util.getString(PREFIX + "button.browseMetaclass.tip")); //$NON-NLS-1$
        this.btnBrowseMetaclass.setEnabled(false);
        this.btnBrowseMetaclass.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleBrowseMetaclass();
            }
        });
    }

    /**
     * Creates the contents of the properties/feature panel.
     * 
     * @param theParent the parent container
     * @param theColumnCount the layout columns in the parent
     * @since 6.0.0
     */
    private void createPropertiesPanelContents( Composite theParent,
                                                int theColumnCount ) {
        //
        // ROW 1
        //

        // include property in search
        this.chkIncludeProperty = WidgetFactory.createCheckBox(theParent, Util.getString(PREFIX + "checkBox.includeProperty"), //$NON-NLS-1$
                                                               SWT.LEFT);
        this.chkIncludeProperty.setToolTipText(Util.getString(PREFIX + "checkBox.includeProperty.tip")); //$NON-NLS-1$
        this.chkIncludeProperty.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleIncludePropertySelected();
            }
        });

        // List of properties
        this.cbxProperties = WidgetFactory.createCombo(theParent,
                                                       SWT.READ_ONLY,
                                                       GridData.HORIZONTAL_ALIGN_FILL,
                                                       (theColumnCount - 1));
        this.cbxProperties.setItems(this.searchMgr.getFeaturesNames());
        this.cbxProperties.setToolTipText(Util.getString(PREFIX + "combo.properties.tip")); //$NON-NLS-1$
        this.cbxProperties.setEnabled(false);
        this.cbxProperties.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handlePropertySelected();
            }
        });

        // enablement for all property related controls based on having properties
        boolean enable = this.cbxProperties.getItemCount() > 0;
        this.chkIncludeProperty.setEnabled(enable);

        // select first property to avoid having nothing selected at startup
        if (enable) {
            this.cbxProperties.select(0);
        }

        //
        // ROW 2
        //

        final int INDENT = 20;

        // property contains text pattern radio button (selected as default)
        this.btnContains = WidgetFactory.createRadioButton(theParent, Util.getString(PREFIX + "radioButton.contains")); //$NON-NLS-1$
        this.btnContains.setToolTipText(Util.getString(PREFIX + "radioButton.contains.tip")); //$NON-NLS-1$
        this.btnContains.setEnabled(enable);
        this.btnContains.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleContainsSelected();
            }
        });
        ((GridData)this.btnContains.getLayoutData()).horizontalIndent = INDENT;

        // property does not contain text pattern radio button
        this.btnNotContain = WidgetFactory.createRadioButton(theParent, Util.getString(PREFIX + "radioButton.notContain")); //$NON-NLS-1$
        this.btnNotContain.setToolTipText(Util.getString(PREFIX + "radioButton.notContain.tip")); //$NON-NLS-1$
        this.btnNotContain.setEnabled(enable);
        this.btnNotContain.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleNotContainSelected();
            }
        });

        // exact match checkbox so that results match the text pattern
        this.chkExactMatch = WidgetFactory.createCheckBox(theParent, Util.getString(PREFIX + "checkBox.exactMatch"), //$NON-NLS-1$
                                                          GridData.FILL_HORIZONTAL);
        this.chkExactMatch.setToolTipText(Util.getString(PREFIX + "checkBox.exactMatch.tip")); //$NON-NLS-1$
        this.chkExactMatch.setEnabled(false);
        this.chkExactMatch.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleExactMatchSelected();
            }
        });

        //
        // ROW 3
        //

        // label for text patterns
        CLabel lbl = WidgetFactory.createLabel(theParent,
                                               GridData.HORIZONTAL_ALIGN_BEGINNING,
                                               theColumnCount,
                                               Util.getString(PREFIX + "label.textPattern")); //$NON-NLS-1$
        ((GridData)lbl.getLayoutData()).horizontalIndent = INDENT;

        //
        // ROW 4
        //

        // MRU for text patterns
        this.cbxTextPattern = WidgetFactory.createCombo(theParent, SWT.NONE, GridData.FILL_HORIZONTAL, (theColumnCount - 1));
        this.cbxTextPattern.setToolTipText(Util.getString(PREFIX + "combo.textPattern.tip")); //$NON-NLS-1$
        this.cbxTextPattern.setEnabled(enable);
        this.cbxTextPattern.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleTextPatternModified();
            }
        });
        ((GridData)this.cbxTextPattern.getLayoutData()).horizontalIndent = INDENT;
    }

    /**
     * Obtains the label provider used for metaclass.
     * 
     * @return the label provider
     * @since 6.0.0
     */
    private ILabelProvider getLabelProvider() {
        if (this.labelProvider == null) {
            // construct dialog to get it's label provider
            MetamodelSelectionDialog dialog = (MetamodelSelectionDialog)MetamodelTreeViewer.createSelectionDialog(getShell(),
                                                                                                                  false);
            this.labelProvider = (ILabelProvider)dialog.getViewerLabelProvider();
        }

        return this.labelProvider;
    }

    /**
     * Obtains the <code>EClass</code> for the specified {@link URI} string representation.
     * 
     * @param theUri the URI whose metaclass is being requested
     * @return the metaclass or <code>null</code>
     * @since 6.0.0
     */
    private EClass getMetaclass( String theUri ) {
        EClass result = null;

        if ((theUri != null) && (theUri.length() > 0)) {
            try {
                Object temp = ModelUtilities.getWorkspaceContainer().getEObject(URI.createURI(theUri), true);

                if ((temp != null) && (temp instanceof EClass)) {
                    result = (EClass)temp;
                } else {
                    Util.log("Not EClass. URI = " + temp); //$NON-NLS-1$
                }
            } catch (CoreException theException) {
                Util.log(theException);
            }
        }

        return result;
    }

    /**
     * Obtains the text representation of the specified metaclass URI.
     * 
     * @param theMetaclassUri the metaclass URI whose text representation is being requested
     * @return the text
     * @since 6.0.0
     */
    private String getMetaclassText( String theMetaclassUri ) {
        return getMetaclass(theMetaclassUri).getName();// getLabelProvider().getText(getMetaclass(theMetaclassUri));
    }

    /**
     * Objtains the string representation of the {@link URI} for the specified metaclass.
     * 
     * @param theMetaclass the metaclass whose URI is being requested
     * @return the string representation of the URI
     * @since 6.0.0
     */
    private String getMetaclassUri( EClass theMetaclass ) {
        return ModelerCore.getModelEditor().getUri(theMetaclass).toString();
    }

    /**
     * Handler for when the any data type radio button is selected/deselected.
     * 
     * @since 6.0.0
     */
    void handleAnyDataTypeSelected() {
        // only update controls and business object when selected since this is a radio button and
        // the other radio buttons in the group also only update when they're selected
        if (this.btnAnyDataType.getSelection()) {
            // disable
            this.txfSimpleType.setEnabled(false);
            this.btnBrowseSimpleType.setEnabled(false);
            this.chkIncludeSubtypes.setEnabled(false);
            this.cbxRuntimeType.setEnabled(false);

            // synch business object
            this.searchMgr.setDatatype(null, false);
            this.searchMgr.setRuntimeType(null);

            // update search button status
            updateSearchState();
        }
    }

    void handleAnyObjectTypeSelected() {
        // only update controls and business object when selected since this is a radio button and
        // the other radio buttons in the group also only update when they're selected
        if (this.btnAnyObjectType.getSelection()) {
            // disable
            this.cbxMetaclass.setEnabled(false);
            this.btnBrowseMetaclass.setEnabled(false);

            // set enablement and update business object
            setDataTypePanelEnabled(true);

            // synch business object
            this.searchMgr.setMetaClass(null);

            // update search button status
            updateSearchState();
        }
    }

    void handleBrowseMetaclass() {
        MetamodelSelectionDialog dialog = (MetamodelSelectionDialog)MetamodelTreeViewer.createSelectionDialog(getShell(), false);

        if (this.labelProvider == null) {
            this.labelProvider = (ILabelProvider)dialog.getViewerLabelProvider();
        }

        dialog.setAllowMultiple(false);

        // set dialog to current metaclass if necessary
        if (this.metaclass != null) {
            dialog.setInitialSelection(this.metaclass);
        }

        // if user makes a selection and OKs dialog validation takes place in handleMetaclassChanged()
        if (dialog.open() == Window.OK) {
            updateMetaclass((EClass)dialog.getResult()[0]); // validator ensures right type of result
            updateDataTypePanelEnable(); // added to upadte data type panel. sz
        }
    }

    void handleBrowseSimpleType() {
        DatatypeSelectionDialog dialog = new DatatypeSelectionDialog(getShell());

        // set dialog to current simple type if necessary
        if (this.simpleType != null) {
            dialog.setInitialSelections(new Object[] {this.simpleType});
        }

        // if user makes a selection and OKs dialog make sure validate
        if (dialog.open() == Window.OK) {
            updateSimpleType((XSDSimpleTypeDefinition)dialog.getResult()[0]);
        }
    }

    /**
     * Handler for when the contains text pattern radio button is selected/deselected.
     * 
     * @since 6.0.0
     */
    void handleContainsSelected() {
        // only validate when selected since that's what all the other buttons in the radio button group are doing
        if (this.btnContains.getSelection()) {
            updatePropertyCriteria();
        }
    }

    /**
     * Handler for when the exact match checkbox is selected/deselected.
     * 
     * @since 6.0.0
     */
    void handleExactMatchSelected() {
        // don't allow unchecking if Object URI search
        if (this.cbxProperties.getText().equals(MetadataSearch.OBJECT_URI_FEATURE)) {
            if (!this.chkExactMatch.getSelection()) {
                this.chkExactMatch.setSelection(true);
            }
        }

        updatePropertyCriteria();
    }

    /**
     * Handler for when the include subtypes of simple type checkbox is checked/unchecked.
     * 
     * @since 6.0.0
     */
    void handleIncludeSubtypesSelected() {
        handleSimpleTypeChanged();
    }

    /**
     * Handler for when the include property in search checkbox is checked/unchecked.
     * 
     * @since 6.0.0
     */
    void handleIncludePropertySelected() {
        boolean enable = this.chkIncludeProperty.getSelection() && this.chkIncludeProperty.getEnabled();

        this.cbxProperties.setEnabled(enable);
        this.btnContains.setEnabled(enable);
        this.btnNotContain.setEnabled(enable);
        this.chkExactMatch.setEnabled(enable);
        this.cbxTextPattern.setEnabled(enable);

        handlePropertySelected(); // call property selected handler since properties were re-enabled
    }

    /**
     * Handler for when the selected metaclass changes.
     * 
     * @since 6.0.0
     */
    void handleMetaclassChanged() {
        int index = this.cbxMetaclass.getSelectionIndex();
        this.metaclass = getMetaclass((String)this.metaclassMru.get(index));

        // this call updates search state
        handleMetaclassSelected();
    }

    /**
     * Handler for when the metaclass radio button is selected/deselected.
     * 
     * @since 6.0.0
     */
    void handleMetaclassSelected() {
        // only update controls and business object when selected since this is a radio button and
        // the other radio buttons in the group also only update when they're selected
        if (this.btnMetaclass.getSelection()) {
            // enable
            this.btnBrowseMetaclass.setEnabled(true);
            this.cbxMetaclass.setEnabled(true);

            // synch business object
            this.searchMgr.setMetaClass(this.metaclass);

            // this updates data type panel. sz
            updateDataTypePanelEnable();

            // update search button status
            updateSearchState();
        }
    }

    /**
     * Updates data type panel based on the metaclass radio button selection, also updates on meta class combo population. sz
     */
    private void updateDataTypePanelEnable() {
        boolean enable = false;

        if (this.metaclass != null) {
            // only certain classes are typed
            enable = this.searchMgr.isTypedMetaClass(this.metaclass);
            cbxMetaclass.setToolTipText(getLabelProvider().getText(this.metaclass));
        }

        // set enablement and update business object
        setDataTypePanelEnabled(enable);
    }

    /**
     * Handler for when the not contains text pattern radio button is selected/deselected.
     * 
     * @since 6.0.0
     */
    void handleNotContainSelected() {
        // only validate when selected since that's what all the other buttons in the radio button group are doing
        if (this.btnNotContain.getSelection()) {
            updatePropertyCriteria();
        }
    }

    /**
     * Handler for when the selected property changes.
     * 
     * @since 6.0.0
     */
    void handlePropertySelected() {
        // if Object URI search make sure exact match is checked and others are disabled
        if (this.cbxProperties.getText().equals(MetadataSearch.OBJECT_URI_FEATURE)) {
            this.chkExactMatch.setSelection(true);
            this.btnContains.setEnabled(false);
            this.btnNotContain.setEnabled(false);
        } else {
            this.btnContains.setEnabled(true);
            this.btnNotContain.setEnabled(true);
        }

        updatePropertyCriteria();
    }

    /**
     * Handler for when the selected runtime type changes.
     * 
     * @since 6.0.0
     */
    void handleRuntimeTypeChanged() {
        this.runtimeType = this.cbxRuntimeType.getText();
        this.searchMgr.setRuntimeType(this.runtimeType);
        updateSearchState();
    }

    /**
     * Handler for when the runtime type radio button is selected/deselected.
     * 
     * @since 6.0.0
     */
    void handleRuntimeTypeSelected() {
        // only update controls and business object when selected since this is a radio button and
        // the other radio buttons in the group also only update when they're selected
        if (this.btnRuntimeType.getSelection()) {
            // disable
            this.txfSimpleType.setEnabled(false);
            this.chkIncludeSubtypes.setEnabled(false);
            this.btnBrowseSimpleType.setEnabled(false);

            // enable
            this.cbxRuntimeType.setEnabled(true);

            // synch business object
            this.searchMgr.setRuntimeType(this.runtimeType);
            this.searchMgr.setDatatype(null, false);

            // update search button status
            updateSearchState();
        }
    }

    /**
     * Handler for when the simple type changes.
     * 
     * @since 6.0.0
     */
    void handleSimpleTypeChanged() {
        this.searchMgr.setDatatype(this.simpleType, this.chkIncludeSubtypes.getSelection());
        updateSearchState();
    }

    /**
     * Handler for when the simple type radio button is selected/deselected.
     * 
     * @since 6.0.0
     */
    void handleSimpleTypeSelected() {
        // only update controls and business object when selected since this is a radio button and
        // the other radio buttons in the group also only update when they're selected
        if (this.btnSimpleType.getSelection()) {
            // disable
            this.cbxRuntimeType.setEnabled(false);

            // enable
            this.txfSimpleType.setEnabled(true);
            this.btnBrowseSimpleType.setEnabled(true);
            this.chkIncludeSubtypes.setEnabled(true);

            // synch business object
            this.searchMgr.setDatatype(this.simpleType, this.chkIncludeSubtypes.getSelection());
            this.searchMgr.setRuntimeType(null);

            // update search button status
            updateSearchState();
        }
    }

    /**
     * Handler for when the text pattern changes.
     * 
     * @since 6.0.0
     */
    void handleTextPatternModified() {
        updatePropertyCriteria();
    }

    /**
     * Select specified button and fire selection event to it's listeners. Need to do this because SWT events aren't fired at
     * construction.
     * 
     * @param theButton the button being selected
     */
    private void initButtonSelected( final Button theButton ) {
        initButtonSelected(theButton, true);
    }

    /**
     * Select or deselect specified button and fire selection event to it's listeners. Need to do this because events aren't
     * generated at construction.
     * 
     * @param theButton the button being selected or deselected
     */
    private void initButtonSelected( final Button theButton,
                                     final boolean theSelectFlag ) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                theButton.setSelection(theSelectFlag);
                Event event = new Event();
                event.widget = theButton;
                theButton.notifyListeners(SWT.Selection, event);
            }
        });
    }

    private void initializeDialogSettings() {
        // get general search dialog settings
        IDialogSettings tempSettings = UiPlugin.getDefault().getDialogSettings();

        // get this page's settings
        this.settings = tempSettings.getSection(getClass().getSimpleName());

        // if settings not found create them
        if (this.settings == null) {
            this.settings = tempSettings.addNewSection(getClass().getSimpleName());
        }
    }

    private boolean isSearchStateValid() {
        boolean result = false;
        boolean useMgr = true;

        // the search manager canExecute() method can't be used in all cases to determine if the
        // search criteria is valid.
        if (getControl() != null) {
            if (this.btnMetaclass.getSelection()) {
                if (this.metaclass == null) {
                    result = false;
                    useMgr = false;
                } else {
                    if (this.searchMgr.isTypedMetaClass(this.metaclass)) {
                        if (this.btnSimpleType.getSelection() && (this.simpleType == null)) {
                            result = false;
                            useMgr = false;
                        } else if (this.btnRuntimeType.getSelection() && (this.runtimeType == null)) {
                            result = false;
                            useMgr = false;
                        }
                    }
                }
            }
        }

        // use business object to determine if search is valid
        if (useMgr) {
            result = this.searchMgr.canExecute().isOK();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.ISearchPage#performAction()
     */
    @Override
    public boolean performAction() {
        saveState();
        NewSearchUI.runQueryInBackground(new MetadataSearchQuery(this.searchMgr));

        return true;
    }

    private void restoreState() {
        //
        // restore object type group
        //

        if (this.settings.getBoolean(DialogSettingsConstants.ANY_OBJ_TYPE)) {
            initButtonSelected(this.btnAnyObjectType);
        } else if (this.settings.getBoolean(DialogSettingsConstants.METACLASS_TYPE)) {
            initButtonSelected(this.btnMetaclass);
        } else {
            // first time using search page
            initButtonSelected(this.btnAnyObjectType);
        }

        // load metaclass MRU. these are metaclass URIs so need to use label provider to fill combo
        this.metaclassMru.clear();
        String[] mru = this.settings.getArray(DialogSettingsConstants.METACLASS_MRU);

        if ((mru != null) && (mru.length > 0)) {
            for (int i = 0; i < mru.length; i++) {
                this.metaclassMru.add(mru[i]);
                this.cbxMetaclass.add(getMetaclassText(mru[i]));
            }

            // last used metaclass
            String uri = this.settings.get(DialogSettingsConstants.LAST_USED_METACLASS);

            if ((uri != null) && (uri.length() > 0)) {
                int index = this.metaclassMru.indexOf(uri);

                if (index == -1) {
                    // then last used has gotten deleted from the MRU by the MRU_LIMIT
                    this.metaclassMru.add(uri);
                    index = 0;
                }

                EClass metaclass = getMetaclass(uri);

                if (metaclass != null) {
                    updateMetaclass(metaclass);
                }
            }
        }

        //
        // restore data type group
        //

        if (this.settings.getBoolean(DialogSettingsConstants.INCLUDE_SUBTYPES)) {
            initButtonSelected(this.chkIncludeSubtypes);
        } else if (this.settings.get(DialogSettingsConstants.INCLUDE_SUBTYPES) == null) {
            // first time using search page
            if (MetadataSearch.DEFAULT_INCLUDE_SUBTYPES) {
                initButtonSelected(this.chkIncludeSubtypes);
            }
        }

        // initialize selected runtime type
        String selectedRuntimeType = this.settings.get(DialogSettingsConstants.SELECTED_RUNTIME_TYPE);

        updateRuntimeType(selectedRuntimeType);

        // initialize data type radio buttons selection if necessary
        // select last saved property
        if (this.settings.getBoolean(DialogSettingsConstants.ANY_DATA_TYPE)) {
            initButtonSelected(this.btnAnyDataType);
        } else if (this.settings.getBoolean(DialogSettingsConstants.SIMPLE_DATA_TYPE)) {
            initButtonSelected(this.btnSimpleType);
        } else if (this.settings.getBoolean(DialogSettingsConstants.RUNTIME_TYPE)) {
            initButtonSelected(this.btnRuntimeType);
        } else {
            // first time using search page
            initButtonSelected(this.btnAnyDataType);
        }

        // simple type
        String uri = this.settings.get(DialogSettingsConstants.SELECTED_SIMPLE_TYPE);

        if ((uri != null) && (uri.length() > 0)) {
            try {
                EObject type = ModelUtilities.getWorkspaceContainer().getEObject(URI.createURI(uri), false);

                if ((type != null) && (type instanceof XSDSimpleTypeDefinition)) {
                    updateSimpleType((XSDSimpleTypeDefinition)type);
                } else {
                    this.settings.put(DialogSettingsConstants.SELECTED_SIMPLE_TYPE, (String)null);
                }
            } catch (CoreException theException) {
                Util.log(theException);
            }
        }

        // load text patterns if necessary
        String[] patterns = this.settings.getArray(DialogSettingsConstants.TEXT_PATTERN);

        if ((patterns != null) && (patterns.length > 0)) {
            this.cbxTextPattern.setItems(patterns);

            // select the last used pattern
            String txt = this.settings.get(DialogSettingsConstants.LAST_USED_TEXT_PATTERN);

            if ((txt != null) && (txt.length() > 0)) {
                this.cbxTextPattern.setText(txt);
            }
        }

        // initialize contains/not contains radio button selection based on dialog settings
        if (this.settings.getBoolean(DialogSettingsConstants.CONTAINS_TEXT)) {
            initButtonSelected(this.btnContains);
        } else if (this.settings.getBoolean(DialogSettingsConstants.NOT_CONTAIN_TEXT)) {
            initButtonSelected(this.btnNotContain);
        } else {
            // first time using search page
            initButtonSelected((MetadataSearch.DEFAULT_CONTAINS_PATTERN) ? this.btnContains : this.btnNotContain);
        }

        // initialize exact match checkbox based on dialog settings
        if (this.settings.getBoolean(DialogSettingsConstants.EXACT_MATCH)) {
            initButtonSelected(this.chkExactMatch);
        }

        // include property in search checkbox
        if (this.settings.getBoolean(DialogSettingsConstants.INCLUDE_PROPERTY)) {
            initButtonSelected(this.chkIncludeProperty);
        } else if (this.settings.get(DialogSettingsConstants.INCLUDE_PROPERTY) == null) {
            // first time using search page check include property
            initButtonSelected(this.chkIncludeProperty);
        } else {
            handleIncludePropertySelected();
        }

        // make sure there are properties to select. if not disable panel.
        if (this.cbxProperties.getItemCount() == 0) {
            this.chkIncludeProperty.setEnabled(false);
            handleIncludePropertySelected();
        } else {
            // select last saved property
            String selectedProperty = this.settings.get(DialogSettingsConstants.SELECTED_PROPERTY);

            if (selectedProperty != null) {
                int index = this.cbxProperties.indexOf(selectedProperty);

                if (index != -1) {
                    this.cbxProperties.select(index);

                    // handle the selection
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            handlePropertySelected();
                        }
                    });
                }
            }
        }
    }

    private void saveState() {
        this.settings.put(DialogSettingsConstants.ANY_OBJ_TYPE, this.btnAnyObjectType.getSelection());
        this.settings.put(DialogSettingsConstants.METACLASS_TYPE, this.btnMetaclass.getSelection());
        this.settings.put(DialogSettingsConstants.ANY_DATA_TYPE, this.btnAnyDataType.getSelection());
        this.settings.put(DialogSettingsConstants.SIMPLE_DATA_TYPE, this.btnSimpleType.getSelection());
        this.settings.put(DialogSettingsConstants.INCLUDE_SUBTYPES, this.chkIncludeSubtypes.getSelection());
        this.settings.put(DialogSettingsConstants.RUNTIME_TYPE, this.btnRuntimeType.getSelection());
        this.settings.put(DialogSettingsConstants.INCLUDE_PROPERTY, this.chkIncludeProperty.getSelection());
        this.settings.put(DialogSettingsConstants.CONTAINS_TEXT, this.btnContains.getSelection());
        this.settings.put(DialogSettingsConstants.NOT_CONTAIN_TEXT, this.btnNotContain.getSelection());
        this.settings.put(DialogSettingsConstants.EXACT_MATCH, this.chkExactMatch.getSelection());
        this.settings.put(DialogSettingsConstants.SELECTED_RUNTIME_TYPE, this.cbxRuntimeType.getText());
        this.settings.put(DialogSettingsConstants.SELECTED_PROPERTY, this.cbxProperties.getText());

        // save text pattern MRU
        WidgetUtil.saveSettings(settings,
                                DialogSettingsConstants.TEXT_PATTERN,
                                this.cbxTextPattern,
                                DialogSettingsConstants.MRU_LIMIT);
        // save last used text pattern
        settings.put(DialogSettingsConstants.LAST_USED_TEXT_PATTERN, this.cbxTextPattern.getText());

        // save metaclass MRU
        UiUtil.save(settings, DialogSettingsConstants.METACLASS_MRU, this.metaclassMru, DialogSettingsConstants.MRU_LIMIT);

        // save last used metaclass
        if (this.metaclass != null) {
            int index = this.cbxMetaclass.getSelectionIndex();

            if (index != -1) {
                settings.put(DialogSettingsConstants.LAST_USED_METACLASS, (String)this.metaclassMru.get(index));
            }
        }

        // save simple type
        if (this.simpleType != null) {
            settings.put(DialogSettingsConstants.SELECTED_SIMPLE_TYPE, this.simpleType.getURI());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.ISearchPage#setContainer(org.eclipse.search.ui.ISearchPageContainer)
     */
    @Override
    public void setContainer( ISearchPageContainer container ) {
        this.searchPageContainer = container;
        this.searchMgr.setModelScope(SearchPageUtil.getModelWorkspaceScope(container));
    }

    /**
     * Sets the enablement of all the UI controls contained within the data type group. Also updates the business object with
     * datatype information.
     * 
     * @param theEnableFlag the flag indicating the enablement state
     * @since 6.0.0
     */
    private void setDataTypePanelEnabled( boolean theEnableFlag ) {
        if (theEnableFlag) {
            this.btnAnyDataType.setEnabled(true);
            this.btnSimpleType.setEnabled(true);
            this.btnRuntimeType.setEnabled(true);

            // updates the business object
            if (this.btnAnyDataType.getSelection()) {
                handleAnyDataTypeSelected();
            } else if (this.btnSimpleType.getSelection()) {
                handleSimpleTypeSelected();
            } else if (this.btnRuntimeType.getSelection()) {
                handleRuntimeTypeSelected();
            }
        } else {
            this.btnAnyDataType.setEnabled(false);

            this.btnSimpleType.setEnabled(false);
            this.txfSimpleType.setEnabled(false);
            this.btnBrowseSimpleType.setEnabled(false);
            this.chkIncludeSubtypes.setEnabled(false);

            this.btnRuntimeType.setEnabled(false);
            this.cbxRuntimeType.setEnabled(false);

            // updates the business object
            this.searchMgr.setDatatype(null, false);
            this.searchMgr.setRuntimeType(null);
        }
    }

    private void updatePropertyCriteria() {
        if (this.chkIncludeProperty.getSelection() && this.chkIncludeProperty.getEnabled()) {
            String pattern = this.cbxTextPattern.getText().trim();

            // if not exact match then need to surround text with asterisks (unless already there)
            // and don't add asterisks if searching by Object ID
            if (!this.chkExactMatch.getSelection() && (pattern.length() > 0)
                && !this.cbxProperties.getText().equals(MetadataSearch.OBJECT_URI_FEATURE)) {

                // make sure pattern includes a leading wildcard char
                if (!pattern.startsWith(MetadataSearch.TEXT_PATTERN_ANY_STRING)
                    && !pattern.startsWith(MetadataSearch.TEXT_PATTERN_ANY_CHAR)) {
                    pattern = new StringBuffer().append(MetadataSearch.TEXT_PATTERN_ANY_STRING).append(pattern).toString();
                }

                // make sure pattern includes an ending wildcard char
                if (!pattern.endsWith(MetadataSearch.TEXT_PATTERN_ANY_STRING)
                    && !pattern.endsWith(MetadataSearch.TEXT_PATTERN_ANY_CHAR)) {
                    pattern = new StringBuffer().append(pattern).append(MetadataSearch.TEXT_PATTERN_ANY_STRING).toString();
                }
            }

            this.searchMgr.setFeatureCriteria(this.cbxProperties.getText(), pattern, this.btnContains.getSelection());
        } else {
            // not performing a property/feature-based search
            this.searchMgr.setFeatureCriteria(null, null, false);
        }

        updateSearchState();
    }

    /**
     * Updates fields and UI with the new simple type.
     * 
     * @param theNewType the new simple type
     * @since 6.0.0
     */
    private void updateMetaclass( EClass theNewMetaclass ) {
        this.metaclass = theNewMetaclass;
        this.searchMgr.setMetaClass(this.metaclass);

        // show only the name:
        String txt = metaclass.getName();
        int index = this.cbxMetaclass.indexOf(txt);

        if (index == -1) {
            this.metaclassMru.add(getMetaclassUri(theNewMetaclass));
            this.cbxMetaclass.add(txt);
            index = this.cbxMetaclass.indexOf(txt);
        }

        this.cbxMetaclass.select(index);
        this.cbxMetaclass.setToolTipText(getLabelProvider().getText(this.metaclass));

        updateSearchState();
    }

    /**
     * Update fields and UI with the new Runtime Type.
     * 
     * @param theNewType the new Runtime type
     * @since 6.0.0
     */
    private void updateRuntimeType( String selectedRuntimeType ) {
        if (selectedRuntimeType != null) {
            int index = this.cbxRuntimeType.indexOf(selectedRuntimeType);

            if (index != -1) {
                this.cbxRuntimeType.select(index);
            } else if (this.cbxRuntimeType.getItemCount() > 0) {
                this.cbxRuntimeType.select(0);
            }

        } else {

            /*
             * If the selectedRuntimeType variable is null, set cbxRuntimeType to the first value in the list.
             */
            if (this.cbxRuntimeType.getItemCount() > 0) {
                this.cbxRuntimeType.select(0);
            }
        }

        this.runtimeType = this.cbxRuntimeType.getText();
    }

    private void updateSearchState() {
        this.searchPageContainer.setPerformActionEnabled(isSearchStateValid());
    }

    /**
     * Updates fields and UI with the new simple type.
     * 
     * @param theNewType the new simple type
     * @since 6.0.0
     */
    private void updateSimpleType( XSDSimpleTypeDefinition theNewType ) {
        this.simpleType = theNewType;
        this.txfSimpleType.setText(ModelUtilities.getEMFLabelProvider().getText(this.simpleType));
        updateSearchState();
    }
}
