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

package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.modeler.core.util.ModelStatisticsVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.util.PrimaryMetamodelStatisticsVisitor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelStatisticsDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;

public class ModelStatisticsReporter {
	private IFile modelFile;
	
	public ModelStatisticsReporter(IFile iFile) {
		super();
		
		this.modelFile = iFile;
	}
	
	public void show() {
        final ModelStatisticsVisitor visitor = new PrimaryMetamodelStatisticsVisitor();   // or PrimaryMetamodelStatisticsVisitor() 
        final int mode = ModelVisitorProcessor.MODE_VISIBLE_CONTAINMENTS;   // show only those objects visible to user
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor,mode);
        final Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        try {
            ModelResource resource = ModelUtilities.getModelResource(modelFile, true);
            if ( resource != null ) {
                processor.walk(resource, ModelVisitorProcessor.DEPTH_INFINITE);

                ModelStatisticsDialog dialog = new ModelStatisticsDialog(shell, visitor, resource);
                dialog.open();

            }
        } catch (Exception e) {
            UiConstants.Util.log(e);
            final String title = UiConstants.Util.getString("ModelStatisticsAction.errorTitle"); //$NON-NLS-1$
            final String message = UiConstants.Util.getString("ModelStatisticsAction.errorMessage"); //$NON-NLS-1$
            MessageDialog.openError(shell, title, message);
        }
	}

}
