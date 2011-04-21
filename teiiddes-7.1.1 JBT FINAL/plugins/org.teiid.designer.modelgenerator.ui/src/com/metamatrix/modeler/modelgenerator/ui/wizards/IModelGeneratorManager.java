/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
