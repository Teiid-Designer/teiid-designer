/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.core.workspace.ModelUtil;

/**
 *
 */
public class ModelProjectResourceValidator implements ResourceValidator {

	/* (non-Javadoc)
	 * @see org.teiid.designer.core.builder.ResourceValidator#isValidatorForObject(java.lang.Object)
	 */
	@Override
	public boolean isValidatorForObject(Object obj) {
		if( obj instanceof IProject ) {
			return DotProjectUtils.isModelerProject((IProject)obj);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.core.builder.ResourceValidator#validate(org.eclipse.core.runtime.IProgressMonitor, java.lang.Object, org.teiid.designer.core.validation.ValidationContext)
	 */
	@SuppressWarnings("unused")
	@Override
	public void validate(IProgressMonitor monitor, Object obj, ValidationContext context) throws ModelerCoreException {
		//System.out.println("ModelProjectResourceValidator.validate() object = " + obj);
		IProject project = (IProject)obj;
		try {
			IProjectDescription desc = project.getDescription();
			
			
			boolean foundVdbBuilder = false;
			for( ICommand com : desc.getBuildSpec()) {
				foundVdbBuilder = com.getBuilderName().equalsIgnoreCase("org.teiid.designer.vdb.ui.vdbBuilder");

				if( foundVdbBuilder ) break;
			}
			if( ! foundVdbBuilder ) {
				ValidationProblem problem = new ValidationProblemImpl(
						999, IStatus.ERROR, "Model project definition for " + project.getName() + " is out of date. Perform Quick Fix to update the project");
						//ModelerCore.Util.getString("ModelProjectResourceValidator.duplicateFileNamesError", fileName)); //$NON-NLS-1$
				
				try {
			        @SuppressWarnings("rawtypes")
					Map<String, Comparable> attributes = new HashMap<String, Comparable>();
			        attributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			        attributes.put(IMarker.MESSAGE, problem.getMessage());
			        attributes.put("missingVdbBuilder", true);
					attributes.put(IMarker.LOCATION, project.getName());
			        createMarker(project, attributes, IMarker.PROBLEM);

				} catch (CoreException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Collection<IFile> allFiles = DotProjectUtils.getAllProjectResources((IProject)obj);
		
		Collection<IPath> fullFilePaths = new ArrayList<IPath>();
		Collection<IPath> fullFilePathsInner = new ArrayList<IPath>();
		
		for( IFile file : allFiles) {
			if( ModelUtil.isModelFile(file) || ModelUtil.isXsdFile(file) ) {
				fullFilePaths.add(file.getFullPath());
				fullFilePathsInner.add(file.getFullPath());
			}
		}
		
		Set<String> fileNamesWithMultiple = new HashSet<String>();
		Set<String> fileNamesWithMultipleUpperCase = new HashSet<String>();
		
		for( IPath path : fullFilePaths) {
			String fileNameNoExt = path.removeFileExtension().lastSegment();
			
			if( fileNamesWithMultipleUpperCase.contains(fileNameNoExt.toUpperCase())) continue;
			
			int nFiles = 0;
			for( IPath innerPath : fullFilePathsInner) {
				if( innerPath.removeFileExtension().lastSegment().equalsIgnoreCase(fileNameNoExt)) {
					nFiles++;
				}
			}
			
			if( nFiles > 1 ) {
				fileNamesWithMultiple.add(fileNameNoExt);
				fileNamesWithMultipleUpperCase.add(fileNameNoExt.toUpperCase());
			}
		}
		
		for( String fileName : fileNamesWithMultiple ) {
			addNewProblemMarker((IProject)obj, fileName);
		}
		

		
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.core.builder.ResourceValidator#addMarkers(org.teiid.designer.core.validation.ValidationContext, org.eclipse.core.resources.IResource)
	 */
	@SuppressWarnings("unused")
	@Override
	public void addMarkers(ValidationContext context, IResource iResource)
			throws ModelerCoreException {
		// Unused
		
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.core.builder.ResourceValidator#isValidatorForResource(org.eclipse.core.resources.IResource)
	 */
	@Override
	@Deprecated
	@SuppressWarnings("deprecation")
	public	boolean isValidatorForResource(IResource iResource) {
		// Unused
		return false;
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.core.builder.ResourceValidator#validate(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.emf.ecore.resource.Resource, org.eclipse.core.resources.IResource, org.teiid.designer.core.validation.ValidationContext)
	 */
	@Override
	@Deprecated
	@SuppressWarnings({ "unused", "deprecation" })
	public void validate(IProgressMonitor monitor, Resource resource,
			IResource iResource, ValidationContext context)
			throws ModelerCoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.core.builder.ResourceValidator#validationStarted(java.util.Collection, org.teiid.designer.core.validation.ValidationContext)
	 */
	@Override
	public void validationStarted(Collection resources,
			ValidationContext context) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.core.builder.ResourceValidator#validationEnded(org.teiid.designer.core.validation.ValidationContext)
	 */
	@Override
	public void validationEnded(ValidationContext context) {
		// TODO Auto-generated method stub
		
	}
	
    // ==================================================================================
    //                         P R I V A T E   M E T H O D S
    // ==================================================================================
	
	
	private void addNewProblemMarker(IProject project, String fileName) {
		
		ValidationProblem problem = new ValidationProblemImpl(
				999, IStatus.WARNING, 
				ModelerCore.Util.getString("ModelProjectResourceValidator.duplicateFileNamesError", fileName)); //$NON-NLS-1$
		
		try {
			createProblemMarker(project.getName(), problem, project);
		} catch (CoreException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

    /**
     * Create a marker given a validationProblem
     */
    private void createProblemMarker(final String location, final ValidationProblem problem, final IResource resource) throws CoreException {
                                         
        IMarker marker = resource.createMarker(IMarker.PROBLEM);
        if( location != null ) marker.setAttribute(IMarker.LOCATION, location);
        marker.setAttribute(IMarker.MESSAGE, problem.getMessage());

        setMarkerSeverity(marker, problem);
    }
    
    /**
     * Get the set the severity on the marker given the validation problem.
     */
    private void setMarkerSeverity(final IMarker marker, final ValidationProblem problem) throws CoreException {
        switch(problem.getSeverity()) {
            case IStatus.ERROR:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                break;
            case IStatus.WARNING:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                break;
            case IStatus.INFO:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
                break;
            default:
                return;
        }
    }
    
	/*
	 * see org.eclipse.ui.texteditor.MarkerUtilities.createMarker()
	 * 
	 * Creates a marker on the given resource with the given type and attributes.
	 * <p>
	 * This method modifies the workspace (progress is not reported to the user).</p>
	 *
	 * @param resource the resource
	 * @param attributes the attribute map (key type: <code>String</code>,
	 *   value type: <code>Object</code>)
	 * @param markerType the type of marker
	 * @throws CoreException if this method fails
	 * @see IResource#createMarker(java.lang.String)
	 */
	public void createMarker(final IResource resource, final Map attributes, final String markerType) throws CoreException {

		IWorkspaceRunnable r= new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker= resource.createMarker(markerType);
				marker.setAttributes(attributes);
			}
		};

		resource.getWorkspace().run(r, null,IWorkspace.AVOID_UPDATE, null);
	}

}
