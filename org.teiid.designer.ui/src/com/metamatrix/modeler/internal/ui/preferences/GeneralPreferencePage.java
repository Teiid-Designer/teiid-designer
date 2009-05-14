/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.preferences;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * This class represents the preference page for setting general preferences.
 */
public final class GeneralPreferencePage extends PreferencePage
    implements IWorkbenchPreferencePage, UiConstants, UiConstants.ExtensionPoints {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(GeneralPreferencePage.class);

    private static final String DEFAULT_CATEGORY_ID = "generalPreference.defaultCategory.id"; //$NON-NLS-1$

    private static final String DEFAULT_CATEGORY_NAME = Util.getStringOrKey(PREFIX + "defaultCategory.name"); //$NON-NLS-1$

    private static final PreferenceCategory DEFAULT_CATEGORY;

    static {
        DEFAULT_CATEGORY = new PreferenceCategory();
        DEFAULT_CATEGORY.id = DEFAULT_CATEGORY_ID;
        DEFAULT_CATEGORY.name = DEFAULT_CATEGORY_NAME;
    }

    /**
     * key=category id, value=PreferenceCategory
     * 
     * @since 5.0
     */
    private Map categoryMap;

    /**
     * key=preference id, value=GeneralPreference
     * 
     * @since 5.0
     */
    private Map preferenceMap;

    public GeneralPreferencePage() {
        super();
        setDescription(Util.getStringOrKey(PREFIX + "description")); //$NON-NLS-1$

        // initialized fields
        this.categoryMap = new HashMap();
        this.preferenceMap = new TreeMap();

        processExtensions();
    }

    private Group constructGroup( Composite theParent,
                                  String theName ) {
        Group result = WidgetFactory.createGroup(theParent, theName, GridData.FILL_HORIZONTAL);
        result.setLayout(new GridLayout());
        result.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        result.setText(theName);

        return result;
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     * @since 5.0
     */
    @Override
    protected Control createContents( Composite theParent ) {
        Composite pnlMain = new Composite(theParent, SWT.NONE);
        pnlMain.setLayout(new GridLayout());
        pnlMain.setLayoutData(new GridData(GridData.FILL_BOTH));

        createExtensionContents(pnlMain);

        // add import/export buttons if supported by the product
        if (UiPlugin.getDefault().isProductContextSupported(IModelerProductContexts.PreferencePages.ID_IMPORT_EXPORT)) {
            createImportExportContents(pnlMain);
        }

        return theParent;
    }

    /**
     * Constructs UI components for importing and exporting preferences.
     * 
     * @param theParent the UI parent container
     * @since 5.0
     */
    private void createImportExportContents( Composite theParent ) {
        Composite pnlImportExport = new Composite(theParent, SWT.NONE);
        pnlImportExport.setLayout(new GridLayout(2, false));
        pnlImportExport.setLayoutData(new GridData());

        Button btn = new Button(pnlImportExport, SWT.PUSH);
        btn.setText(Util.getStringOrKey(PREFIX + "btnImport.text") + InternalUiConstants.Widgets.BROWSE_BUTTON); //$NON-NLS-1$
        btn.setToolTipText(Util.getStringOrKey(PREFIX + "btnImport.toolTip")); //$NON-NLS-1$
        btn.setLayoutData(new GridData());
        btn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleImportSelected();
            }
        });

        btn = new Button(pnlImportExport, SWT.PUSH);
        btn.setText(Util.getStringOrKey(PREFIX + "btnExport.text") + InternalUiConstants.Widgets.BROWSE_BUTTON); //$NON-NLS-1$
        btn.setToolTipText(Util.getStringOrKey(PREFIX + "btnExport.toolTip")); //$NON-NLS-1$
        btn.setLayoutData(new GridData());
        btn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleExportSelected();
            }
        });
    }

    /**
     * Constructs UI components for the contributed preferences.
     * 
     * @param theParent the UI parent container
     * @since 5.0
     */
    private void createExtensionContents( Composite theParent ) {
        Map groupMap = new TreeMap();

        // add prefs to categories
        Iterator itr = this.preferenceMap.values().iterator();

        while (itr.hasNext()) {
            GeneralPreference pref = (GeneralPreference)itr.next();
            Group group = (Group)groupMap.get(pref.categoryId);

            // if group hasn't been constructed yet, then construct it
            if (group == null) {
                PreferenceCategory category = (PreferenceCategory)this.categoryMap.get(pref.categoryId);

                // make sure a category for that id exists
                if (category == null) {
                    // put it in the default group
                    category = DEFAULT_CATEGORY;
                    group = (Group)groupMap.get(category.id);

                    // log that category not found
                    Object[] params = new Object[] {pref.categoryId, pref.id};
                    Util.log(IStatus.ERROR, Util.getString(PREFIX + "invalidCategoryId", params)); //$NON-NLS-1$
                }

                // create group for valid category
                if (group == null) {
                    group = constructGroup(theParent, category.name);
                }

                groupMap.put(category.id, group);
            }

            try {
                pref.contributor.createPreferenceEditor(group);
            } catch (Exception theException) {
                Util.log(IStatus.ERROR, Util.getString(PREFIX + "preferenceEditorError", //$NON-NLS-1$
                                                       pref.contributor.getClass().getName()));
            }
        }
    }

    /**
     * Handles the exporting of all preferences.
     * 
     * @since 5.0
     */
    void handleExportSelected() {
        // display chooser
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell());
        dialog.setText(Util.getStringOrKey(PREFIX + "exportPrefsChooser.title")); //$NON-NLS-1$
        dialog.setFilterExtensions(PreferencesUtils.PREFERENCE_DIALOG_FILTER_EXTENSIONS);

        // display dialog and process results
        if (dialog.open() != null) {
            String name = dialog.getFileName();
            String directory = dialog.getFilterPath();
            String path = new StringBuffer().append(directory).append(File.separatorChar).append(name).toString();
            path = PreferencesUtils.ensurePathExtension(path);
            boolean overwrite = true;

            // if file already exists ask user if they want to overwrite
            if (new File(path).exists()) {
                overwrite = MessageDialog.openConfirm(getShell(), Util.getStringOrKey(PREFIX
                                                                                      + "exportPrefsConfirmOverwrite.title"), //$NON-NLS-1$
                                                      Util.getStringOrKey(PREFIX + "exportPrefsConfirmOverwrite.msg")); //$NON-NLS-1$
            }

            if (overwrite) {
                try {
                    PreferencesUtils.exportAll(path, overwrite);
                } catch (Exception theException) {
                    Util.log(IStatus.ERROR, theException, theException.getMessage());
                    MessageDialog.openError(getControl().getShell(), Util.getStringOrKey(PREFIX + "exportPrefsProblem.title"), //$NON-NLS-1$
                                            theException.getLocalizedMessage());
                }
            }
        }
    }

    /**
     * Handles the importing of preferences from a file on the file system.
     * 
     * @since 5.0
     */
    void handleImportSelected() {
        // display chooser
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell());
        dialog.setText(Util.getStringOrKey(PREFIX + "importPrefsChooser.title")); //$NON-NLS-1$
        dialog.setFilterExtensions(PreferencesUtils.PREFERENCE_DIALOG_FILTER_EXTENSIONS);

        // display dialog and process results
        if (dialog.open() != null) {
            String name = dialog.getFileName();
            String directory = dialog.getFilterPath();
            String path = new StringBuffer().append(directory).append(File.separatorChar).append(name).toString();

            try {
                PreferencesUtils.importAll(path);
                refresh();
            } catch (Exception theException) {
                Util.log(IStatus.ERROR, theException, theException.getMessage());
                MessageDialog.openError(getControl().getShell(), Util.getStringOrKey(PREFIX + "importPrefsProblem.title"), //$NON-NLS-1$
                                        theException.getLocalizedMessage());
            }
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     * @since 5.0
     */
    public void init( IWorkbench theWorkbench ) {
        // pass the workbench to the contributors
        Iterator itr = this.preferenceMap.values().iterator();

        while (itr.hasNext()) {
            GeneralPreference pref = (GeneralPreference)itr.next();

            try {
                pref.contributor.setWorkbench(theWorkbench);
            } catch (Exception theException) {
                Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "unexpectedErrorSettingWorkbench", pref.id)); //$NON-NLS-1$
            }
        }
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performCancel()
     * @since 5.0
     */
    @Override
    public boolean performCancel() {
        boolean result = true;
        Iterator itr = this.preferenceMap.values().iterator();

        while (itr.hasNext()) {
            GeneralPreference pref = (GeneralPreference)itr.next();

            try {
                boolean successful = pref.contributor.performCancel();

                if (!successful) {
                    result = false;
                }
            } catch (Exception theException) {
                result = false;
                Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "performCancelError", pref.id)); //$NON-NLS-1$
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     * @since 5.0
     */
    @Override
    protected void performDefaults() {
        Iterator itr = this.preferenceMap.values().iterator();

        while (itr.hasNext()) {
            GeneralPreference pref = (GeneralPreference)itr.next();

            try {
                pref.contributor.performDefaults();
            } catch (Exception theException) {
                Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "performDefaultsError", pref.id)); //$NON-NLS-1$
            }
        }

        // must call super
        super.performDefaults();
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     * @since 5.0
     */
    @Override
    public boolean performOk() {
        boolean result = true;
        Iterator itr = this.preferenceMap.values().iterator();

        while (itr.hasNext()) {
            GeneralPreference pref = (GeneralPreference)itr.next();

            try {
                boolean successful = pref.contributor.performOk();

                if (!successful) {
                    result = false;
                }
            } catch (Exception theException) {
                Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "performOkError", pref.id)); //$NON-NLS-1$
                result = false;
            }
        }

        return result;
    }

    /**
     * Processes the specified category <code>IConfigurationElement</code>.
     * 
     * @param theExtension the category being processed
     * @return the flag indicating if the processing was successful
     * @since 5.0
     */
    private boolean processCategory( IConfigurationElement theExtension ) {
        boolean result = true;
        String categoryId = null;
        String categoryName = null;

        try {
            categoryId = theExtension.getAttribute(GeneralPreferenceContributor.CATEGORY_ID_ATTRIBUTE);

            // category ID can't be null
            if (StringUtil.isEmpty(categoryId)) {
                Util.log(IStatus.ERROR, Util.getString(PREFIX + "nullCategoryId", //$NON-NLS-1$
                                                       theExtension.getDeclaringExtension().getExtensionPointUniqueIdentifier()));
                result = false;
            }

            // category with that ID already exists
            if (result && (this.categoryMap.get(categoryId) != null)) {
                Object[] params = new Object[] {categoryId,
                    theExtension.getDeclaringExtension().getExtensionPointUniqueIdentifier()};
                Util.log(IStatus.ERROR, Util.getString(PREFIX + "duplicateCategoryId", params)); //$NON-NLS-1$
                result = false;
            }

            if (result) {
                categoryName = theExtension.getAttribute(GeneralPreferenceContributor.CATEGORY_NAME_ATTRIBUTE);

                // category name can't be null
                if (StringUtil.isEmpty(categoryName)) {
                    Object[] params = new Object[] {categoryId,
                        theExtension.getDeclaringExtension().getExtensionPointUniqueIdentifier()};
                    Util.log(IStatus.ERROR, Util.getString(PREFIX + "nullCategoryName", params)); //$NON-NLS-1$
                    result = false;
                }
            }
        } catch (Exception theException) {
            Util.log(IStatus.ERROR, Util.getString(PREFIX + "unexpectedCategoryError", //$NON-NLS-1$
                                                   theExtension.getDeclaringExtension().getExtensionPointUniqueIdentifier()));
            result = false;
        }

        // if all good add category
        if (result) {
            PreferenceCategory category = new PreferenceCategory();
            category.id = categoryId;
            category.name = categoryName;
            this.categoryMap.put(category.id, category);
        }

        return result;
    }

    /**
     * Process the extensions.
     * 
     * @since 5.0
     */
    private void processExtensions() {
        // get the general preference extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID,
                                                                                           GeneralPreferenceContributor.ID);

        Assertion.isNotNull(extensionPoint, Util.getString(PREFIX + "noExtensionPointFound", GeneralPreferenceContributor.ID)); //$NON-NLS-1$

        try {
            // get all extensions
            IExtension[] extensions = extensionPoint.getExtensions();

            if (extensions.length != 0) {
                for (int i = 0; i < extensions.length; ++i) {
                    IConfigurationElement[] elements = extensions[i].getConfigurationElements();

                    for (int j = 0; j < elements.length; ++j) {
                        if (elements[j].getName().equals(GeneralPreferenceContributor.CATEGORY_ELEMENT)) {
                            processCategory(elements[j]);
                        } else if (elements[j].getName().equals(GeneralPreferenceContributor.PREFERENCE_ELEMENT)) {
                            processPreference(elements[j]);
                        } else {
                            Assertion.failed(Util.getString(PREFIX + "unknownConfigurationElement", elements[j].getName())); //$NON-NLS-1$
                        }
                    }
                }
            }
        } catch (Exception theException) {
            Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "unexpectedErrorProcessingExtensions", //$NON-NLS-1$
                                                                 GeneralPreferenceContributor.ID));
        }
    }

    /**
     * Processes the specified preference <code>IConfigurationElement</code>.
     * 
     * @param theExtension the preference being processed
     * @return the flag indicating if the processing was successful
     * @since 5.0
     */
    private boolean processPreference( IConfigurationElement theExtension ) {
        boolean result = true;
        String prefId = null;
        String categoryId = null;
        IGeneralPreferencePageContributor contributor = null;

        try {
            prefId = theExtension.getAttribute(GeneralPreferenceContributor.PREFERENCE_ID_ATTRIBUTE);

            // preference ID can't be null
            if (StringUtil.isEmpty(prefId)) {
                Util.log(IStatus.ERROR, Util.getString(PREFIX + "nullPreferenceId", //$NON-NLS-1$
                                                       theExtension.getDeclaringExtension().getExtensionPointUniqueIdentifier()));
                result = false;
            }

            // preference with that ID already exists
            if (result && (this.preferenceMap.get(prefId) != null)) {
                Object[] params = new Object[] {prefId, theExtension.getDeclaringExtension().getExtensionPointUniqueIdentifier()};
                Util.log(IStatus.ERROR, Util.getString(PREFIX + "duplicatePreferenceId", params)); //$NON-NLS-1$
                result = false;
            }

            if (result) {
                categoryId = theExtension.getAttribute(GeneralPreferenceContributor.PREFERENCE_CATEGORY_ID_ATTRIBUTE);

                // if categoryId is null, set it to the catch all category
                if (StringUtil.isEmpty(categoryId)) {
                    categoryId = DEFAULT_CATEGORY_ID;
                }
            }

            if (result) {
                // get the contributor
                Object obj = theExtension.createExecutableExtension(GeneralPreferenceContributor.PREFERENCE_CONTRIBUTOR_CLASS_ATTRIBUTE);

                if (obj instanceof IGeneralPreferencePageContributor) {
                    contributor = (IGeneralPreferencePageContributor)obj;
                } else {
                    Object[] params = new Object[] {theExtension.getClass().getName(),
                        theExtension.getDeclaringExtension().getExtensionPointUniqueIdentifier()};
                    Util.log(IStatus.ERROR, Util.getString(PREFIX + "incorrectClass", params)); //$NON-NLS-1$
                    result = false;
                }
            }
        } catch (Exception theException) {
            Util.log(IStatus.ERROR, Util.getString(PREFIX + "unexpectedPreferenceError", //$NON-NLS-1$
                                                   theExtension.getDeclaringExtension().getExtensionPointUniqueIdentifier()));
            result = false;
        }

        // if all good add preference
        if (result) {
            GeneralPreference pref = new GeneralPreference();
            pref.id = prefId;
            pref.categoryId = categoryId;
            pref.contributor = contributor;
            this.preferenceMap.put(pref.id, pref);
        }

        return result;
    }

    /**
     * Refresh editor values from their appropriate preference stores.
     * 
     * @since 5.0
     */
    private void refresh() {
        Iterator itr = this.preferenceMap.values().iterator();

        while (itr.hasNext()) {
            GeneralPreference pref = (GeneralPreference)itr.next();
            pref.contributor.refresh();
        }
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#setTitle(java.lang.String)
     * @since 5.0.2
     */
    @Override
    public void setTitle( String theTitle ) {
        String title = theTitle;
        String key = PREFIX + "title"; //$NON-NLS-1$

        // if title is null/empty that means the title supplied by the extension should be used.
        // if not null/empty it means that a product.properties entry was made to override default title
        if (Util.keyExists(key)) {
            title = Util.getString(key);
        }

        super.setTitle(title);
    }

    static class PreferenceCategory {
        String id;
        String name;
    }

    class GeneralPreference {
        String id;
        String categoryId;
        IGeneralPreferencePageContributor contributor;
    }
}
