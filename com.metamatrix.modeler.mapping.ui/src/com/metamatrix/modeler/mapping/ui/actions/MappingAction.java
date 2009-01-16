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

package com.metamatrix.modeler.mapping.ui.actions;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.internal.mapping.factory.MappingClassFactory;
import com.metamatrix.modeler.internal.ui.actions.ModelObjectAction;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.util.MappingUiUtil;

/**
 * MappingAction provides transformation-specific helper methods.
 */
public class MappingAction extends ModelObjectAction   {
    
    private EObject transformationEObject;
    private MappingClassFactory mappingClassFactory;
    protected DiagramEditor editor;
    private boolean isDetailed = false;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Construct an instance of MappingAction.
     * 
     */
    public MappingAction() {
        super(UiPlugin.getDefault());
    }
    
    /**
     * Construct an instance of MappingAction.
     * 
     */
    public MappingAction( UiPlugin plugin, int iStyle ) {
        super( plugin, iStyle );
    }

    /**
     * Construct an instance of MappingAction.
     * 
     */
    public MappingAction(DiagramEditor editor) {
        super(UiPlugin.getDefault());
        this.editor = editor;
    }
    
    /**
     * Construct an instance of MappingAction.
     * 
     */
    public MappingAction(MappingClassFactory factory) {
        super(UiPlugin.getDefault());
        this.mappingClassFactory = factory;
    }
    
    /**
     * Construct an instance of MappingAction.
     * 
     */
    public MappingAction(EObject transformationEObject) {
        super(UiPlugin.getDefault());
        this.transformationEObject = transformationEObject;
    }
    
    public EObject getTransformation() {
        return transformationEObject;
    }
    
    public void setTransformation(EObject transformationEObject) {
        this.transformationEObject = transformationEObject;
    }
    

    @Override
    protected void doRun() {
    }
    
    
    public void setDiagramEditor(DiagramEditor editor) {
        this.editor = editor;
    }
    
    /**
     * @return
     */
    public MappingClassFactory getMappingClassFactory() {
        
        // jh Defect 21277: Ensure that we are using an up to date mcf, and that
        //                  any mods we do are done to the right TreeMappingAdapter.
        mappingClassFactory = MappingUiUtil.getCurrentMappingClassFactory();
        return mappingClassFactory;
    }

    /**
     * @param factory
     */
    public void setMappingClassFactory(MappingClassFactory factory) {
        mappingClassFactory = factory;
    }
    
    public boolean isMappingClass(EObject eObject) {
        return eObject instanceof MappingClass;
    }
    
    public boolean isStagingTable(EObject eObject) {
        return eObject instanceof StagingTable;
    }
    
    public boolean isMappingClassColumn(EObject eObject) {
        return eObject instanceof MappingClassColumn;
    }
    
    protected void setDetailed(boolean mode) {
        this.isDetailed = mode;
    }
    
    public boolean isDetailed() {
        return isDetailed;
    }

    protected boolean isWritable() {
        return ! isReadOnly();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return false;
    }

}
