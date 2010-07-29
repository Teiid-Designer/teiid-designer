/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.metamatrix.common.xml.JdomHelper;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * Utility class for inquiring about eclipse and Designer project-related info.
 *
 */
public class DotProjectUtils {

    public static final String DOT_PROJECT = ".project"; //$NON-NLS-1$

    private DotProjectUtils() {
    }

    /**
     * Finds and returns the a project's .project file
     * @param container
     * @return the .project file for this container, or null if not found
     */
    public static IFile getDotProjectFile( IContainer container ) {
        IProject project = container.getProject();

        if (project != null) {
            return container.getProject().getFile(DotProjectUtils.DOT_PROJECT);
        }
        return null;
    }

    /**
     * 
     * Method which checks if there are multiple .project files within file.
     * 
     * The intend was to provide a method to recognize when one project is nested within another project.
     * 
     * @throws JDOMException
     * @throws IOException
     * @param File name String, boolean to recurse to second level folders, and boolean to only count modeler projects
     * @return 0, 1, or 2 for 2 or greater .project files found
     */

    public static int getDotProjectCount( String fileName,
                                          boolean recurse,
                                          boolean onlyModelerProjects ) throws IOException, JDOMException {
        File file = new File(fileName);
        return getDotProjectCount(file, recurse, onlyModelerProjects);
    }

    /**
     * Method which checks if there are multiple .project files within file.
     * 
     * The intend was to provide a method to recognize when one project is nested within another project.
     * 
     * @throws JDOMException
     * @throws IOException
     * @param File, boolean to recurse to second level folders, and boolean to only count modeler projects
     * @return 0, 1, or 2 for 2 or greater .project files found
     */
    public static int getDotProjectCount( File file,
                                          boolean recurse,
                                          boolean onlyModelerProjects ) throws IOException, JDOMException {
        int dotProjectCount = 0;
        int depth = 0;
        List<File> resources = new ArrayList<File>();
        resources.add(file);
        while (resources.size() > 0) {
            File resource = resources.get(0);
            if (resource.isFile()) {
                if (isDotProject(resource, onlyModelerProjects)) {
                    dotProjectCount++;
                }
                if (dotProjectCount > 1) {
                    return dotProjectCount;
                }
            } else if (recurse || depth == 0) {
                File[] members = resource.listFiles();
                for (int i = 0; i < members.length; i++) {
                    resources.add(members[i]);
                }
                depth++;
            }
            resources.remove(0);
        }
        return dotProjectCount;
    }

    /**
     * Checks file name and peeks into .project if necessary to check for modeler nature.
     * 
     * @throws JDOMException
     * @throws IOException
     * @param File name and boolean to only count modeler projects
     * @return
     */

    public static boolean isDotProject( String file,
                                        boolean onlyModelerProject ) throws IOException, JDOMException {
        return isDotProject(new File(file), onlyModelerProject);
    }

    /**
     * Checks file name and peeks into .project if necessary to check for modeler nature.
     * 
     * @throws JDOMException
     * @throws IOException
     * @param File name and boolean to only count modeler projects
     * @return
     */
    public static boolean isDotProject( File file,
                                        boolean onlyModelerProject ) throws IOException, JDOMException {
        if (file.getName().equals(DOT_PROJECT)) {
            if (onlyModelerProject) {
                Document doc = JdomHelper.buildDocument(file);
                Element elementFound = JdomHelper.findElement(doc.getRootElement(), "nature"); //$NON-NLS-1$
                if (elementFound == null) {
                    return false;
                }
                if (elementFound.getText().equals(ModelerCore.NATURE_ID)) {
                    return true;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * <p>
     * </p>
     * 
     * @throws JDOMException
     * @throws IOException
     * @throws CoreException
     * @param IResource, boolean to recurse to second level folders, and boolean to only count modeler projects
     * @return 0, 1, or 2 for 2 or greater .project files found
     */
    public static int getDotProjectCount( IResource targetResource,
                                          boolean recurse,
                                          boolean onlyModelerProjects ) throws CoreException, IOException, JDOMException {
        int dotProjectCount = 0;
        int depth = 0;
        List<IResource> resources = new ArrayList<IResource>();
        resources.add(targetResource);
        while (resources.size() > 0) {
            IResource resource = resources.get(0);
            if (!(resource instanceof IContainer)) {
                if (isDotProject(resource, onlyModelerProjects)) {
                    dotProjectCount++;
                }
                if (dotProjectCount > 1) {
                    return dotProjectCount;
                }
            } else if (recurse || depth == 0) {
                IResource[] members = ((IContainer)resource).members();
                for (int i = 0; i < members.length; i++) {
                    resources.add(members[i]);
                }
                depth++;
            }
            resources.remove(0);
        }
        return dotProjectCount;
    }

    /**
     * Checks resource name and peeks into .project if necessary to check for modeler nature.
     * 
     * @throws JDOMException
     * @throws IOException
     * @throws CoreException
     * @param File name and boolean to only count modeler projects
     * @return
     */
    public static boolean isDotProject( IResource resource,
                                        boolean onlyModelerProject ) throws CoreException, IOException, JDOMException {
        if (resource.getName().equals(DOT_PROJECT) && resource.getType() == IResource.FILE) {
            if (onlyModelerProject) {
                if (resource.isAccessible()) {
                    if (resource.getProject().isOpen()) {
                        IProjectNature nature = resource.getProject().getNature(ModelerCore.NATURE_ID);
                        return nature != null;
                    }

                    return isModelNature(resource);

                } else if (isDotProject(((IFile)resource).getLocation().toFile(), onlyModelerProject)) {
                    return true;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Determines if the IResource (expecting a .project) contains a Modeler Nature
     * 
     * @param resource
     * @return true if file contains the Model Nature
     */
    private static boolean isModelNature( IResource resource ) {
        boolean result = false;
        File theFile = ((IFile)resource).getLocation().toFile();

        FileReader fileReader = null;
        BufferedReader bufferReader = null;

        try {
            fileReader = new FileReader(theFile.getPath());
            bufferReader = new BufferedReader(fileReader);
            String str = null;
            while ((str = bufferReader.readLine()) != null && !result) {
                if (str.indexOf(ModelerCore.NATURE_ID) > -1) {
                    result = true;
                }
            }
        } catch (Exception e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
        } finally {
            // Clean up readers & buffers
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (java.io.IOException e) {
                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            }
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (java.io.IOException e) {
                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }

        return result;
    }

    /**
     * Determines if an <code>IProject</code> has a Modeler Nature
     * 
     * This will also look at "closed" projects.
     * 
     * 
     * @param iProject
     * @return true if Modeler Project
     */
    public static boolean isModelerProject( IProject iProject ) {
        boolean result = false;

        IFile dotProjectFile = DotProjectUtils.getDotProjectFile(iProject);

        if (dotProjectFile != null) {
            try {
                result = DotProjectUtils.isDotProject(dotProjectFile, true);
            } catch (Exception e) {
                ModelerCore.Util.log(e);
            }
        }
        return result;
    }


    /**
     * Returns a list of open Model Projects within the workspace
     * 
     * @return array of Modeler <code>IProject</code>s
     */
    public static IProject[] getOpenModelProjects() {
        IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

        List<IProject> openModelProjectList = new ArrayList<IProject>(allProjects.length);

        for (IProject proj : allProjects) {
            if (proj.isOpen() && isModelerProject(proj) && !ModelerCore.hasNature(proj, ModelerCore.HIDDEN_PROJECT_NATURE_ID)) {
                openModelProjectList.add(proj);
            }
        }

        IProject[] projArray = new IProject[openModelProjectList.size()];
        int i = 0;
        for (IProject mProj : openModelProjectList) {
            projArray[i++] = mProj;
        }

        return projArray;
    }

}
