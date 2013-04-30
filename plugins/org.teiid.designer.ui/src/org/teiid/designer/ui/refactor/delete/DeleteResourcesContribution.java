/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.delete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

/**
 *
 */
public class DeleteResourcesContribution extends RefactoringContribution {

    /**
     * Key used for the number of resource to be moved
     */
    private static final String ATTRIBUTE_NUMBER_OF_RESOURCES= "resources"; //$NON-NLS-1$

    /**
     * Key prefix used for the path of the resources to be moved.
     * <p>
     * The element arguments are simply distinguished by appending a number to
     * the argument name, e.g. element1. The indices of this argument are one-based.
     * </p>
     */
    private static final String ATTRIBUTE_ELEMENT= "element"; //$NON-NLS-1$

    /**
     * Key used for the delete contents parameter
     */
    private static final String ATTRIBUTE_DELETE_CONTENTS = "deleteContents"; //$NON-NLS-1$
    
    /**
     * Taken from ltk's ResourceProcessors class since it is internal
     * 
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
        return new DeleteResourcesDescriptor();
    }
    
    @Override
    public RefactoringDescriptor createDescriptor(String id,
                                                  String project,
                                                  String description,
                                                  String comment,
                                                  Map arguments,
                                                  int flags) throws IllegalArgumentException {
        try {
            int numResources = Integer.parseInt((String) arguments.get(ATTRIBUTE_NUMBER_OF_RESOURCES));
            if (numResources < 0 || numResources > 100000) {
                throw new IllegalArgumentException("Can not restore MoveResourceDescriptor from map, number of moved elements invalid"); //$NON-NLS-1$
            }

            List<IPath> resourcePaths = new ArrayList<IPath>(numResources);
            for (int i = 0; i < numResources; i++) {
                String resource = (String) arguments.get(ATTRIBUTE_ELEMENT + String.valueOf(i + 1));
                if (resource == null) {
                    throw new IllegalArgumentException("Can not restore MoveResourceDescriptor from map, resource missing"); //$NON-NLS-1$
                }
                resourcePaths.add(handleToResourcePath(project, resource));
            }

            String del = (String) arguments.get(ATTRIBUTE_DELETE_CONTENTS);

            DeleteResourcesDescriptor descriptor = new DeleteResourcesDescriptor();
            descriptor.setProject(project);
            descriptor.setDescription(description);
            descriptor.setComment(comment);
            descriptor.setFlags(flags);
            descriptor.setResourcePathsToDelete(resourcePaths);
            descriptor.setDeleteContents(Boolean.TRUE.equals(del));
            
            return descriptor;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Can not restore DeleteResourceDescriptor from map"); //$NON-NLS-1$
        }
    }
    
    @Override
    public Map retrieveArgumentMap(RefactoringDescriptor descriptor) {
        if (! (descriptor instanceof DeleteResourcesDescriptor))
            return null;

        DeleteResourcesDescriptor deleteDescriptor = (DeleteResourcesDescriptor) descriptor;
        HashMap<String, String> map = new HashMap<String, String>();

        Collection<IPath> paths = deleteDescriptor.getResourcePaths();
        String project = deleteDescriptor.getProject();
        int i = 0;
        map.put(ATTRIBUTE_NUMBER_OF_RESOURCES, String.valueOf(paths.size()));
        for (IPath path : paths) {
            map.put(ATTRIBUTE_ELEMENT + (i + 1), resourcePathToHandle(project, path));
            i++;
        }

        map.put(ATTRIBUTE_DELETE_CONTENTS, deleteDescriptor.isDeleteContents() ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
        return map;
    }

}
