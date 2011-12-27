package org.teiid.designer.datatools.ui.dialogs;

import org.eclipse.datatools.connectivity.IConnectionProfileProvider;
import org.eclipse.datatools.connectivity.internal.ConnectionProfileManager;
import org.eclipse.datatools.connectivity.internal.ui.wizards.CPWizardNode;
import org.eclipse.datatools.connectivity.internal.ui.wizards.ProfileWizardProvider;
import org.eclipse.datatools.connectivity.ui.wizards.IWizardCategoryProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class NewCPWizardProfileFilter extends ViewerFilter {

	private String profileID;

	public NewCPWizardProfileFilter(String categoryID) {
		setProviderID(categoryID);
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		CPWizardNode wizardNode = (CPWizardNode) element;
		if (!(wizardNode.getProvider() instanceof IWizardCategoryProvider)) {
			IConnectionProfileProvider provider = ConnectionProfileManager
					.getInstance().getProvider(
							((ProfileWizardProvider) wizardNode.getProvider())
									.getProfile());
			if (provider != null && provider.getId().equals(profileID))
				return true;
			else
				return false;

		} else {
			if (((IWizardCategoryProvider) wizardNode.getProvider()).getId()
					.equals(profileID))
				return true;
		}
		return false;
	}

	public void setProviderID(String categoryID) {
		this.profileID = categoryID;
	}

}