/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.GlobalBuildAction;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ValidationDescriptor;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * This class represents the preference page for setting the Modeler Validation Preferences.
 */
public class ValidationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, UiConstants {
	/////////////////////////////////////////////////////////////////////////////////////////////
	// STATIC VARIABLES
	/////////////////////////////////////////////////////////////////////////////////////////////
	public static final String[] STORED_VALUES;
	public static final String[] VALUE_DISPLAY_NAMES;
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	/////////////////////////////////////////////////////////////////////////////////////////////
	static {
		STORED_VALUES = new String[4];
		VALUE_DISPLAY_NAMES = new String[4];
		STORED_VALUES[0] = ValidationDescriptor.ERROR;
		VALUE_DISPLAY_NAMES[0] = Util.getString("ValidationPreferencePage.error"); //$NON-NLS-1$
		STORED_VALUES[1] = ValidationDescriptor.WARNING;
		VALUE_DISPLAY_NAMES[1] = Util.getString("ValidationPreferencePage.warning"); //$NON-NLS-1$
		STORED_VALUES[2] = ValidationDescriptor.INFO;
		VALUE_DISPLAY_NAMES[2] = Util.getString("ValidationPreferencePage.info"); //$NON-NLS-1$
		STORED_VALUES[3] = ValidationDescriptor.IGNORE;
		VALUE_DISPLAY_NAMES[3] = Util.getString("ValidationPreferencePage.ignore"); //$NON-NLS-1$
	}
	
	public static int indexOfStoredValueName(String name) {
		int index = -1;
		int i = 0;
		while ((i < STORED_VALUES.length) && (index < 0)) {
			if (STORED_VALUES[i].equals(name)) {
				index = i;
			} else {
				i++;
			}
		}
		return index;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	// INSTANCE VARIABLES
	////////////////////////////////////////////////////////////////////////////////////////////
	private TabFolder tabFolder;
	private TabItem[] tabs;
	private Map /*<CTabItem to list of ValidationItems>*/ tabItemsMap = new HashMap();
	private Map /*<String (item name) to String (current value)>*/ currentValuesMap;
	private java.util.List /*<ValidationDescriptor>*/ descriptors;
	
	public ValidationPreferencePage() {
		super();
		setDescription(Util.getString("ValidationPreferencePage.description")); //$NON-NLS-1$
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	// INSTANCE METHODS
	/////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Overridden from {@link PreferencePage} as required.  Create the contents for the page.
	 * 
	 * @param   parent            parent control
	 * @return   Control for the page
	 */
	@Override
    public Control createContents(Composite parent) {
		String BLANK = ""; //$NON-NLS-1$
		String TAB_NAME_FOR_BLANK = Util.getString("ValidationPreferencePage.general"); //$NON-NLS-1$
		descriptors = ModelerCore.getValidationPreferences().getValidationDescriptors();
		java.util.List /*<String>*/ categories = new ArrayList(descriptors.size()); 
		Iterator it = descriptors.iterator();
		while (it.hasNext()) {
			ValidationDescriptor vd = (ValidationDescriptor)it.next();
			String category = vd.getPreferenceCategory();
			if (category == null) {
				category = BLANK;
			}
			if (!categories.contains(category)) {
				categories.add(category);
			}
		}
		//Re-order the categories to put the "General" category first.  This is the
		//category for where the name is blank.
		int index = categories.indexOf(BLANK);
		if (index >= 0) {
			categories.remove(index);
			categories.add(0, BLANK);
		}
		
		tabFolder = new TabFolder(parent, SWT.NONE);

		//Add a tab for each category
		tabs = new TabItem[categories.size()];
		it = categories.iterator();
		for (int i = 0; it.hasNext(); i++) {
			String tabName = (String)it.next();
			if (tabName.equals(BLANK)) {
				tabName = TAB_NAME_FOR_BLANK;
			}
			tabs[i] = new TabItem(tabFolder, SWT.NONE);
			tabs[i].setText(tabName);
			tabs[i].setToolTipText(tabName);
			java.util.List validationItemsThisTab = new ArrayList();
			tabItemsMap.put(tabs[i], validationItemsThisTab);
			Composite tabComposite = new Composite(tabFolder, SWT.NONE);
			GridLayout tabLayout = new GridLayout();
			tabLayout.marginHeight = 4;
			tabComposite.setLayout(tabLayout);
			tabComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			tabs[i].setControl(tabComposite);
			String tabDescriptionText = tabName + " " + Util.getString( //$NON-NLS-1$
					"ValidationPreferencePage.validationControlSettings"); //$NON-NLS-1$
			Label tabDescriptionLabel = new Label(tabComposite, SWT.NONE);
			tabDescriptionLabel.setText(tabDescriptionText);
            ScrolledComposite tabScrollPane = new ScrolledComposite(tabComposite, SWT.V_SCROLL | SWT.H_SCROLL);
			Point pt = tabScrollPane.getParent().computeSize(SWT.DEFAULT, SWT.DEFAULT);
			tabScrollPane.setExpandHorizontal(true);
			tabScrollPane.setExpandVertical(true);
			tabScrollPane.setMinWidth(pt.x);
			tabScrollPane.setMinHeight(pt.y);
			GridLayout tabScrollPaneLayout = new GridLayout();
			tabScrollPaneLayout.marginHeight = 0;
			tabScrollPaneLayout.marginWidth = 0;
			tabScrollPane.setLayout(tabScrollPaneLayout);
			GridData tabScrollPaneGridData = new GridData(GridData.FILL_BOTH);
			tabScrollPaneGridData.horizontalIndent = 4;
			tabScrollPane.setLayoutData(tabScrollPaneGridData);
			Composite groupContents = new Composite(tabScrollPane, SWT.NONE);
			tabScrollPane.setContent(groupContents);
			GridLayout groupContentsLayout = new GridLayout();
			groupContents.setLayout(groupContentsLayout);
			groupContentsLayout.marginHeight = 0;
			groupContents.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			//Add a row for each item in the category
			Iterator iter = descriptors.iterator();
			while (iter.hasNext()) {
				ValidationDescriptor vd = (ValidationDescriptor)iter.next();
				String descriptorTabName = vd.getPreferenceCategory();
				if ((descriptorTabName == null) || descriptorTabName.equals(BLANK)) {
					descriptorTabName = TAB_NAME_FOR_BLANK;
				}
				if (descriptorTabName.equals(tabName)) {
					ValidationItem vi = new ValidationItem(groupContents, vd);
					validationItemsThisTab.add(vi);
				}
			}
		}
		setValues();
		setValid(true);
		tabFolder.setSelection(0);
		return tabFolder;
	}
	
	private void setValues() {
		currentValuesMap = ModelerCore.getValidationPreferences().getOptions();
		for (int i = 0; i < tabs.length; i++) {
            java.util.List /*<ValidationItem>*/itemsThisTab = (java.util.List)tabItemsMap.get(tabs[i]);
			Iterator it = itemsThisTab.iterator();
			while (it.hasNext()) {
				ValidationItem item = (ValidationItem)it.next();
				ValidationDescriptor descriptor = item.getDescriptor();
				String itemName = descriptor.getExtensionID() + "." + //$NON-NLS-1$ 
						descriptor.getPreferenceName();
				//String itemName = descriptor.getPreferenceName();
				String currentValue = (String)currentValuesMap.get(itemName);
				if (currentValue == null) {
					String defaultValue = descriptor.getDefaultOption();
					currentValue = defaultValue;
				}
				item.setToValue(currentValue, true);
			}
		}
	}
	
	/**
	 * Method required by {@link IWorkbenchPreferencePage}
	 */
	@Override
    public void init(IWorkbench workbench) {
	}
	
	/**
     * Method overridden from {@link PreferencePage} as required. Handle processing when the "Apply" button is pressed.
	 * 
	 * @return   always true
	 */ 
	@Override
    public boolean performOk() {
		Map /*<ValidationDescriptor to String (value)>*/ changedValuesMap = new HashMap();
		boolean changeMade = false;
		for (int i = 0; i < tabs.length; i++) {
			List /*<ValidationItem>*/ items = (List)tabItemsMap.get(tabs[i]);
			Iterator it = items.iterator();
			while (it.hasNext()) {
				ValidationItem item = (ValidationItem)it.next();
				ValidationDescriptor descriptor = item.getDescriptor();
				String itemName = descriptor.getExtensionID() + "." //$NON-NLS-1$
						+ descriptor.getPreferenceName();
				//String itemName = descriptor.getPreferenceName();
				boolean nullIfUnchanged = (currentValuesMap.get(itemName) != null);
				String changedValue = item.getValue(nullIfUnchanged);
				if (changedValue != null) {
					changedValuesMap.put(descriptor, changedValue);
					changeMade = true;
				}
			}
		}
		if (changeMade) {		    
		    //Save the changes before showing dialog so that the new validation settings will be used during build
		    ModelerCore.getValidationPreferences().setOptions(changedValuesMap);

            // defect 19167 - Stolen from OptionsConfigurationBlock.  Prompt the user to rebuild:
            MessageDialog dialog = new MessageDialog(
                                                     getShell(),
                                                     Util.getString("ValidationPreferencePage.dialogChangedTitle"), null, Util.getString("ValidationPreferencePage.dialogChangedMessage"), MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL}, 2); //$NON-NLS-1$ //$NON-NLS-2$
            int res = dialog.open();
            if (res == 0) {
                try {
                    final IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();
                    IRunnableWithProgress op = new IRunnableWithProgress() {
                        @Override
                        public void run( IProgressMonitor monitor ) {
                            try {
                                // defect 19634 - code below copied from CleanDialog:
                                ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
                                GlobalBuildAction build = new GlobalBuildAction(window, IncrementalProjectBuilder.FULL_BUILD);
                                build.doBuild();
                            } catch (CoreException ex) {
                                Util.log(ex);
                            } // endtry
                        }
                    }; // endanon IRunnableWithProgress
                    new ProgressMonitorDialog(getShell()).run(true, true, op);

                } catch (InvocationTargetException ex) {
                    Util.log(ex);

                } catch (InterruptedException ex) {
                    Util.log(ex);
                } // endtry

            } else if (res != 1) {
                return false; // cancel pressed
            }

            // Now retrieve the saved changes and set the GUI from them. This will ensure that the changes were saved correctly.
			setValues();
		}
		return true;
	}

	/**
     * Method overridden from {@link PreferencePage} as required. Handle resetting items to their default values then the
     * "Restore Defaults" button is pressed.
	 */
	@Override
    public void performDefaults() {
		for (int i = 0; i < tabs.length; i++) {
			java.util.List /*<ValidationItem>*/ items = (java.util.List)tabItemsMap.get(tabs[i]);
			Iterator it = items.iterator();
			while (it.hasNext()) {
				ValidationItem item = (ValidationItem)it.next();
				ValidationDescriptor descriptor = item.getDescriptor();
				String defaultValue = descriptor.getDefaultOption();
				item.setToValue(defaultValue, false);
			}
		}
		super.performDefaults();
	}
}//end ValidationPreferencePage




/**
 * Class to represent the GUI for a single preference. It consists of a label for the preference, and a combo box from which a
 * preference value is selected. The class contains a reference to a {@link ValidationDescriptor}.
 */
class ValidationItem extends Composite {
	private ValidationDescriptor descriptor;
	private String originalValue;
	private Combo choiceCombo;
			
    public ValidationItem( Composite parent,
                           ValidationDescriptor vd ) {
		super(parent, SWT.NONE);
		this.descriptor = vd;
						
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		this.setLayout(layout);
		layout.numColumns = 2;
		GridData thisGridData = new GridData(GridData.FILL_HORIZONTAL);
		this.setLayoutData(thisGridData);
		Label itemNameLabel = new Label(this, SWT.NONE);
		itemNameLabel.setText(descriptor.getPreferenceLabel());
		itemNameLabel.setToolTipText(descriptor.getPreferenceToolTip());
		GridData labelGridData = new GridData();
		labelGridData.grabExcessHorizontalSpace = true;
		itemNameLabel.setLayoutData(labelGridData);
		choiceCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (int i = 0; i < ValidationPreferencePage.VALUE_DISPLAY_NAMES.length; i++) {
			choiceCombo.add(ValidationPreferencePage.VALUE_DISPLAY_NAMES[i]);
		}
		int index = ValidationPreferencePage.indexOfStoredValueName(originalValue);
		choiceCombo.select(index);
	}
	
    public void setToValue( String value,
                            boolean setAsOriginalValue ) {
		if (setAsOriginalValue) {
			this.originalValue = value;
		}
		int index = ValidationPreferencePage.indexOfStoredValueName(value);
		choiceCombo.select(index);
	}
	
	/**
	 * Return value.  If flag set, return null if value unchanged.
	 */
	public String getValue(boolean nullIfUnchanged) {
		int index = choiceCombo.getSelectionIndex();
		String value = ValidationPreferencePage.STORED_VALUES[index];
		String changedValue = null;
		if ((!nullIfUnchanged) || (!value.equals(originalValue))) {
			changedValue = value;
		}
		return changedValue;
	}
	
	public ValidationDescriptor getDescriptor() {
		return descriptor;
	}		
}//end ValidationItem	
