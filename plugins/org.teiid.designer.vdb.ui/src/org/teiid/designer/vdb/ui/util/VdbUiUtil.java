/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbPlugin;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.teiid.designer.vdb.ui.VdbUiConstants;

/**
 *
 */
public class VdbUiUtil {
	
	/**
	 * @param theVdb the VDB
	 */
	public static boolean synchronizeWorkspace(final IFile theVdb) {
		boolean result = false;
		if (theVdb.exists()) {
			IProject theProject = theVdb.getProject();
			
			VdbElement manifest = VdbUtil.getVdbManifest(theVdb);
			if (manifest != null) {
				
				for (ModelElement model : manifest.getModels()) {
					String modelUuid = VdbUtil.getUuid(model);
					
					IResource resource = null;

					if( modelUuid != null ) {
						resource = WorkspaceResourceFinderUtil.findIResourceByUUID(modelUuid);
					} else {
						// Find my model name
						IResource[] resources = WorkspaceResourceFinderUtil.findIResourceInProjectByName(model.getName()+ ModelUtil.DOT_EXTENSION_XMI, theProject);
						if( resources.length == 1 ) {
							resource = resources[0];
						}
					}
					
					// Check if resource is found or not.
					if( resource == null ) {
						// Note that we have a View model that is in a VDB and it's source is removed (VDB.removeEntry())
						// then that action will remove the View Model as well.
						// We need to prevent this OR need to tell the user we can't do anything about this and maybe
						// bail from this method.

						// Construct model path
						IPath targetPath = getProjectRelativeModelPath(model.getPath(), theVdb.getProject());
						//extractModelFromVdb(theVdb, model, targetPath);
						VdbUiUtil.extractFileFromVdbToSameProject(theVdb, model.getPath(), targetPath);
						
						result = true;
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @param theVdb the VDB
	 * @param extractMissingModels 
	 */
	public static void synchronizeVdb(final IFile theVdb, boolean extractMissingModels) {
		if (theVdb.exists()) {
			IProject theProject = theVdb.getProject();

			
			VdbElement manifest = VdbUtil.getVdbManifest(theVdb);
			if (manifest != null) {
				boolean changed = false;
				
				if( extractMissingModels ) {
					changed = synchronizeWorkspace(theVdb);
				}
				
				Vdb actualVDB = new Vdb(theVdb, true, new NullProgressMonitor());
				
				Set<ModelElement> modelsToReplace = new HashSet<ModelElement>();
				Collection<IResource> matchingResources = new ArrayList<IResource>();
				Set<ModelElement> modelsNotInWorkspace = new HashSet<ModelElement>();
				Map<String, IResource> oldModelPathToResourceMap = new HashMap<String, IResource>();
				
				Set<String> dependentViewModelPaths = new HashSet<String>();
				
				
				for (ModelElement model : manifest.getModels()) {
					Collection<String> modelImports = VdbUtil.getModelImports(model);
					for( String importedModelPath : modelImports ) {
						for (ModelElement model_2 : manifest.getModels()) {
							if( model_2.getPath().equals(importedModelPath) ) {
								dependentViewModelPaths.add(model.getPath());
								break;
							}
						}
					}
				}
				
				for (ModelElement model : manifest.getModels()) {
					String modelName = model.getName();
					String modelUuid = VdbUtil.getUuid(model);
					
					IResource resource = null;
					
					boolean addTheUuid =  (modelUuid == null);
					boolean nameWasChanged = false;

					if( !addTheUuid ) {
						resource = WorkspaceResourceFinderUtil.findIResourceByUUID(modelUuid);
						if( resource != null ) {
							nameWasChanged = ! resource.getFullPath().removeFileExtension().lastSegment().equalsIgnoreCase(modelName);
						}
					} else {
						// Find my model name
						IResource[] resources = WorkspaceResourceFinderUtil.findIResourceInProjectByName(model.getName()+ ModelUtil.DOT_EXTENSION_XMI, theProject);
						if( resources.length == 1 ) {
							resource = resources[0];
						}
					}
					
					// Check if resource is found or not.
					if( resource != null ) {
						String path = model.getPath();
						
						// Check IPath
						IPath iPath = new Path(path);
						IResource expectedResourceAtPath = ModelerCore.getWorkspace().getRoot().findMember(iPath);
						
						boolean fixThePath = (expectedResourceAtPath == null);

						oldModelPathToResourceMap.put(model.getPath(), resource);
						
						if( fixThePath || nameWasChanged || addTheUuid) {
					 		for( VdbModelEntry modelEntry : actualVDB.getModelEntries()) {
								if( modelEntry.getName().removeFileExtension().lastSegment().equalsIgnoreCase(modelName) ) {
									modelsToReplace.add(model);
									matchingResources.add(resource);
									changed = true;
									break;
					 			}
					 		}

						}
					} else {
						// Note that we have a View model that is in a VDB and it's source is removed (VDB.removeEntry())
						// then that action will remove the View Model as well.
						// We need to prevent this OR need to tell the user we can't do anything about this and maybe
						// bail from this method.
						modelsNotInWorkspace.add(model);
						// Construct model path
//						IPath targetPath = getProjectRelativeModelPath(model.getPath(), theVdb.getProject());
//						//extractModelFromVdb(theVdb, model, targetPath);
//						VdbUiUtil.extractFileFromVdbToSameProject(theVdb, model.getPath(), targetPath);
					}
				}
				
				// Check to see that any dependent View models are in the modelsNotInWorkspace list
				if( ! modelsNotInWorkspace.isEmpty() ) {

					// check if any are "dependent" (i.e. view models)
					for( ModelElement missingModel : modelsNotInWorkspace ) {
						if( dependentViewModelPaths.contains(missingModel.getPath()) ) {
							// TODO: Throw up dialog saying can't synchronize because view model does not exist in workspace
							return;
						}
					}
				}
				
				// Loop through the changed models and remove/add them back as model entries
				Collection<String> modelPathsToReplace = new ArrayList<String>();
				
				for( ModelElement element : modelsToReplace ) {
					VdbModelEntry modelEntry = getVdbModelEntry(element, actualVDB);
					if( modelEntry != null ) {
						modelPathsToReplace.add(element.getPath());
						actualVDB.removeEntry(modelEntry);
					}
				}
				
				// Then loop through dependentViewModels list and add back any that are NOT in the VDB yet. These will be the
				// View model VdbModelEntry() instances that are auto-removed when "source" models are removed by VdbModelEntry.dispose() method.
				for( String thePath : modelPathsToReplace ) {
					IResource matchingResource = oldModelPathToResourceMap.get(thePath);
					if( matchingResource != null ) {
						actualVDB.addModelEntry(matchingResource.getFullPath(), new NullProgressMonitor());
					}
				}
				
				for( String thePath : dependentViewModelPaths ) {
					IResource matchingResource = oldModelPathToResourceMap.get(thePath);
					if( matchingResource != null ) {
						actualVDB.addModelEntry(matchingResource.getFullPath(), new NullProgressMonitor());
					}
				}
				
				actualVDB.synchronize(new NullProgressMonitor());
				
				
		 		actualVDB.save(new NullProgressMonitor());
		 		try {
					theVdb.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
				} catch (CoreException ex) {
					VdbPlugin.UTIL.log(IStatus.ERROR, ex, ex.getMessage());
				}
			}
		}
			
	}
	
	private static VdbModelEntry getVdbModelEntry(ModelElement element, Vdb actualVDB) {
 		for( VdbModelEntry modelEntry : actualVDB.getModelEntries()) {
			if( modelEntry.getName().removeFileExtension().lastSegment().equalsIgnoreCase(element.getName()) ) {
				return modelEntry;
			}
 		}
 		
 		return null;
	}
	
	/*
	 * Simple method to return a project relative path to a project folder or sub-folder based on the model path
	 * defined in another project (or same project)
	 * 
	 * Method removes the "project" segment from the input path and appends the rest to the target project
	 */
	private static IPath getProjectRelativeModelPath(final String modelPathInVdb, final IProject targetProject) {
		IPath vdbModelPath = new Path(modelPathInVdb);
		
		IPath targetPath = new Path(StringUtilities.EMPTY_STRING);
		int iSegs = vdbModelPath.segmentCount();
		if( iSegs > 1 ) {
			for( int i=1; i<iSegs; i++ ) {
				targetPath = targetPath.append(vdbModelPath.segment(i));
			}
		}
		
		return targetPath;
	}
	
	/**
	 * 
	 * @param zipFile
	 * @param zipEntryFullPath
	 * @param projectRelativeTargetFolder
	 * @return true if successful, false if not
	 */
	public static boolean extractFileFromVdbToSameProject(final IFile zipFile, final String zipEntryFullPath, IPath projectRelativeTargetFolder) {
		
		String zipFilePath = zipFile.getRawLocation().toOSString();
		
		
		
		String projectFilePath = ModelerCore.getWorkspace().getRoot().getRawLocation() + zipFile.getProject().getFullPath().toOSString();
		
		ZipInputStream zin = null;
		
		boolean result = false;
		
		try {

			FileInputStream fin = new FileInputStream(zipFilePath);
			BufferedInputStream bin = new BufferedInputStream(fin);
			zin = new ZipInputStream(bin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				String entryName = '/' + ze.getName();
			    if (entryName.equals(zipEntryFullPath)) {
					String finalModelPath = projectFilePath + '/' + projectRelativeTargetFolder;
					
					//System.out.println("Name of  Zip Entry : " + zipEntryFullPath);
					
					File entryFile = new File(finalModelPath);
					if( !entryFile.getParentFile().exists() ) {
						entryFile.getParentFile().mkdir();
					}
					
					FileOutputStream outstream = new FileOutputStream(finalModelPath);
			        byte[] buffer = new byte[8192];
			        int len;
			        while ((len = zin.read(buffer)) != -1) {
			        	outstream.write(buffer, 0, len);
			        }

					//System.out.println("Successfully Extracted File Name : " + finalModelPath);
					outstream.close();
		
					zin.closeEntry();
					result = true;
			        break;
			    }
			}
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} finally {
			if( zin != null ) {
				try {
					zin.close();
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}
		
 		try {
			zipFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException ex) {
			VdbUiConstants.Util.log(IStatus.ERROR, ex, ex.getMessage());
		}
 		
 		return result;
	}
}
