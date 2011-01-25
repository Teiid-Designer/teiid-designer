/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.ui.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.internal.ui.wizards.NewCPWizard;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.datatools.ui.actions.EditConnectionProfileAction;

/**
 * Utility class to manage Connection Profiles of either a specific category or all profiles.
 * 
 * Intended to be used by wizards to keep track of a selected connection profile and which require both creation of new and editing 
 * of existing connection profiles.
 * 
 * By registering as a listener through the constructor, a wizard page will be notified that a connection profile has changed and that
 * the UI should re-load components that display profile info (i.e. Combo Box, or current selected connection profile).
 * 
 */
public class ConnectionProfileWorker {

	public final static String CATEGORY_JDBC = "org.eclipse.datatools.connectivity.db.category"; //$NON-NLS-1$
	public final static String CATEGORY_WS = "org.eclipse.datatools.enablement.oda.ws"; //$NON-NLS-1$
	public static final String CATEGORY_TEIID_IMPORT = "org.teiid.designer.import.category"; //$NON-NLS-1$

	private String categoryID;
	private IConnectionProfile selectedProfile;
	private Set<IConnectionProfile> allProfiles;
	private Shell shell;
	private IProfileChangedListener listener;

	private static final ProfileManager profileManager = ProfileManager.getInstance();

	/**
	 * 
	 * @param shell the current display <code>Shell</code>
	 * @param categoryID the Connection Profile Category ID. If NULL, then ALL categories are assumed.
	 * @param listener the listener for changed profiles.
	 */
	public ConnectionProfileWorker(Shell shell, String categoryID,
			IProfileChangedListener listener) {
		super();
		this.shell = shell;
		this.categoryID = categoryID;
		this.listener = listener;
		this.allProfiles = new HashSet<IConnectionProfile>();
		reloadProfiles();
	}

	public void create() {
		NewCPWizard wiz = null;

		if( categoryID == null ) {
			wiz = new NewCPWizard();
		} else {
			wiz = new NewTeiidFilteredCPWizard(this.categoryID);
		}
		WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
		wizardDialog.setBlockOnOpen(true);

		CPListener listener = new CPListener();
		ProfileManager.getInstance().addProfileListener(listener);
		if( wizardDialog.open() == Window.OK ) {
			selectedProfile = listener.getChangedProfile();

			reloadProfiles();

			notifyProfileChanged();
		}
		ProfileManager.getInstance().removeProfileListener(listener);
	}

	/**
	 * Launches an editor to allow changing connection profile properties including name.
	 * 
	 * Result will force reloading the list of connection profiles and notify that profile has changed.
	 */
	public void edit() {
		if( this.selectedProfile != null ) {
			EditConnectionProfileAction action = new EditConnectionProfileAction(this.shell, selectedProfile);

			CPListener listener = new CPListener();
			ProfileManager.getInstance().addProfileListener(listener);

			action.run();

			reloadProfiles();

			ProfileManager.getInstance().removeProfileListener(listener);

			notifyProfileChanged();
		}
	}

	public IConnectionProfile getConnectionProfile() {
		return this.selectedProfile;
	}

	public IConnectionProfile getProfile(String name) {
		return ConnectionProfileWorker.profileManager.getProfileByName(name);
	}

	public List<IConnectionProfile> getProfiles() {
		return new ArrayList<IConnectionProfile>(this.allProfiles);
	}

	private void notifyProfileChanged() {
		if( this.listener != null ) {
			this.listener.profileChanged(this.selectedProfile);
		}
	}

	private void reloadProfiles() {
		allProfiles.clear();

		if( categoryID == null ) {
			for (IConnectionProfile prof : profileManager.getProfiles()) {
				allProfiles.add(prof);
			}
		} else {
			for (IConnectionProfile prof : profileManager.getProfilesByCategory(categoryID)) {
				allProfiles.add(prof);
			}
			for (IConnectionProfile prof : profileManager.getProfileByProviderID(categoryID)) {
				allProfiles.add(prof);
			}
		}
	}

	public void setSelection(IConnectionProfile profile) {
		this.selectedProfile = profile;
	}

	public class CPListener implements IProfileListener {

		IConnectionProfile changedProfile;

		public IConnectionProfile getChangedProfile() {
			return changedProfile;
		}

		@Override
		public void profileAdded(IConnectionProfile profile) {
			changedProfile = profile;
		}

		@Override
		public void profileChanged(IConnectionProfile profile) {
			changedProfile = profile;
		}

		@Override
		public void profileDeleted(IConnectionProfile profile) {
			// nothing
		}
	}
}
