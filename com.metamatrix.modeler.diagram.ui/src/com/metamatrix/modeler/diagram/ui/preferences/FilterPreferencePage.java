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
package com.metamatrix.modeler.diagram.ui.preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;

/**
 * This class represents the preference page for setting the Diagram Filter Preferences.
 */
public class FilterPreferencePage extends PreferencePage implements DiagramUiConstants, IWorkbenchPreferencePage {

    // I18N Property File Keys
    private static final String KEY_PAGE_DESCRIPTION = "DiagramFilterPrefPage.description";//$NON-NLS-1$
    private static final String KEY_DIAGRAM_TAB_TITLE = "DiagramFilterPrefPage.diagram.tabTitle";//$NON-NLS-1$
    private static final String KEY_PACKAGE_TAB_TITLE = "DiagramFilterPrefPage.package.tabTitle";//$NON-NLS-1$
    private static final String KEY_GROUP_TAB_TITLE = "DiagramFilterPrefPage.group.tabTitle";//$NON-NLS-1$
    private static final String KEY_ATTRIBUTE_TAB_TITLE = "DiagramFilterPrefPage.attribute.tabTitle";//$NON-NLS-1$
    private static final String KEY_OPERATIONS_TAB_TITLE = "DiagramFilterPrefPage.operations.tabTitle";//$NON-NLS-1$
    private static final String KEY_ASSOCIATIONS_TAB_TITLE = "DiagramFilterPrefPage.associations.tabTitle";//$NON-NLS-1$
    private static final String KEY_DIAGRAM_TAB_DESC = "DiagramFilterPrefPage.diagram.tabDesc";//$NON-NLS-1$
    private static final String KEY_PACKAGE_TAB_DESC = "DiagramFilterPrefPage.package.tabDesc";//$NON-NLS-1$
    private static final String KEY_GROUP_TAB_DESC = "DiagramFilterPrefPage.group.tabDesc";//$NON-NLS-1$
    private static final String KEY_ATTRIBUTE_TAB_DESC = "DiagramFilterPrefPage.attribute.tabDesc";//$NON-NLS-1$
    private static final String KEY_OPERATIONS_TAB_DESC = "DiagramFilterPrefPage.operations.tabDesc";//$NON-NLS-1$
    private static final String KEY_ASSOCIATIONS_TAB_DESC = "DiagramFilterPrefPage.associations.tabDesc";//$NON-NLS-1$

    private ArrayList settingControls;
    HashSet changedControls = null;

    public FilterPreferencePage() {
        setDescription(Util.getString(KEY_PAGE_DESCRIPTION));
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
     */
    @Override
    protected Control createContents( Composite parent ) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);

        TabFolder folder = new TabFolder(container, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        folder.setLayoutData(gd);

        settingControls = new ArrayList();
        SelectionListener listener = new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                if (changedControls == null) changedControls = new HashSet();
                changedControls.add(e.widget);
            }
        };

        createPage(folder, Util.getString(KEY_DIAGRAM_TAB_TITLE), FilterSettings.DIAGRAM);
        createPage(folder, Util.getString(KEY_PACKAGE_TAB_TITLE), FilterSettings.PACKAGE);
        createPage(folder, Util.getString(KEY_GROUP_TAB_TITLE), FilterSettings.GROUP);
        createPage(folder, Util.getString(KEY_ATTRIBUTE_TAB_TITLE), FilterSettings.ATTRIBUTE);
        createPage(folder, Util.getString(KEY_OPERATIONS_TAB_TITLE), FilterSettings.OPERATIONS);
        createPage(folder, Util.getString(KEY_ASSOCIATIONS_TAB_TITLE), FilterSettings.ASSOCIATIONS);

        for (int i = 0; i < settingControls.size(); i++) {
            Control control = (Control)settingControls.get(i);
            if (control instanceof Button) ((Button)control).addSelectionListener(listener);
        }

        return container;
    }

    private void createPage( TabFolder folder,
                             String name,
                             int index ) {
        Composite page = new Composite(folder, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        page.setLayout(layout);

        TabItem tab = new TabItem(folder, SWT.NONE);
        tab.setText(name);
        tab.setControl(page);

        Group group = new Group(page, SWT.NONE);
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        String descKey;
        if (index == FilterSettings.DIAGRAM) descKey = KEY_DIAGRAM_TAB_DESC;
        else if (index == FilterSettings.PACKAGE) descKey = KEY_PACKAGE_TAB_DESC;
        else if (index == FilterSettings.GROUP) descKey = KEY_GROUP_TAB_DESC;
        else if (index == FilterSettings.ATTRIBUTE) descKey = KEY_ATTRIBUTE_TAB_DESC;
        else if (index == FilterSettings.OPERATIONS) descKey = KEY_OPERATIONS_TAB_DESC;
        else descKey = KEY_ASSOCIATIONS_TAB_DESC;

        // Set text on the Group makes a border around it
        group.setText(Util.getString(descKey));
        group.setLayout(new GridLayout());

        GridData gd = new GridData();
        gd.horizontalSpan = 2;

        String[] settingIDs = FilterSettings.getSettings(index);
        for (int i = 0; i < settingIDs.length; i++) {
            Control control = createSetting(group, settingIDs[i]);
            settingControls.add(control);
        }
    }

    private Control createSetting( Composite page,
                                   String settingID ) {
        Control control = null;

        Button button = new Button(page, SWT.CHECK);
        button.setText(Util.getString(settingID));
        button.setSelection(FilterSettings.getBoolean(settingID));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        button.setLayoutData(gd);
        control = button;

        control.setData(settingID);
        return control;
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        for (int i = 0; i < settingControls.size(); i++) {
            Control control = (Control)settingControls.get(i);
            String settingID = (String)control.getData();
            if (control instanceof Button) {
                ((Button)control).setSelection(FilterSettings.getDefaultBoolean(settingID));
            }
        }
        changedControls = null;
    }

    @Override
    public boolean performOk() {
        if (changedControls != null) {

            for (Iterator iter = changedControls.iterator(); iter.hasNext();) {
                Control control = (Control)iter.next();
                String settingID = (String)control.getData();
                if (control instanceof Button) {
                    boolean value = ((Button)control).getSelection();
                    FilterSettings.setBoolean(settingID, value);
                }
            }
            FilterSettings.save();

        }
        return super.performOk();
    }

    /**
     * Initializes this preference page using the passed desktop.
     * 
     * @param desktop the current desktop
     */
    public void init( IWorkbench workbench ) {
    }
}
