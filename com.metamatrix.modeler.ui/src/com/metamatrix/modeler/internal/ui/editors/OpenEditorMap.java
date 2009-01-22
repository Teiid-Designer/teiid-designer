/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.editors;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * OpenEditorMap
 */
public class OpenEditorMap {
    
    private static final OpenEditorMap instance = new OpenEditorMap();
    
    public static OpenEditorMap getInstance() {
        return instance;
    }
    
    /** key = ModelResource, value = ModelEditor */
    private HashMap modelEditorMap = new HashMap();
    /** key = IFile, value = ModelEditor */
    private HashMap fileEditorMap = new HashMap();
    /** key = Resource, value = ModelEditor */
    private HashMap resourceEditorMap = new HashMap();

    private OpenEditorMap() {
    }

    public synchronized boolean isEditorOpen(ModelResource modelResource) {
        return modelEditorMap.keySet().contains(modelResource);
    }

    public synchronized boolean isEditorOpen(IFile modelFile) {
        return fileEditorMap.keySet().contains(modelFile);
    }

    public synchronized boolean isEditorOpen(Resource emfResource) {
        return resourceEditorMap.keySet().contains(emfResource);
    }

    public synchronized ModelEditor getModelEditor(ModelResource modelResource) {
        return (ModelEditor) modelEditorMap.get(modelResource);
    }

    public synchronized ModelEditor getModelEditor(IFile modelFile) {
        return (ModelEditor) fileEditorMap.get(modelFile);
    }

    public synchronized ModelEditor getModelEditor(Resource emfResource) {
        return (ModelEditor) resourceEditorMap.get(emfResource);
    }

    synchronized void addModelEditor(ModelEditor editor, ModelResource resource) throws ModelWorkspaceException {
        modelEditorMap.put(resource, editor);
        fileEditorMap.put(resource.getResource(), editor);
        resourceEditorMap.put(resource.getEmfResource(), editor);
    }

    synchronized void removeModelEditor(ModelResource resource) throws ModelWorkspaceException {
        modelEditorMap.remove(resource);
        fileEditorMap.remove(resource.getResource());
        resourceEditorMap.remove(resource.getEmfResource());
    }
}
