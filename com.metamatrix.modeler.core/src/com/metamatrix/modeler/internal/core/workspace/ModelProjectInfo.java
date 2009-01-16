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

package com.metamatrix.modeler.internal.core.workspace;


/**
 * ModelWorkspaceInfo
 */
public class ModelProjectInfo extends OpenableModelWorkspaceItemInfo {

    /**
     * A array with all the non-model fragment roots contained by this model
     */
    Object[] nonModelResources;

    /**
     * Constructs a new Model Workspace Info 
     */
    protected ModelProjectInfo() {
        this.nonModelResources = null;
    }
    /**
     * Compute the non-java resources contained in this java project.
     */
    private Object[] computeNonModelResources(final ModelProjectImpl project) {
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
    Object[] getNonModelResources(final ModelProjectImpl project) {
        Object[] nonModelResources = this.nonModelResources;
        if (nonModelResources == null) {
            nonModelResources = computeNonModelResources(project);
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
