/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.editors.summary;

import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;

public class LaunchWizardHyperlinkHandler implements IHyperlinkListener {
	String wizardId;
	IResource targetModel;

	public LaunchWizardHyperlinkHandler(IResource resource, String wizardId) {
		this.targetModel = resource;
		this.wizardId = wizardId;
	}

	@Override
	public void linkEntered(HyperlinkEvent e) {

	}

	@Override
	public void linkExited(HyperlinkEvent e) {

	}

	@Override
	public void linkActivated(HyperlinkEvent e) {
		ModelerUiViewUtils.launchWizard(wizardId, new StructuredSelection(targetModel), new Properties(), true);
	}

}
