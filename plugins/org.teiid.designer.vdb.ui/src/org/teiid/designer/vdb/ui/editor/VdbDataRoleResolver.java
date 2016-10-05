/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.ui.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelEditorImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.ContainerImpl;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.wizard.DataRoleResolver;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;



/**
 * This class provides the VDB Editor a mechanism to clean-up/validate the data roles defined for the VDB based
 * on any changes in VDB contents.
 * 
 * Models removed from VDB, for instance, may require Permissions and/or Data Roles becoming obsolete.
 * 
 * Models that have been synchronized from workspace may have changed to the point of making Permissions obsolete.
 * 
 *
 *
 * @since 8.0
 */
public class VdbDataRoleResolver {
	static final String WEB_SERVICES_VIEW_MODEL_URI = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$
	private static final char DELIM = CoreStringUtil.Constants.DOT_CHAR;
	private static final char B_SLASH = '/';
	private static final String SYSADMIN = "sysadmin"; //$NON-NLS-1$
	
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
			modelNames.add(entry.getName());
		}
		
		removePermissionsForModels(modelNames);
	}
	
	
	/*
	 * Remove any permissions who's target name begins with one of the provided model names
	 */
	private void removePermissionsForModels(Set<String> modelNames) {
		Collection<DataRole> roles = vdb.getDataRoles();
		
		Collection<DataRole> removeList = new ArrayList<DataRole>();
		Collection<DataRole> addList = new ArrayList<DataRole>();
		
		for( DataRole role : roles ) {
			boolean changedPerms = false;
			Collection<Permission> keepPermList = new ArrayList<Permission>();
			Collection<Permission> permissions = role.getPermissions();
			for( Permission perm : permissions ) {
				boolean shouldRemovePermission = targetIsInStringList(modelNames, perm.getTargetName());
				
				if( shouldRemovePermission ) {
					changedPerms = true;
				} else {
					keepPermList.add(perm);
				}
			}
			
			if( keepPermList.size() == 1 ) {
				if( ((Permission)keepPermList.toArray()[0]).getTargetName().equals(SYSADMIN) ) {
					keepPermList.clear();
				}
			}
			
			if( changedPerms ) {
				if( ! keepPermList.isEmpty() ) {
				
					// Remove the old data policy
					removeList.add(role);
					
					// Create a data role
					DataRole dr = new DataRole(role.getName());
					dr.setDescription(role.getDescription());
					if( !role.getRoleNames().isEmpty() ) {
						dr.setRoleNames(role.getRoleNames());
					}
					dr.setPermissions(keepPermList);
					dr.setAnyAuthenticated(role.isAnyAuthenticated());
	
					addList.add(dr);
				} else {
					removeList.add(role);
				}
			}
		}
		
		for( DataRole role : removeList ) {
			vdb.removeDataRole(role.getName());
		}
		for( DataRole role : addList ) {
			vdb.addDataRole(role);
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
	
	private DataRole getDataRole(DataRole DataRole) {
		// Create a data role
		DataRole dr = new DataRole(DataRole.getName());
		dr.setDescription(DataRole.getDescription());
		if( !DataRole.getRoleNames().isEmpty() ) {
			dr.setRoleNames(DataRole.getRoleNames());
		}
		List<Permission> perms = new ArrayList<Permission>(DataRole.getPermissions());
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
	            	String entryFileName = entry.getPathName();
	            	if( file.getName().equals(entryFileName)) {
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
	        	
	        	for( DataRole existingRole : vdb.getDataRoles() ) {
	        		DataRole existingDataRole = getDataRole(existingRole);
	    			DataRole changedDataRole = resolver.resolveDataRole(existingDataRole);
	    			
	    			if( changedDataRole != null ) {
	    				// Remove the old data policy
	    				vdb.removeDataRole(existingRole.getName());
	    				vdb.addDataRole(changedDataRole);
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
        	
        	for( DataRole existingRole : vdb.getDataRoles() ) {
        		DataRole existingDataRole = getDataRole(existingRole);
    			DataRole changedDataRole = resolver.resolveDataRole(existingDataRole);
    			
    			if( changedDataRole != null ) {
    				// Remove the old data policy
    				vdb.removeDataRole(existingRole.getName());
    				vdb.addDataRole(changedDataRole);
    			}
        	}
        	
        }
	}
}
