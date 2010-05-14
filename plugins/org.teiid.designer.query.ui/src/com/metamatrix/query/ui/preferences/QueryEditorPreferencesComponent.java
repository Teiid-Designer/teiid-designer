/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.ui.preferences;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.ui.internal.preferences.IEditorPreferencesComponent;
import com.metamatrix.ui.internal.preferences.IEditorPreferencesValidationListener;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * EditorPreferencePage
 */
public class QueryEditorPreferencesComponent implements IEditorPreferencesComponent, UiConstants {

    private Button startClausesOnNewLineButton;
    private Button indentClausesButton;
    private Text defaultStringLength;
    private Button showViewAndSourcesRadioButton;
    private Button showTreeLayoutRadioButton;
    private String name = UiPlugin.getDefault().getPluginUtil().getString("QueryEditorPreferencesComponent.name"); //$NON-NLS-1$
    private String defaultStringMessage = UiPlugin.getDefault().getPluginUtil().getString("QueryEditorPreferencesComponent.defaultStringLength"); //$NON-NLS-1$
    private String invalidNumberMessage = UiPlugin.getDefault().getPluginUtil().getString("QueryEditorPreferencesComponent.invalidNumber"); //$NON-NLS-1$

    private static int MAX_NUMBER = 255;
    private static int MIN_NUMBER = 1;
    private List validationListeners = new ArrayList();

    public QueryEditorPreferencesComponent() {
    }

    public String getName() {
        return name;
    }

    public String getTooltip() {
        return name;
    }

    public Composite createEditorPreferencesComponent( Composite parent ) {
        Label label = null;
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.numColumns = 1;
        comp.setLayout(layout);

        // ================================================
        // 1. SQL Clauses options
        // ================================================
        String clausesGroupHdr = Util.getString("EditorPreferencePage.sqlClauses"); //$NON-NLS-1$
        Group clausesGroup = WidgetFactory.createGroup(comp, clausesGroupHdr, GridData.FILL_HORIZONTAL, 1, 2);

        startClausesOnNewLineButton = new Button(clausesGroup, SWT.CHECK);
        String buttonText = Util.getString("EditorPreferencePage.startClauses"); //$NON-NLS-1$
        startClausesOnNewLineButton.setText(buttonText);
        startClausesOnNewLineButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                setButtonEnabling();
            }
        });
        indentClausesButton = new Button(clausesGroup, SWT.CHECK);
        buttonText = Util.getString("EditorPreferencePage.indentClause"); //$NON-NLS-1$
        indentClausesButton.setText(buttonText);
        indentClausesButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                setButtonEnabling();
            }
        });
        GridData buttonGridData = new GridData();
        buttonGridData.horizontalIndent = 6;
        indentClausesButton.setLayoutData(buttonGridData);

        // ================================================
        // 2. Transformation Diagram Layout
        // ================================================
        String diagramLayoutHeader = Util.getString("EditorPreferencePage.diagramLayout"); //$NON-NLS-1$
        Group grpDiagramLayout = WidgetFactory.createGroup(comp, diagramLayoutHeader, GridData.FILL_HORIZONTAL);

        showViewAndSourcesRadioButton = WidgetFactory.createRadioButton(grpDiagramLayout,
                                                                        Util.getString("EditorPreferencePage.viewAndSources")); //$NON-NLS-1$);
        showTreeLayoutRadioButton = WidgetFactory.createRadioButton(grpDiagramLayout,
                                                                    Util.getString("EditorPreferencePage.treeLayout")); //$NON-NLS-1$);

        // ================================================
        // 3. Miscellaneous
        // ================================================
        String miscHeader = Util.getString("EditorPreferencePage.miscellaneous"); //$NON-NLS-1$
        Group grpMisc = WidgetFactory.createGroup(comp, miscHeader, GridData.FILL_HORIZONTAL, 1, 2);

        label = new Label(grpMisc, SWT.NONE);
        label.setText(defaultStringMessage);
        defaultStringLength = new Text(grpMisc, SWT.BORDER);

        defaultStringLength.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

        setInitialButtonStates();

        return comp;
    }

    private void setInitialButtonStates() {
        boolean selected = getPreferenceStore().getBoolean(UiConstants.Prefs.START_CLAUSES_ON_NEW_LINE);
        startClausesOnNewLineButton.setSelection(selected);
        selected = getPreferenceStore().getBoolean(UiConstants.Prefs.INDENT_CLAUSE_CONTENT);
        indentClausesButton.setSelection(selected);
        defaultStringLength.setText(String.valueOf(ModelerCore.getTransformationPreferences().getDefaultStringLength()));
        boolean treeLayout = getPreferenceStore().getBoolean(UiConstants.Prefs.TREE_DIAGRAM_LAYOUT);
        showViewAndSourcesRadioButton.setSelection(!treeLayout);
        showTreeLayoutRadioButton.setSelection(treeLayout);
        setButtonEnabling();
    }

    public boolean performOk() {
        boolean changeMade = false;
        boolean isSelected = (startClausesOnNewLineButton.getEnabled() && startClausesOnNewLineButton.getSelection());
        boolean isSet = getPreferenceStore().getBoolean(UiConstants.Prefs.START_CLAUSES_ON_NEW_LINE);
        if (isSelected != isSet) {
            getPreferenceStore().setValue(UiConstants.Prefs.START_CLAUSES_ON_NEW_LINE, isSelected);
            changeMade = true;
        }
        isSelected = (indentClausesButton.getEnabled() && indentClausesButton.getSelection());
        isSet = getPreferenceStore().getBoolean(UiConstants.Prefs.INDENT_CLAUSE_CONTENT);
        if (isSelected != isSet) {
            getPreferenceStore().setValue(UiConstants.Prefs.INDENT_CLAUSE_CONTENT, isSelected);
            changeMade = true;
        }

        Integer val = new Integer(defaultStringLength.getText());
        if (val.intValue() != ModelerCore.getTransformationPreferences().getDefaultStringLength()) {
            ModelerCore.getTransformationPreferences().setDefaultStringLength(val.intValue());
            changeMade = true;
        }

        boolean isTreeLayout = getPreferenceStore().getBoolean(UiConstants.Prefs.TREE_DIAGRAM_LAYOUT);
        if (isTreeLayout != showTreeLayoutRadioButton.getSelection()) {
            changeMade = true;
            getPreferenceStore().setValue(UiConstants.Prefs.TREE_DIAGRAM_LAYOUT, showTreeLayoutRadioButton.getSelection());
        }

        if (changeMade) {
            UiPlugin.getDefault().savePluginPreferences();
        }
        return true;
    }

    public void performDefaults() {
        boolean select = getPreferenceStore().getDefaultBoolean(UiConstants.Prefs.START_CLAUSES_ON_NEW_LINE);
        startClausesOnNewLineButton.setSelection(select);
        select = getPreferenceStore().getDefaultBoolean(UiConstants.Prefs.INDENT_CLAUSE_CONTENT);
        indentClausesButton.setSelection(select);
        setButtonEnabling();
        validate();
    }

    void setButtonEnabling() {
        startClausesOnNewLineButton.setEnabled(true);
        indentClausesButton.setEnabled(startClausesOnNewLineButton.getSelection());
    }

    private IPreferenceStore getPreferenceStore() {
        return UiPlugin.getDefault().getPreferenceStore();
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.ui.internal.preferences.IEditorPreferencesComponent#addValidationListener(com.metamatrix.ui.internal.preferences.IEditorPreferencesValidationListener)
     */
    public void addValidationListener( IEditorPreferencesValidationListener listener ) {
        this.validationListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.ui.internal.preferences.IEditorPreferencesComponent#removeValidationListener(com.metamatrix.ui.internal.preferences.IEditorPreferencesValidationListener)
     */
    public void removeValidationListener( IEditorPreferencesValidationListener listener ) {
        this.validationListeners.remove(listener);
    }

    public void fireValidationStatus( boolean validationStatus,
                                      String message ) {
        for (int i = 0; i < this.validationListeners.size(); i++) {
            ((IEditorPreferencesValidationListener)this.validationListeners.get(i)).validationStatus(validationStatus, message);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.preferences.IEditorPreferencesComponent#validate()
     */
    public void validate() {
        try {
            Integer val = new Integer(defaultStringLength.getText());
            if ((val.intValue() >= MIN_NUMBER && val.intValue() <= MAX_NUMBER)) {
                fireValidationStatus(true, null);
                return;
            }
        } catch (NumberFormatException e) {
            // Expected
        }

        fireValidationStatus(false, invalidNumberMessage);
    }
}
