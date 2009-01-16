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

package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelections;

/**
 * ModelGeneratorManager Interface
 */
public interface IModelGeneratorManager {

  public void init();
  
  public ModelWorkspaceSelections getUmlInputSelections();
  
  public ModelWorkspaceSelections getDatatypeSelections();
    
  public boolean hasValidSourceSelections();

  public GeneratorManagerOptions getGeneratorManagerOptions();
       
  public void generateOutputAndMerge(ModelResource targetModel, IProgressMonitor monitor);
  
  public List getDifferenceReports(ModelResource modelResource, IProgressMonitor monitor);
  
  public IStatus performMerge(IProgressMonitor monitor);
  
  public ModelResource getRelationalOutputModel();
  
  public ModelResource getRelationshipsModel();
  
  public void save(IProgressMonitor monitor);
}
