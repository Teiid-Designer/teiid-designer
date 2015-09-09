package org.teiid.designer.ui.common.wizard;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.teiid.designer.ui.common.Messages;

public class NoOpenProjectsWizardPage extends WizardPage {
	
	public static WizardPage getStandardPage() {
		WizardPage page =  new NoOpenProjectsWizardPage(NoOpenProjectsWizardPage.class.getSimpleName(),
				Messages.noOpenProjectsWizardTitle, null);
		
		page.setMessage(Messages.noOpenProjectsWizardMessage, IMessageProvider.ERROR);
		page.setPageComplete(false);
		
		return page;
	}

	public NoOpenProjectsWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite parent) {
		setControl(new Composite(parent, SWT.NONE));
	}

}
