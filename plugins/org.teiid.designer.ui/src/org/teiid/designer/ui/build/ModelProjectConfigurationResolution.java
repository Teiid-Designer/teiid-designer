/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.build;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IMarkerResolution;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.UiConstants;

public class ModelProjectConfigurationResolution implements IMarkerResolution {

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return UiConstants.Util.getString("ModelProjectConfigurationResolution.label"); //"Update Teiid Designer Model Project Configuration";
	}

	@Override
	public void run(IMarker marker) {
		IResource resource = marker.getResource();
		
		if( resource instanceof IProject ) {
			IProject project = (IProject)resource;
			try {
				IProjectDescription desc = project.getDescription();
				
				
				boolean foundVdbBuilder = false;
				for( ICommand com : desc.getBuildSpec()) {
					foundVdbBuilder = com.getBuilderName().equalsIgnoreCase(ModelerCore.VDB_BUILDER_ID);

					if( foundVdbBuilder ) break;
				}
				if( ! foundVdbBuilder ) {
					// Add org.teiid.designer.vdb.ui.vdbBuilder
		            ICommand cmd = desc.newCommand();
		            cmd.setBuilderName(ModelerCore.VDB_BUILDER_ID);
		            final ICommand[] cmds = desc.getBuildSpec();
		            final ICommand[] newCmds = new ICommand[cmds.length + 1];
		            System.arraycopy(cmds, 0, newCmds, 1, cmds.length);
		            newCmds[0] = cmd;
		            desc.setBuildSpec(newCmds);
		            project.setDescription(desc, null);
		            project.refreshLocal(1, new NullProgressMonitor());
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}
