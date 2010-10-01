/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.ui.actions;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IManagedConnection;
import org.eclipse.datatools.connectivity.internal.repository.IConnectionProfileRepository;
import org.eclipse.datatools.connectivity.internal.repository.IConnectionProfileRepositoryConstants;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.internal.ui.ProfileUIManager;
import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * This action provides "Edit Connection Profile" functionality.
 * 
 * It contains code from DTP's ViewPropertyAction because it handled the complex nature of the property contributions for connection 
 * profiles.
 */
public class EditConnectionProfileAction extends Action {

	// for stashing the size of the dialog for consistency 
	public final static String MEMENTO_ROOT = "Connectivity_Property_Dialog_Root";//$NON-NLS-1$
	public final static String MEMENTO_DIALOG_SIZE_HEIGHT = "Dialog_Size_Height";//$NON-NLS-1$
	public final static String MEMENTO_DIALOG_SIZE_WIDTH = "Dialog_Size_Width";//$NON-NLS-1$
	private int mShellWidth = 0;
	private int mShellHeight = 0;

	private IConnectionProfile profile;
	private Shell mShell;
	
	private boolean wasFinished = false;
	

	/**
	 * Constructor
	 */
	public EditConnectionProfileAction(Shell shell, IConnectionProfile profile) {
		this.profile = profile;
		this.mShell = shell;
		setText(ConnectivityUIPlugin.getDefault().getResourceString(
				"ServersView.action.showproperties")); //$NON-NLS-1$
	}

	/*
	 * Facility method added in here so that dependency to eclipse internal api
	 * is kept to a minimum
	 */
	public static boolean hasContributors(Object selected) {
		return ProfileUIManager.hasContributors( selected );
	}

	/*
	 * @see org.eclipse.ui.IAction#run()
	 */
	@Override
	public void run() {

		PreferenceDialog propertyDialog = 
		    ProfileUIManager.createPreferenceDialog( mShell, 
		            this.profile );
		if( propertyDialog == null )
		    return;

		// check for size settings
		IDialogSettings dset = ConnectivityUIPlugin.getDefault()
			.getDialogSettings();
		boolean foundSettings = false;
		if (dset != null) {
			IDialogSettings dSection = dset.getSection(MEMENTO_ROOT);
			if (dSection != null) {
				if (dSection.get(MEMENTO_DIALOG_SIZE_HEIGHT) != null
						&& dSection.get(MEMENTO_DIALOG_SIZE_HEIGHT).trim()
								.length() > 0) {
					mShellHeight = dSection.getInt(MEMENTO_DIALOG_SIZE_HEIGHT);
					mShellWidth = dSection.getInt(MEMENTO_DIALOG_SIZE_WIDTH);
					foundSettings = true;
				}
			}
		}
		// if we found them, set it to the old values
		if (foundSettings) {
			propertyDialog.getShell().setSize(mShellWidth, mShellHeight);
			propertyDialog.getShell().layout();
		// if not, initialize 'mShellWidth' and 'mShellHeigth'
		}else{
			mShellHeight = propertyDialog.getShell().getSize().y;
			mShellWidth = propertyDialog.getShell().getSize().x;
		}
		
        String title = ConnectivityUIPlugin.getDefault().getResourceString(
                        "properties.dialog"); //$NON-NLS-1$

    	title = ConnectivityUIPlugin.getDefault().
			getResourceString("ConnectAction.title",  //$NON-NLS-1$
				new String[] {profile.getName()});
    	
		propertyDialog.getShell().setText(title);
		
		// add a listener to make sure we get any resizes of the dialog
		// to store for the next time
		this.mShell.addControlListener(new ControlListener(){

			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				if (e.getSource() instanceof Shell) {
					Shell shell = (Shell) e.getSource();
					EditConnectionProfileAction.this.mShellHeight = shell.getSize().y;
					EditConnectionProfileAction.this.mShellWidth = shell.getSize().x;
				}
			}
		});

		// check to see if the profile is in a read-only repository
		// and if so, disable the controls on each page selected
		boolean inReadOnlyRepository = false;

		if (profile.getParentProfile() != null) {
			IManagedConnection imc = ((IConnectionProfile) profile.getParentProfile())
				.getManagedConnection(IConnectionProfileRepositoryConstants.REPOSITORY_CONNECTION_FACTORY_ID);
			if (imc != null && imc.isConnected()) {
				IConnectionProfileRepository repo = (IConnectionProfileRepository) imc
						.getConnection().getRawConnection();
				inReadOnlyRepository = repo.isReadOnly();
			}
		}
	
		// handle the initial page selected in the dialog for the
		// read only repository page disabling...
		propertyDialog.addPageChangedListener( new PropertyPageChangeListener(inReadOnlyRepository) );
		if (inReadOnlyRepository && propertyDialog.getSelectedPage() != null) {
			PropertyPage page = (PropertyPage) propertyDialog.getSelectedPage();
			if (propertyDialog.getSelectedPage() instanceof ConnectionProfileDetailsPage ||
					propertyDialog.getSelectedPage() instanceof ProfileDetailsPropertyPage) {
				if (page.getControl() instanceof Composite) {
					Composite composite = (Composite) page.getControl();
					if (inReadOnlyRepository)
						disableControls(composite, !inReadOnlyRepository, true);
				}
			}
			else if (propertyDialog.getSelectedPage() instanceof PropertyPage) {
				if (page.getControl() instanceof Composite) {
					Composite composite = (Composite) page.getControl();
					disableControls(composite, !inReadOnlyRepository);
				}
			}
		}
		int rtn_val = propertyDialog.open();
		if (rtn_val == Dialog.OK) {
			wasFinished = true;
			saveState();
		}
	}
	
	/*
	 * Page change listener to disable controls for profiles
	 * in a read-only repository.
	 * 
	 * @author brianf
	 *
	 */
	private class PropertyPageChangeListener implements IPageChangedListener {
		
		private boolean inReadOnlyRepository = false;
		
		/*
		 * Constructor
		 * @param flag
		 */
		public PropertyPageChangeListener( boolean flag ) {
			inReadOnlyRepository = flag;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IPageChangedListener#pageChanged(org.eclipse.jface.dialogs.PageChangedEvent)
		 */
		public void pageChanged(PageChangedEvent event) {
			if (event.getSelectedPage() instanceof ConnectionProfileDetailsPage ||
					event.getSelectedPage() instanceof ProfileDetailsPropertyPage) {
				PropertyPage page = (PropertyPage) event.getSelectedPage();
				if (page.getControl() instanceof Composite) {
					Composite composite = (Composite) page.getControl();
					if (inReadOnlyRepository)
						disableControls(composite, !inReadOnlyRepository, true);
				}
			}
			else if (event.getSelectedPage() instanceof PropertyPage) {
				PropertyPage page = (PropertyPage) event.getSelectedPage();
				if (page.getControl() instanceof Composite) {
					Composite composite = (Composite) page.getControl();
					if (inReadOnlyRepository)
						disableControls(composite, !inReadOnlyRepository);
				}
			}
		}
	}
	
	/*
	 * Disable controls on a composite
	 * @param parent
	 * @param enabled
	 */
	private void disableControls ( Composite parent, boolean enabled ) {
		disableControls(parent, enabled, false);
	}
	
	/*
	 * Disable controls on a composite, but have a special case
	 * for the Test Connection (Ping) button 
	 * @param parent
	 * @param enabled
	 * @param checkForPing
	 */
	private void disableControls ( Composite parent, boolean enabled, boolean checkForPing ) {
		for (int i = 0; i < parent.getChildren().length; i++) {
			
			if (parent.getChildren()[i] instanceof TabFolder) {
				Control[] tabList = ((TabFolder) parent.getChildren()[i]).getTabList();
				for (int j = 0; j < tabList.length; j++) {
					if (tabList[j] instanceof Composite) {
						disableControls((Composite) tabList[j], enabled, checkForPing);
					}
					tabList[j].setEnabled(false);
				}
			}
			else if (parent.getChildren()[i] instanceof Composite) {
				disableControls((Composite) parent.getChildren()[i], enabled, checkForPing);
				if (parentHasCombo((Composite)parent.getChildren()[i], enabled)) {
					parent.getChildren()[i].setEnabled(enabled);
				}
			}
			else {
				if (parent.getChildren()[i] instanceof Label) {
					// ignore
				}
				else if (parent.getChildren()[i] instanceof List) {
					Color bg = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND); 
					((List)parent.getChildren()[i]).setBackground(bg);
					((List)parent.getChildren()[i]).setEnabled(enabled);
				}
				else if (parent.getChildren()[i] instanceof TabFolder) {
					//ignore
				}
				else if (parent.getChildren()[i] instanceof Button) {
					if (checkForPing) {
						String pingLabel = ConnectivityUIPlugin.getDefault().getResourceString(
							"ConnectionProfileDetailsPage.Button.TestConnection"); //$NON-NLS-1$
						Button btn = (Button) parent.getChildren()[i];
						if (!btn.isDisposed() && btn.getText().equals(pingLabel)) {
							btn.setEnabled(true);
						}
						else {
							btn.setEnabled(enabled);
						}
					}
					else {
						parent.getChildren()[i].setEnabled(enabled);
					}
				}
				else {
					parent.getChildren()[i].setEnabled(enabled);
				}
			}
		}
	}
	
	/*
	 * See if the composite has a combo on it. If so, set the background
	 * so it looks disabled.
	 * @param parent
	 * @param enabled
	 * @return
	 */
	private boolean parentHasCombo ( Composite parent, boolean enabled ) {
		if (parent.getChildren().length > 0) {
			Control[] controls = parent.getChildren();
			for (int i = 0; i < controls.length; i++) {
				if (controls[i] instanceof Combo) {
					if (!enabled) {
						Color bg = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND); 
						((Combo)controls[i]).setBackground(bg);
					}
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Save the dialog settings
	 */
	private void saveState() {
		IDialogSettings dset = ConnectivityUIPlugin.getDefault()
				.getDialogSettings();
		if (dset != null && this.mShell != null ) {
			IDialogSettings dSection = dset.getSection(MEMENTO_ROOT);
			if (dSection == null)
				dSection = dset.addNewSection(MEMENTO_ROOT);
			if (dSection != null) {
				dSection.put(MEMENTO_DIALOG_SIZE_HEIGHT, mShellHeight);
				dSection.put(MEMENTO_DIALOG_SIZE_WIDTH, mShellWidth);
			}
		}
	}

	public boolean wasFinished() {
		return wasFinished;
	}
}
