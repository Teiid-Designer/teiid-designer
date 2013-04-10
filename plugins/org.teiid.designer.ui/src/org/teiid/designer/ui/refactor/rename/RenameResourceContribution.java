/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.rename;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

/**
 *
 */
public class RenameResourceContribution extends RefactoringContribution {

    /**
     * Key prefix used for the path of the resource to be renamed.
     */
    private static final String ATTRIBUTE_INPUT = "input"; //$NON-NLS-1$

    /**
     * Key used for the new resource name
     */
    private static final String ATTRIBUTE_NAME= "name"; //$NON-NLS-1$
    
    /**
     * @param project
     * @param handle
     * @return
     */
    private IPath handleToResourcePath(final String project, final String handle) {
        final IPath path= Path.fromPortableString(handle);
        if (project != null && project.length() > 0 && !path.isAbsolute())
            return new Path(project).append(path).makeAbsolute();
        return path;
    }
    
    private String resourcePathToHandle(final String project, final IPath resourcePath) {
        if (project != null && project.length() > 0 && resourcePath.segmentCount() != 1)
            if (resourcePath.segment(0).equals(project)) {
                return resourcePath.removeFirstSegments(1).toPortableString();
            }
        return resourcePath.toPortableString();
    }
    
    @Override
    public RefactoringDescriptor createDescriptor() {
        return new RenameResourceDescriptor();
    }
    
    @Override
    public RefactoringDescriptor createDescriptor(String id,
                                                  String project,
                                                  String description,
                                                  String comment,
                                                  Map arguments,
                                                  int flags) throws IllegalArgumentException {
        
        String pathString = (String) arguments.get(ATTRIBUTE_INPUT);
        String newName = (String) arguments.get(ATTRIBUTE_NAME);

        if (pathString != null && newName != null) {
            IPath path = handleToResourcePath(project, pathString);
            RenameResourceDescriptor descriptor = new RenameResourceDescriptor();
            descriptor.setProject(project);
            descriptor.setDescription(description);
            descriptor.setComment(comment);
            descriptor.setFlags(flags);
            descriptor.setNewName(newName);
            descriptor.setResourcePathToRename(path);
            return descriptor;
        }
        throw new IllegalArgumentException("Can not restore RenameResourceDescriptor from map"); //$NON-NLS-1$
    }
    
    @Override
    public Map retrieveArgumentMap(RefactoringDescriptor descriptor) {
        if (! (descriptor instanceof RenameResourceDescriptor))
            return null;
        
        RenameResourceDescriptor renameDescriptor = (RenameResourceDescriptor) descriptor;

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(ATTRIBUTE_INPUT, resourcePathToHandle(descriptor.getProject(), renameDescriptor.getResourcePath()));
        map.put(ATTRIBUTE_NAME, renameDescriptor.getNewName());
        return map;
    }
}
