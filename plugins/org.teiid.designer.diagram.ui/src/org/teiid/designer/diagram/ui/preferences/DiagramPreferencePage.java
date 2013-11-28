/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.preferences;

import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.teiid.designer.diagram.ui.DiagramNotationManager;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.PluginConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * This class represents the preference page for setting the Modeler Diagram Preferences.
 *
 * @since 8.0
 */
public class DiagramPreferencePage extends PreferencePage
    implements DiagramUiConstants, PluginConstants, IWorkbenchPreferencePage {

    // ////////////////////////////////////////////////////////////////////
    // Static variables
    // ////////////////////////////////////////////////////////////////////
    // Property File Keys
    private static final String KEY_PAGE_DESCRIPTION = "DiagramPrefPage.description";//$NON-NLS-1$

    // ////////////////////////////////////////////////////////////////////
    // Instance variables
    // ////////////////////////////////////////////////////////////////////
    private NotationIDAndName[] notations;
    private String[] routers;
    private Combo notationCombo;
    private Combo routerCombo;
    private AppearanceProcessor appearanceProcessor;
    private Button fkShowNameButton;
    private Button fkShowMultiplicityButton;

    private boolean wasCancelled = false;
    private boolean wasOKed = false;

    Text modelSizeTextField;
    String modelSizeError;

    // ////////////////////////////////////////////////////////////////////
    // Constructors
    // ////////////////////////////////////////////////////////////////////
    public DiagramPreferencePage() {
        super();
        setPreferenceStore(DiagramUiPlugin.getDefault().getPreferenceStore());
        setDescription(Util.getString(KEY_PAGE_DESCRIPTION));
    }

    // ////////////////////////////////////////////////////////////////////
    // Instance methods
    // ////////////////////////////////////////////////////////////////////
    @Override
	public void init( IWorkbench workbench ) {
    }

    @Override
    public Control createContents( Composite parent ) {
        //        System.out.println("[DiagramPreferencePage.createContents] TOP " );   //$NON-NLS-1$

        Composite comp = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        comp.setLayout(layout);

        // -----------------------------------------
        // Diagram UML Notation Options
        // -----------------------------------------
        String notationsHdr = Util.getString("DiagramPrefPage.notations"); //$NON-NLS-1$
        Group notationsGroup = WidgetFactory.createGroup(comp, notationsHdr, GridData.FILL_HORIZONTAL, 1, 2);

        Label notationLabel = new Label(notationsGroup, SWT.NONE);
        String notationStr = Util.getString("DiagramPrefPage.defaultNotation"); //$NON-NLS-1$
        notationLabel.setText(notationStr);
        notationCombo = new Combo(notationsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData notationComboGridData = new GridData(GridData.FILL_HORIZONTAL);
        notationComboGridData.horizontalIndent = 10;
        notationCombo.setLayoutData(notationComboGridData);
        notations = getNotations();
        for (int i = 0; i < notations.length; i++) {
            notationCombo.add(notations[i].getDisplayName());
        }
        selectCurrentNotation();

        // -----------------------------------------
        // Diagram Link Routing options
        // -----------------------------------------
        String routersHdr = Util.getString("DiagramPrefPage.routers"); //$NON-NLS-1$
        Group routersGroup = WidgetFactory.createGroup(comp, routersHdr, GridData.FILL_HORIZONTAL, 1, 2);
        routersGroup.setText(routersHdr);
        GridData routersGroupGridData = new GridData(GridData.FILL_HORIZONTAL);
        routersGroup.setLayoutData(routersGroupGridData);

        Label routerLabel = new Label(routersGroup, SWT.NONE);
        String routerStr = Util.getString("DiagramPrefPage.defaultRouter"); //$NON-NLS-1$
        routerLabel.setText(routerStr);
        routerCombo = new Combo(routersGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData routerComboGridData = new GridData(GridData.FILL_HORIZONTAL);
        routerComboGridData.horizontalIndent = 10;
        routerCombo.setLayoutData(routerComboGridData);
        routers = DiagramUiConstants.LinkRouter.types;
        for (int i = 0; i < routers.length; i++) {
            routerCombo.add(routers[i]);
        }
        selectCurrentRouter();

        // -----------------------------------------
        // Diagram Appearance options (colors, fonts, etc..)
        // -----------------------------------------
        appearanceProcessor = new AppearanceProcessor(getPreferenceStore(), getShell());
        Control appearanceControl = appearanceProcessor.createContents(comp);
        GridData appearanceControlGridData = new GridData(GridData.FILL_HORIZONTAL);
        appearanceControl.setLayoutData(appearanceControlGridData);

        String modelSizeHeader = Util.getString("DiagramPrefPage.largeModelSizeHeader"); //$NON-NLS-1$
        Group modelSizeGroup = WidgetFactory.createGroup(comp, modelSizeHeader, GridData.FILL_HORIZONTAL, 1, 2);

        modelSizeError = Util.getString("DiagramPrefPage.largeModelSizeError"); //$NON-NLS-1$
        String modelSizeButtonStr = Util.getString("DiagramPrefPage.largeModelSize"); //$NON-NLS-1$
        WidgetFactory.createLabel(modelSizeGroup, modelSizeButtonStr);
        modelSizeTextField = WidgetFactory.createTextField(modelSizeGroup, GridData.FILL_HORIZONTAL);
        modelSizeTextField.addModifyListener(new SizeModifyListener());

        String largeModelSizeStr = getPreferenceStore().getString(PluginConstants.Prefs.LARGE_MODEL_SIZE);
        modelSizeTextField.setText(largeModelSizeStr);

        // -----------------------------------------
        // FK Links Display options Defect 22147 requested by Bob
        // -----------------------------------------

        Group fkRelationshipsGroup = WidgetFactory.createGroup(comp, Util.getString("DiagramPrefPage.relationshipOptionsLabel"), //$NON-NLS-1$
                                                               GridData.FILL_HORIZONTAL);

        fkShowNameButton = new Button(fkRelationshipsGroup, SWT.CHECK);
        fkShowNameButton.setText(Util.getString("DiagramPrefPage.showRoleNamesLabel")); //$NON-NLS-1$
        boolean showName = getPreferenceStore().getBoolean(PluginConstants.Prefs.SHOW_FK_NAME);
        fkShowNameButton.setSelection(showName);

        fkShowMultiplicityButton = new Button(fkRelationshipsGroup, SWT.CHECK);
        fkShowMultiplicityButton.setText(Util.getString("DiagramPrefPage.showMultiplicityLabel")); //$NON-NLS-1$
        boolean showMulti = getPreferenceStore().getBoolean(PluginConstants.Prefs.SHOW_FK_MULTIPLICITY);
        fkShowMultiplicityButton.setSelection(showMulti);

        return comp;
    }

    private NotationIDAndName[] getNotations() {
        DiagramNotationManager mgr = DiagramUiPlugin.getDiagramNotationManager();
        List<NotationIDAndName> notationList = mgr.getDiagramNotationInfo();
        NotationIDAndName[] notationArray = new NotationIDAndName[notationList.size()];
        Iterator<NotationIDAndName> it = notationList.iterator();
        for (int i = 0; it.hasNext(); i++) {
            notationArray[i] = it.next();
        }
        return notationArray;
    }

    private void selectCurrentNotation() {
        String notationPrefName = PluginConstants.Prefs.DIAGRAM_NOTATION;
        String notation = getPreferenceStore().getString(notationPrefName);
        selectNotation(notation);
    }

    private void selectDefaultNotation() {
        String notationPrefName = PluginConstants.Prefs.DIAGRAM_NOTATION;
        String notationDefault = getPreferenceStore().getDefaultString(notationPrefName);
        selectNotation(notationDefault);
    }

    private void selectNotation( String notation ) {
        boolean selected = false;
        int i = 0;
        while ((!selected) && (i < notations.length)) {
            String thisNotation = notations[i].getID();
            if (thisNotation.equals(notation)) {
                notationCombo.select(i);
                selected = true;
            } else {
                i++;
            }
        }
    }

    private void selectCurrentRouter() {
        String prefName = PluginConstants.Prefs.DIAGRAM_ROUTER_STYLE;
        String router = getPreferenceStore().getString(prefName);
        selectRouter(router);
    }

    private void selectDefaultRouter() {
        String prefName = PluginConstants.Prefs.DIAGRAM_ROUTER_STYLE;
        String router = getPreferenceStore().getDefaultString(prefName);
        selectRouter(router);
    }

    private void selectRouter( String router ) {
        boolean selected = false;
        int i = 0;
        while ((!selected) && (i < routers.length)) {
            String thisRouter = routers[i];
            if (thisRouter.equals(router)) {
                routerCombo.select(i);
                selected = true;
            } else {
                i++;
            }
        }
    }

    private void selectDefaultShowFKValues() {
        boolean defShowName = getPreferenceStore().getDefaultBoolean(PluginConstants.Prefs.SHOW_FK_NAME);
        fkShowNameButton.setSelection(defShowName);
        boolean defShowMulti = getPreferenceStore().getDefaultBoolean(PluginConstants.Prefs.SHOW_FK_MULTIPLICITY);
        fkShowMultiplicityButton.setSelection(defShowMulti);
    }

    @Override
    public boolean performOk() {
        boolean savePrefs = false;
        int selectedIndex = notationCombo.getSelectionIndex();
        if (selectedIndex >= 0) {
            String id = notations[selectedIndex].getID();
            String currentStoredID = getPreferenceStore().getString(PluginConstants.Prefs.DIAGRAM_NOTATION);
            if (!id.equals(currentStoredID)) {
                savePrefs = true;
                getPreferenceStore().setValue(PluginConstants.Prefs.DIAGRAM_NOTATION, id);
            }
        }
        selectedIndex = routerCombo.getSelectionIndex();
        if (selectedIndex >= 0) {
            String id = routers[selectedIndex];
            String currentStoredID = getPreferenceStore().getString(PluginConstants.Prefs.DIAGRAM_ROUTER_STYLE);
            if (!id.equals(currentStoredID)) {
                savePrefs = true;
                getPreferenceStore().setValue(PluginConstants.Prefs.DIAGRAM_ROUTER_STYLE, id);
                // DiagramUiPlugin.getDefault().savePluginPreferences();
            }
        }
        String oldLargeModelSize = getPreferenceStore().getString(PluginConstants.Prefs.LARGE_MODEL_SIZE);
        String currentLargeModelSize = modelSizeTextField.getText();
        if (!oldLargeModelSize.equals(currentLargeModelSize)) {
            savePrefs = true;
            getPreferenceStore().setValue(PluginConstants.Prefs.LARGE_MODEL_SIZE, currentLargeModelSize);
            // DiagramUiPlugin.getDefault().savePluginPreferences();
        }

        boolean appearanceOk = appearanceProcessor.performOk();
        if (!savePrefs) {
            savePrefs = appearanceOk;
        }

        // update FK display values
        boolean currentShowName = getPreferenceStore().getBoolean(PluginConstants.Prefs.SHOW_FK_NAME);
        if (currentShowName != fkShowNameButton.getSelection()) {
            savePrefs = true;
            boolean newValue = fkShowNameButton.getSelection();
            getPreferenceStore().setValue(PluginConstants.Prefs.SHOW_FK_NAME, newValue);
        }
        boolean currentShowMulti = getPreferenceStore().getBoolean(PluginConstants.Prefs.SHOW_FK_MULTIPLICITY);
        if (currentShowMulti != fkShowMultiplicityButton.getSelection()) {
            boolean newValue = fkShowMultiplicityButton.getSelection();
            getPreferenceStore().setValue(PluginConstants.Prefs.SHOW_FK_MULTIPLICITY, newValue);
            savePrefs = true;
        }
        if (savePrefs) {
            DiagramUiPlugin.getDefault().savePreferences();
            DiagramUiPlugin.updateEditorForPreferences();
        }
        wasOKed = true;

        return true;
    }

    @Override
    public void performDefaults() {
        selectDefaultNotation();
        selectDefaultRouter();
        appearanceProcessor.performDefaults();
        modelSizeTextField.setText(String.valueOf(getPreferenceStore().getDefaultInt(PluginConstants.Prefs.LARGE_MODEL_SIZE)));
        selectDefaultShowFKValues();
    }

    @Override
    public void dispose() {
        // Need to check the case where the user "imported" preferences??
        if (!wasCancelled && !wasOKed) DiagramUiPlugin.updateEditorForPreferences();
        super.dispose();
    }

    @Override
    public boolean performCancel() {
        wasCancelled = true;
        return super.performCancel();
    }

    class SizeModifyListener implements ModifyListener {
        @Override
		public void modifyText( ModifyEvent e ) {
            try {
                Integer.parseInt(modelSizeTextField.getText());
                DiagramPreferencePage.this.setValid(true);
                DiagramPreferencePage.this.setErrorMessage(null);
            } catch (Exception ex) {
                DiagramPreferencePage.this.setErrorMessage(modelSizeError);
                DiagramPreferencePage.this.setValid(false);
            }
        }
    }

}
