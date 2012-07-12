/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.core.util.ModelStatisticsVisitor;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.core.util.PrimaryMetamodelStatisticsVisitor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.viewsupport.ModelStatisticsDialog;


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
            ModelResource resource = ModelUtil.getModelResource(modelFile, true);
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
