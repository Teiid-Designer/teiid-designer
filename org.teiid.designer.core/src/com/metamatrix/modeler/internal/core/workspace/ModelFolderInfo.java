/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;


/**
 * ModelFolderInfo
 */
public class ModelFolderInfo extends OpenableModelWorkspaceItemInfo {

    /**
     * A array with all the non-model resources
     */
    Object[] nonModelResources;

    /**
     * Constructs a new Model Workspace Info 
     */
    protected ModelFolderInfo() {
        this.nonModelResources = null;
    }
    /**
     * Compute the non-java resources contained in this java project.
     */
    private Object[] computeNonModelResources(final ModelFolderImpl folder) {
        //mtkTODO: Implement method to compute non-model resources
        // determine if src == project and/or if bin == project
//        IPath projectPath = project.getProject().getFullPath();
//        boolean srcIsProject = false;
//        boolean binIsProject = false;
//        char[][] exclusionPatterns = null;
//        IClasspathEntry[] classpath = null;
//        IPath projectOutput = null;
//        try {
//            classpath = project.getResolvedClasspath(true/*ignore unresolved variable*/);
//            for (int i = 0; i < classpath.length; i++) {
//                IClasspathEntry entry = classpath[i];
//                if (projectPath.equals(entry.getPath())) {
//                    srcIsProject = true;
//                    exclusionPatterns = ((ClasspathEntry)entry).fullExclusionPatternChars();
//                    break;
//                }
//            }
//            projectOutput = project.getOutputLocation();
//            binIsProject = projectPath.equals(projectOutput);
//        } catch (ModelWorkspaceException e) {
//            // ignore
//        }

//        Object[] nonModelResources = new IResource[5];
//        int nonJavaResourcesCounter = 0;
//        try {
//            IResource[] members = ((IContainer) project.getResource()).members();
//            for (int i = 0, max = members.length; i < max; i++) {
//                IResource res = members[i];
//                switch (res.getType()) {
//                    case IResource.FILE :
//                        IPath resFullPath = res.getFullPath();
//                        String resName = res.getName();
//                        
//                        // ignore a jar file on the classpath
//                        if (Util.isArchiveFileName(resName) && this.isClasspathEntryOrOutputLocation(resFullPath, classpath, projectOutput)) {
//                            break;
//                        }
//                        // ignore .java file if src == project
//                        if (srcIsProject 
//                            && Util.isValidCompilationUnitName(resName)
//                            && !Util.isExcluded(res, exclusionPatterns)) {
//                            break;
//                        }
//                        //// ignore .class file if bin == project
//                        //if (binIsProject && Util.isValidClassFileName(resName)) {
//                        //    break;
//                        //}
//                        // else add non java resource
//                        if (nonModelResources.length == nonJavaResourcesCounter) {
//                            // resize
//                            System.arraycopy( nonModelResources,0,
//                                              (nonModelResources = new IResource[nonJavaResourcesCounter * 2]),
//                                              0,nonJavaResourcesCounter);
//                        }
//                        nonModelResources[nonJavaResourcesCounter++] = res;
//                        break;
//                    case IResource.FOLDER :
//                        resFullPath = res.getFullPath();
//                        
//                        // ignore non-excluded folders on the classpath or that correspond to an output location
//                        if ((srcIsProject && !Util.isExcluded(res, exclusionPatterns) && Util.isValidFolderNameForPackage(res.getName()))
//                                || this.isClasspathEntryOrOutputLocation(resFullPath, classpath, projectOutput)) {
//                            break;
//                        }
//                        // else add non model resource
//                        if (nonModelResources.length == nonJavaResourcesCounter) {
//                            // resize
//                            System.arraycopy(
//                                nonModelResources,
//                                0,
//                                (nonModelResources = new IResource[nonJavaResourcesCounter * 2]),
//                                0,
//                                nonJavaResourcesCounter);
//                        }
//                        nonModelResources[nonJavaResourcesCounter++] = res;
//                }
//            }
//            if (nonModelResources.length != nonJavaResourcesCounter) {
//                System.arraycopy(
//                    nonModelResources,
//                    0,
//                    (nonModelResources = new IResource[nonJavaResourcesCounter]),
//                    0,
//                    nonJavaResourcesCounter);
//            }
//        } catch (CoreException e) {
//            nonModelResources = NO_NON_MODEL_RESOURCES;
//            nonJavaResourcesCounter = 0;
//        }
        return nonModelResources;
    }
    
    /**
     * Returns an array of non-model resources contained in the receiver.
     */
    Object[] getNonModelResources(final ModelFolderImpl folder) {
        Object[] nonModelResources = this.nonModelResources;
        if (nonModelResources == null) {
            nonModelResources = computeNonModelResources(folder);
            this.nonModelResources = nonModelResources;
        }
        return nonModelResources;
    }

    /**
     * Set the non model resources
     */
    synchronized void setNonModelResources(Object[] resources) {
        this.nonModelResources = resources;
    }
    
}
