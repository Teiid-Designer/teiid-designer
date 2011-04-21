/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.DataRoleResolver;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbDataRole;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;


/**
 * This class provides the VDB Editor a mechanism to clean-up/validate the data roles defined for the VDB based
 * on any changes in VDB contents.
 * 
 * Models removed from VDB, for instance, may require Permissions and/or Data Roles becoming obsolete.
 * 
 * Models that have been synchronized from workspace may have changed to the point of making Permissions obsolete.
 * 
 *
 */
public class VdbDataRoleResolver {
	static final String WEB_SERVICES_VIEW_MODEL_URI = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$
	private static final char DELIM = StringUtil.Constants.DOT_CHAR;
	private static final char B_SLASH = '/';
	
	private Vdb vdb;
	
	/**
	 * 
	 * @param vdb
	 */
	public VdbDataRoleResolver(Vdb vdb) {
		super();
		this.vdb = vdb;
	}
	
	/**
	 * Assumed that the input list of VdbEntry's are being removed from the VDB.
	 * 
	 * Makes a call to clean-up/remove any data role permissions associated with any of these removed models.
	 * 
	 * @param entries
	 */
	public void modelEntriesRemoved( Set<VdbEntry> entries ) {
		Set<String> modelNames = new HashSet<String>(entries.size());
		for( VdbEntry entry : entries ) {
			modelNames.add(entry.getName().removeFileExtension().lastSegment());
		}
		
		removePermissionsForModels(modelNames);
	}
	
	
	/*
	 * Remove any permissions who's target name begins with one of the provided model names
	 */
	private void removePermissionsForModels(Set<String> modelNames) {
		Set<VdbDataRole> roles = vdb.getDataPolicyEntries();
		
		for( VdbDataRole role : roles ) {
			boolean changedPerms = false;
			Collection<Permission> keepPermList = new ArrayList<Permission>();
			List<Permission> permissions = role.getPermissions();
			for( Permission perm : permissions ) {
				boolean shouldRemovePermission = targetIsInStringList(modelNames, perm.getTargetName());
				
				if( shouldRemovePermission ) {
					changedPerms = true;
				} else {
					keepPermList.add(perm);
				}
			}
			
			if( changedPerms && ! keepPermList.isEmpty() ) {
				// Remove the old data policy
				vdb.removeDataPolicy(role);
				
				// Create a data role
				DataRole dr = new DataRole(role.getName());
				dr.setDescription(role.getDescription());
				if( !role.getMappedRoleNames().isEmpty() ) {
					dr.setRoleNames(role.getMappedRoleNames());
				}
				dr.setPermissions(keepPermList);
				dr.setAnyAuthenticated(role.isAnyAuthenticated());
				
				vdb.addDataPolicy(dr, new NullProgressMonitor());
				
			}
		}
	}
	
	private boolean targetIsInStringList(Set<String> modelNames, String targetName) {
		IPath targetModelPath = new Path(getTargetNamePath(targetName));
		String targetModelName = targetModelPath.segment(0); // SHOULD BE MODEL NAME
		
		for( String modelName : modelNames ) {
			if( modelName.equalsIgnoreCase(targetModelName) ) {
				return true;
			}
		}
		
		return false;
	}
	
	private String getTargetNamePath(String dotPath) {
		return dotPath.replace(DELIM, B_SLASH);
	}
	
	private DataRole getDataRole(VdbDataRole vdbDataRole) {
		// Create a data role
		DataRole dr = new DataRole(vdbDataRole.getName());
		dr.setDescription(vdbDataRole.getDescription());
		if( !vdbDataRole.getMappedRoleNames().isEmpty() ) {
			dr.setRoleNames(vdbDataRole.getMappedRoleNames());
		}
		List<Permission> perms = new ArrayList<Permission>(vdbDataRole.getPermissions());
		dr.setPermissions(perms);
		
		return dr;
	}
	
	/**
	 * Synchronize Model action in VdbEditor will replace a model in the VDB with the current same-name model in the workspace.
	 * 
	 * This method will load this current model (after synchronization) into a temporary EMF container and remove any 
	 * permission in any data role that has an unresolved target. This is accomplished by searching the container for
	 * an EObject with the corresponding "target name" or path.
	 * @param element the VdbEntry
	 */
	public void modelSynchronized(VdbEntry element) {
		if( element instanceof VdbModelEntry ) {
			ContainerImpl tempContainer = null;
			VdbModelEntry entry = (VdbModelEntry)element;

	        try {
	            File modelFile = null;
	            for( File file : vdb.getModelFiles() ) {
	            	String entryName = entry.getName().lastSegment();
	            	if( file.getName().equals(entryName)) {
	            		modelFile = file;
	            		break;
	            	}
	            }
	            if( modelFile != null ) {
		            tempContainer = (ContainerImpl)ModelerCore.createContainer("tempVdbModelContainer"); //$NON-NLS-1$
		            ModelEditorImpl.setContainer(tempContainer);

	                boolean isVisible = true;

	                Resource r = tempContainer.getResource(URI.createFileURI(modelFile.getPath()), true);
	                if (isVisible && ModelUtil.isModelFile(r) && !ModelUtil.isXsdFile(r)) {
	                    EObject firstEObj = r.getContents().get(0);
	                    ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(firstEObj);
	                    String mmURI = ma.getPrimaryMetamodelUri();
	                    if (RelationalPackage.eNS_URI.equalsIgnoreCase(mmURI) ||
	                    	XmlDocumentPackage.eNS_URI.equalsIgnoreCase(mmURI) ||
	                    	WEB_SERVICES_VIEW_MODEL_URI.equalsIgnoreCase(mmURI)) {
	                        // DO NOTHING. This leaves the resource in the temp container
	                    } else {
	                        tempContainer.getResources().remove(r);
	                    }
	                } else {
	                    tempContainer.getResources().remove(r);
	                }
	            }
	        } catch (CoreException e) {
	            e.printStackTrace();
	        } finally {
	            ModelEditorImpl.setContainer(null);
	        }
	        
	        if( tempContainer != null ) {
	        	DataRoleResolver resolver = new DataRoleResolver(tempContainer);
	        	
	        	for( VdbDataRole existingRole : vdb.getDataPolicyEntries() ) {
	        		DataRole existingDataRole = getDataRole(existingRole);
	    			DataRole changedDataRole = resolver.resolveDataRole(existingDataRole);
	    			
	    			if( changedDataRole != null ) {
	    				// Remove the old data policy
	    				vdb.removeDataPolicy(existingRole);
	    				vdb.addDataPolicy(changedDataRole, new NullProgressMonitor());
	    			}
	        	}
	        	
	        }
		}
	}
	
	/**
	 * Synchronize All action in VdbEditor will replace any model in the VDB that has been changed in the user's 
	 * workspace with that current same-name model.
	 * 
	 * This method will load each vdb model (after synchronization) into a temporary EMF container and remove any 
	 * permission in any data role that has an unresolved target. This is accomplished by searching the container for
	 * an EObject with the corresponding "target name" or path.
	 */
	public void allSynchronized() { 
		ContainerImpl tempContainer = null;
        try {
            Collection<File> modelFiles = vdb.getModelFiles();

            tempContainer = (ContainerImpl)ModelerCore.createContainer("tempVdbModelContainer"); //$NON-NLS-1$
            ModelEditorImpl.setContainer(tempContainer);
            for (File modelFile : modelFiles) {
                boolean isVisible = true;

                Resource r = tempContainer.getResource(URI.createFileURI(modelFile.getPath()), true);
                if (isVisible && ModelUtil.isModelFile(r) && !ModelUtil.isXsdFile(r)) {
                    EObject firstEObj = r.getContents().get(0);
                    ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(firstEObj);
                    String mmURI = ma.getPrimaryMetamodelUri();
                    if (RelationalPackage.eNS_URI.equalsIgnoreCase(mmURI) ||
                    	XmlDocumentPackage.eNS_URI.equalsIgnoreCase(mmURI) ||
                    	WEB_SERVICES_VIEW_MODEL_URI.equalsIgnoreCase(mmURI)) {
                        // DO NOTHING. This leaves the resource in the temp container
                    } else {
                        tempContainer.getResources().remove(r);
                    }
                } else {
                    tempContainer.getResources().remove(r);
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        } finally {
            ModelEditorImpl.setContainer(null);
        }
        
        if( tempContainer != null ) {
        	DataRoleResolver resolver = new DataRoleResolver(tempContainer);
        	
        	for( VdbDataRole existingRole : vdb.getDataPolicyEntries() ) {
        		DataRole existingDataRole = getDataRole(existingRole);
    			DataRole changedDataRole = resolver.resolveDataRole(existingDataRole);
    			
    			if( changedDataRole != null ) {
    				// Remove the old data policy
    				vdb.removeDataPolicy(existingRole);
    				vdb.addDataPolicy(changedDataRole, new NullProgressMonitor());
    			}
        	}
        	
        }
	}
}
