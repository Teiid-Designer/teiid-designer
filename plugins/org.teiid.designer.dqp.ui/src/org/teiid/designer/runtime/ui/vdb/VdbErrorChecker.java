/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.vdb;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.core.validation.Severity;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.ProblemElement;
import org.teiid.designer.vdb.manifest.VdbElement;

public class VdbErrorChecker {

    public static boolean hasErrors(IFile vdbFile, boolean deployOnly) {
    	CoreArgCheck.isNotNull(vdbFile, "vdbFile"); //$NON-NLS-1$

        
    	try {
			VdbElement vdbElement = VdbUtil.getVdbManifest(vdbFile);
			for( ModelElement model : vdbElement.getModels() ) {
				
				if( hasProblems(model)) {
			    	
			        final IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();
			        
			    	String vdbName = FileUtils.getNameWithoutExtension(vdbFile);
			    	if( deployOnly ) {
		        		MessageDialog.openError(window.getShell(), 
		        				DqpUiConstants.UTIL.getString("VdbErrorChecker.vdbHasErrors.title"),  //$NON-NLS-1$
		        				DqpUiConstants.UTIL.getString("VdbErrorChecker.vdbHasErrorsOnDeploy.message", vdbName));  //$NON-NLS-1$
			    	} else {
		        		MessageDialog.openError(window.getShell(), 
		        				DqpUiConstants.UTIL.getString("VdbErrorChecker.vdbHasErrors.title"),  //$NON-NLS-1$
		        				DqpUiConstants.UTIL.getString("VdbErrorChecker.vdbHasErrors.message", vdbName));  //$NON-NLS-1$
			    	}
	        		
					return true;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return false;
    }
    
    private static boolean hasProblems(ModelElement model) {
    	for( ProblemElement problem : model.getProblems()) {
    		if( problem.getSeverity().name().equals(Severity.get(Severity.ERROR).getName())) {
    			return true;
    		}
    	}
    	
    	return false;
    }
}
