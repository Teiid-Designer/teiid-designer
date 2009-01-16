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
