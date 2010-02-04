/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions.workers;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import com.metamatrix.modeler.internal.ui.wizards.ExportWizard;
import com.metamatrix.ui.internal.util.WidgetFactory;

public class ExportActionUiWorker {
	private IStructuredSelection theSelection;
	private IWorkbenchWindow wdw;
	
	public ExportActionUiWorker(IStructuredSelection selection, IWorkbenchWindow window) {
		super();
		this.theSelection = selection;
		this.wdw = window;
	}
	
	public void run() {
		WidgetFactory.createWizardDialog(this.wdw.getShell(), new ExportWizard(this.wdw.getWorkbench(), theSelection)).open();
	}

}
