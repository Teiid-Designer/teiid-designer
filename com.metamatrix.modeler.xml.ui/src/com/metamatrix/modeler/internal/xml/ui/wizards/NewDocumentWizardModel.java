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

package com.metamatrix.modeler.internal.xml.ui.wizards;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.mapping.factory.MappingClassBuilderStrategy;
import com.metamatrix.modeler.xml.IVirtualDocumentFragmentSource;
import com.metamatrix.modeler.xml.ModelerXmlPlugin;
import com.metamatrix.modeler.xml.PluginConstants;


/**
 * @author PForhan
 */
public class NewDocumentWizardModel {
    private Composite                      wizHolder;
    private IVirtualDocumentFragmentSource source;
    private XmlFragment[]                  fragments;
    private int                            selectedFragmentCount;
    private boolean                        fragmentsNeedUpdating = true;
    private boolean                        buildEntireDocuments;
    private boolean                        buildMappingClasses;
    private int                            estimatedNodeCount;
    private boolean                        useSchemaTypes = true;
    private boolean                        buildGlobalOnly; // Note: not currently read by anyone!
    private Collection                     unhandledModelImports;
    private MappingClassBuilderStrategy    strategy;
//  may not be needed:
//  public IDocumentsAndFragmentsPopulator populator;
    
    public NewDocumentWizardModel(){
        ModelerXmlPlugin plugin = ModelerXmlPlugin.getDefault();
        if(plugin != null) {
            plugin.getPluginPreferences().setDefault(PluginConstants.PreferenceKeys.MAPPING_TYPE_FROM_XSD, true);
        }
    }
    //
    // Data methods:
    //
    public XmlFragment[] getFragments(ModelResource modelResource, IProgressMonitor monitor) {
        if (fragmentsNeedUpdating
         || fragments == null) {
            fragments = source.getFragments(modelResource, monitor);
            fragmentsNeedUpdating = false;
        } // endif
        
        return fragments;
    }
    public void setBuildEntireDocuments(boolean buildEntireDocuments) {
        // only operate if this is a change:
        if (this.buildEntireDocuments != buildEntireDocuments) {
            fragmentsNeedUpdating = true; // indicate that the fragments have changed
            this.buildEntireDocuments = buildEntireDocuments;
        } // endif
    }
    public boolean getBuildEntireDocuments() {
        return buildEntireDocuments;
    }
    public void setSource(IVirtualDocumentFragmentSource source) {
        if (this.source != source) {
            fragmentsNeedUpdating = true; // indicate that the fragments have changed
            this.source = source;
        } // endif
    }
    public IVirtualDocumentFragmentSource getSource() {
        return source;
    }
    public void setSelectedFragmentCount(int selectedFragmentCount) {
        // only operate if this is a change:
        if (this.selectedFragmentCount != selectedFragmentCount) {
            fragmentsNeedUpdating = true; // indicate that the fragments have changed
            this.selectedFragmentCount = selectedFragmentCount;
        } // endif
    }
    public int getSelectedFragmentCount() {
        return selectedFragmentCount;
    }
    public void setBuildMappingClasses(boolean buildMappingClasses) {
        // has no effect upon fragmentsNeedsUpdating
        this.buildMappingClasses = buildMappingClasses;
    }
    public boolean getBuildMappingClasses() {
        return buildMappingClasses;
    }
    public void setEstimatedNodeCount(int estimatedNodeCount) {
        // has no effect upon fragmentsNeedsUpdating
        this.estimatedNodeCount = estimatedNodeCount;
    }
    public int getEstimatedNodeCount() {
        return estimatedNodeCount;
    }
    public void setUseSchemaTypes(boolean useSchemaTypes) {
        // only operate if this is a change:
        if (this.useSchemaTypes != useSchemaTypes) {
            fragmentsNeedUpdating = true; // indicate that the fragments have changed
            this.useSchemaTypes = useSchemaTypes;
        } // endif
    }
    public boolean getUseSchemaTypes() {
        return useSchemaTypes;
    }
    public void setBuildGlobalOnly(boolean buildGlobalOnly) {
        // only operate if this is a change:
        if (this.buildGlobalOnly != buildGlobalOnly) {
            fragmentsNeedUpdating = true; // indicate that the fragments have changed
            this.buildGlobalOnly = buildGlobalOnly;
        } // endif
    }
    public boolean getBuildGlobalOnly() {
        return buildGlobalOnly;
    }
    
    /** Allows access to the container holding wizard page controls */
    public Composite getWizHolder() {
        return wizHolder;
    }
    public void setWizHolder(Composite wizHolder) {
        this.wizHolder = wizHolder;
    }
    
    public void setReferencedResources(final Collection refs) {
        this.unhandledModelImports = refs;
    }
    
    public Collection getUnhandledModelImports() {
        if(unhandledModelImports == null) {
            unhandledModelImports = new HashSet();
        }
        return unhandledModelImports;
    }
    
    public void setMappingClassBuilderStrategy(MappingClassBuilderStrategy strategy) {
        this.strategy = strategy;
    }
    
    public MappingClassBuilderStrategy getMappingClassBuilderStrategy( ) {
        return this.strategy;
    }
}
