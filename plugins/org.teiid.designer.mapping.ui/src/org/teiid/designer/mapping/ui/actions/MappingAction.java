/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.mapping.factory.MappingClassFactory;
import org.teiid.designer.mapping.ui.UiPlugin;
import org.teiid.designer.mapping.ui.util.MappingUiUtil;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.MappingClassColumn;
import org.teiid.designer.metamodels.transformation.StagingTable;
import org.teiid.designer.ui.actions.ModelObjectAction;


/**
 * MappingAction provides transformation-specific helper methods.
 */
public class MappingAction extends ModelObjectAction {

    private EObject transformationEObject;
    private MappingClassFactory mappingClassFactory;
    protected DiagramEditor editor;
    private boolean isDetailed = false;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct an instance of MappingAction.
     */
    public MappingAction() {
        super(UiPlugin.getDefault());
    }

    /**
     * Construct an instance of MappingAction.
     */
    public MappingAction( UiPlugin plugin,
                          int iStyle ) {
        super(plugin, iStyle);
    }

    /**
     * Construct an instance of MappingAction.
     */
    public MappingAction( EObject transformationEObject ) {
        super(UiPlugin.getDefault());
        this.transformationEObject = transformationEObject;
    }

    public EObject getTransformation() {
        return transformationEObject;
    }

    public void setTransformation( EObject transformationEObject ) {
        this.transformationEObject = transformationEObject;
    }

    @Override
    protected void doRun() {
    }

    public void setDiagramEditor( DiagramEditor editor ) {
        this.editor = editor;
    }

    /**
     * @return
     */
    public MappingClassFactory getMappingClassFactory() {

        // jh Defect 21277: Ensure that we are using an up to date mcf, and that
        // any mods we do are done to the right TreeMappingAdapter.
        mappingClassFactory = MappingUiUtil.getCurrentMappingClassFactory();
        return mappingClassFactory;
    }

    /**
     * @param factory
     */
    public void setMappingClassFactory( MappingClassFactory factory ) {
        mappingClassFactory = factory;
    }

    public boolean isMappingClass( EObject eObject ) {
        return eObject instanceof MappingClass;
    }

    public boolean isStagingTable( EObject eObject ) {
        return eObject instanceof StagingTable;
    }

    public boolean isMappingClassColumn( EObject eObject ) {
        return eObject instanceof MappingClassColumn;
    }

    protected void setDetailed( boolean mode ) {
        this.isDetailed = mode;
    }

    public boolean isDetailed() {
        return isDetailed;
    }

    protected boolean isWritable() {
        return !isReadOnly();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return false;
    }

}
