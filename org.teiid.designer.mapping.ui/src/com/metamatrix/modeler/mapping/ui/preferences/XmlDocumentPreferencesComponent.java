/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.preferences;

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
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.ui.internal.preferences.IEditorPreferencesComponent;
import com.metamatrix.ui.internal.preferences.IEditorPreferencesValidationListener;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * EditorPreferencePage
 */
public class XmlDocumentPreferencesComponent implements IEditorPreferencesComponent, UiConstants, PluginConstants {

    private Text upperRecursionLimit;
    private Button removeDuplicateAttributesButton;
    private String name = UiPlugin.getDefault().getPluginUtil().getString("XmlDocumentPreferencesComponent.name"); //$NON-NLS-1$
    private String upperRecurionLimitMessage = UiPlugin.getDefault().getPluginUtil().getString("XmlDocumentPreferencesComponent.upperRecursionLimit"); //$NON-NLS-1$
    private String removeDuplicateAttributesMessage = UiPlugin.getDefault().getPluginUtil().getString("XmlDocumentPreferencesComponent.removeDuplicateAttributes"); //$NON-NLS-1$
    String sNumericErrorMsg = Util.getString("XmlDocumentPreferencesComponent.numericErrorMsg"); //$NON-NLS-1$

    private List validationListeners = new ArrayList();

    private Text txfAutoExpandMappingClassMax;
    private Text txfAutoExpandTreeTargetLevel;
    private Button btnDisplayMappingClassesFolded;

    private boolean changeMade;
    private boolean isSelected;

    public String getName() {
        return name;
    }

    public String getTooltip() {
        return name;
    }

    public Composite createEditorPreferencesComponent( Composite parent ) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.numColumns = 1;
        comp.setLayout(layout);

        // ========================================
        // Mapping Diagram Tree Settings
        // ========================================
        String mappingTreeHeader = Util.getString("XmlDocumentPreferencesComponent.mappingDiagramTreePrefsHeader"); //$NON-NLS-1$
        Group mappingTreeGroup = WidgetFactory.createGroup(comp, mappingTreeHeader, GridData.FILL_HORIZONTAL, 1, 3);

        /*
         * setting: auto expand mapping class maximum
         */
        String sAutoExpandMappingClassMaxLabel1 = Util.getString("XmlDocumentPreferencesComponent.autoExpandMappingClassMaxLabel1"); //$NON-NLS-1$
        WidgetFactory.createLabel(mappingTreeGroup, sAutoExpandMappingClassMaxLabel1);

        txfAutoExpandMappingClassMax = WidgetFactory.createTextField(mappingTreeGroup);
        GridData gd1 = new GridData();
        gd1.widthHint = 40;
        txfAutoExpandMappingClassMax.setLayoutData(gd1);

        String sAutoExpandMappingClassMaxLabel2 = Util.getString("XmlDocumentPreferencesComponent.autoExpandMappingClassMaxLabel2"); //$NON-NLS-1$
        WidgetFactory.createLabel(mappingTreeGroup, sAutoExpandMappingClassMaxLabel2);

        String sAutoExpandMappingClassMaxValue = "0"; //$NON-NLS-1$ 
        // get the current value
        sAutoExpandMappingClassMaxValue = getPreferenceStore().getString(PluginConstants.Prefs.AUTO_EXPAND_MAX_MAPPING_CLASSES);

        if (sAutoExpandMappingClassMaxValue.trim().equals("")) { //$NON-NLS-1$
            // if no current value, get the default
            sAutoExpandMappingClassMaxValue = getPreferenceStore().getDefaultString(PluginConstants.Prefs.AUTO_EXPAND_MAX_MAPPING_CLASSES);
        }
        txfAutoExpandMappingClassMax.setText(sAutoExpandMappingClassMaxValue);
        txfAutoExpandMappingClassMax.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

        /*
         * setting: auto expand target tree level
         */
        String sAutoExpandTreeTargetLevelLabel = Util.getString("XmlDocumentPreferencesComponent.autoExpandTreeTargetLevelLabel"); //$NON-NLS-1$
        WidgetFactory.createLabel(mappingTreeGroup, sAutoExpandTreeTargetLevelLabel);

        txfAutoExpandTreeTargetLevel = WidgetFactory.createTextField(mappingTreeGroup);
        GridData gd2 = new GridData();
        gd2.widthHint = 40;
        gd2.horizontalSpan = 2;
        txfAutoExpandTreeTargetLevel.setLayoutData(gd2);

        String sAutoExpandTreeTargetLevelValue = "0"; //$NON-NLS-1$ 
        // get the current value
        sAutoExpandTreeTargetLevelValue = getPreferenceStore().getString(PluginConstants.Prefs.AUTO_EXPAND_TARGET_LEVEL);

        if (sAutoExpandTreeTargetLevelValue.trim().equals("")) { //$NON-NLS-1$
            // if no current value, get the default
            sAutoExpandTreeTargetLevelValue = getPreferenceStore().getDefaultString(PluginConstants.Prefs.AUTO_EXPAND_TARGET_LEVEL);
        }

        txfAutoExpandTreeTargetLevel.setText(sAutoExpandTreeTargetLevelValue);
        txfAutoExpandTreeTargetLevel.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

        /*
         * setting: display Mapping Classes Folded checkbox
         */

        btnDisplayMappingClassesFolded = new Button(mappingTreeGroup, SWT.CHECK);
        String sDisplayMappingClassesFoldedLabel = Util.getString("XmlDocumentPreferencesComponent.displayMappingClassesFoldedLabel"); //$NON-NLS-1$
        btnDisplayMappingClassesFolded.setText(sDisplayMappingClassesFoldedLabel);

        // get the current value
        boolean bool = getPreferenceStore().getBoolean(PluginConstants.Prefs.FOLD_MAPPING_CLASSES_BY_DEFAULT);
        btnDisplayMappingClassesFolded.setSelection(bool);

        createOtherSettingsControls(comp);

        return comp;
    }

    private void createOtherSettingsControls( Composite parent ) {
        String mappingGroupHdr = Util.getString("XmlDocumentPreferencesComponent.xmlMappingGroupLabel"); //$NON-NLS-1$
        Group mappingGroup = WidgetFactory.createGroup(parent, mappingGroupHdr, GridData.FILL_HORIZONTAL, 1, 2);

        Label label = new Label(mappingGroup, SWT.NONE);
        label.setText(upperRecurionLimitMessage);
        upperRecursionLimit = new Text(mappingGroup, SWT.BORDER);
        upperRecursionLimit.setText("111"); //$NON-NLS-1$

        upperRecursionLimit.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

        removeDuplicateAttributesButton = new Button(mappingGroup, SWT.CHECK);
        removeDuplicateAttributesButton.setText(removeDuplicateAttributesMessage);
        removeDuplicateAttributesButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                setButtonEnabling();
            }
        });

        setInitialButtonStates();

    }

    private void setInitialButtonStates() {
        upperRecursionLimit.setText(String.valueOf(ModelerCore.getTransformationPreferences().getUpperRecursionLimit()));
        removeDuplicateAttributesButton.setSelection(ModelerCore.getTransformationPreferences().getRemoveDuplicateAttibutes());
        setButtonEnabling();
    }

    public boolean performOk() {

        // ========================================
        // Mapping Diagram Tree Settings
        // ========================================
        String sOldAutoExpandMappingClassMax = getPreferenceStore().getString(PluginConstants.Prefs.AUTO_EXPAND_MAX_MAPPING_CLASSES);

        String sNewAutoExpandMappingClassMax = txfAutoExpandMappingClassMax.getText();
        if (!sOldAutoExpandMappingClassMax.equals(sNewAutoExpandMappingClassMax)) {
            getPreferenceStore().setValue(PluginConstants.Prefs.AUTO_EXPAND_MAX_MAPPING_CLASSES, sNewAutoExpandMappingClassMax);
            UiPlugin.getDefault().savePluginPreferences();
        }

        String sOldAutoExpandTreeTargetLevel = getPreferenceStore().getString(PluginConstants.Prefs.AUTO_EXPAND_TARGET_LEVEL);

        String sNewAutoExpandTreeTargetLevel = txfAutoExpandTreeTargetLevel.getText();
        if (!sOldAutoExpandTreeTargetLevel.equals(sNewAutoExpandTreeTargetLevel)) {
            getPreferenceStore().setValue(PluginConstants.Prefs.AUTO_EXPAND_TARGET_LEVEL, sNewAutoExpandTreeTargetLevel);
            UiPlugin.getDefault().savePluginPreferences();

        }

        getPreferenceStore().setValue(PluginConstants.Prefs.FOLD_MAPPING_CLASSES_BY_DEFAULT,
                                      btnDisplayMappingClassesFolded.getSelection());

        isSelected = (removeDuplicateAttributesButton.getEnabled() && removeDuplicateAttributesButton.getSelection());
        if (isSelected != ModelerCore.getTransformationPreferences().getRemoveDuplicateAttibutes()) {
            ModelerCore.getTransformationPreferences().setRemoveDuplicateAttibutes(isSelected);

            changeMade = true;
        }

        Integer val = new Integer(upperRecursionLimit.getText());
        if (val.intValue() != ModelerCore.getTransformationPreferences().getUpperRecursionLimit()) {
            ModelerCore.getTransformationPreferences().setUpperRecursionLimit(val.intValue());
            changeMade = true;
        }

        if (changeMade) {
            UiPlugin.getDefault().savePluginPreferences();
        }

        return true;

    }

    public void performDefaults() {

        String sAutoExpandMappingClassMaxValue = getPreferenceStore().getDefaultString(PluginConstants.Prefs.AUTO_EXPAND_MAX_MAPPING_CLASSES);
        txfAutoExpandMappingClassMax.setText(sAutoExpandMappingClassMaxValue);

        String sAutoExpandTreeTargetLevelValue = getPreferenceStore().getDefaultString(PluginConstants.Prefs.AUTO_EXPAND_TARGET_LEVEL);
        txfAutoExpandTreeTargetLevel.setText(sAutoExpandTreeTargetLevelValue);

        boolean bool = getPreferenceStore().getDefaultBoolean(PluginConstants.Prefs.FOLD_MAPPING_CLASSES_BY_DEFAULT);
        btnDisplayMappingClassesFolded.setSelection(bool);
    }

    void setButtonEnabling() {
    }

    private IPreferenceStore getPreferenceStore() {
        return UiPlugin.getDefault().getPreferenceStore();
    }

    public void addValidationListener( IEditorPreferencesValidationListener listener ) {
        this.validationListeners.add(listener);
    }

    public void removeValidationListener( IEditorPreferencesValidationListener listener ) {
        this.validationListeners.remove(listener);
    }

    public void fireValidationStatus( boolean validationStatus,
                                      String message ) {
        for (int i = 0; i < this.validationListeners.size(); i++) {
            ((IEditorPreferencesValidationListener)this.validationListeners.get(i)).validationStatus(validationStatus, message);
        }
    }

    /**
     * @see com.metamatrix.ui.internal.preferences.IEditorPreferencesComponent#validate()
     */
    public void validate() {
        try {
            new Integer(txfAutoExpandTreeTargetLevel.getText());
            new Integer(txfAutoExpandMappingClassMax.getText());
            new Integer(upperRecursionLimit.getText());

            // if we do not get a format error:
            fireValidationStatus(true, null);
            return;

        } catch (NumberFormatException e) {
            // Expected
        }
        fireValidationStatus(false, sNumericErrorMsg);
    }
}
