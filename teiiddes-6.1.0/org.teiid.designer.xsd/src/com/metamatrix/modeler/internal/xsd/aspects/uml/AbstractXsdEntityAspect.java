/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.aspects.uml;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect;


/** 
 * @since 5.0.2
 */
public abstract class AbstractXsdEntityAspect extends AbstractMetamodelAspect
                                              implements UmlDiagramAspect {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    // ID must match the extension point ID
    public static final String ASPECT_ID = "umlDiagramAspect"; //$NON-NLS-1$
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    protected AbstractXsdEntityAspect(MetamodelEntity theEntity) {
        setID(ASPECT_ID);
        setMetamodelEntity(theEntity);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getImage(java.lang.Object)
     * @since 5.0.2
     */
    public Object getImage(Object theObject) {
        AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
        IItemLabelProvider provider = (IItemLabelProvider)adapterFactory.adapt(theObject,IItemLabelProvider.class);
        return provider.getImage(theObject);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getVisibility(java.lang.Object)
     * @since 5.0.2
     */
    public int getVisibility(Object theObject) {
        return VISIBILITY_PUBLIC;
    }
    
}
