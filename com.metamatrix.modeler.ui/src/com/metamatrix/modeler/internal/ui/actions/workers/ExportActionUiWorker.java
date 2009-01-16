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
