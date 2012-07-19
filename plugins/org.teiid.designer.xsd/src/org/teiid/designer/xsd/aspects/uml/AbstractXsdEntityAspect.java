/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xsd.aspects.uml;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect;



/** 
 * @since 8.0
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
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getImage(java.lang.Object)
     * @since 5.0.2
     */
    @Override
	public Object getImage(Object theObject) {
        AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
        IItemLabelProvider provider = (IItemLabelProvider)adapterFactory.adapt(theObject,IItemLabelProvider.class);
        return provider.getImage(theObject);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getVisibility(java.lang.Object)
     * @since 5.0.2
     */
    @Override
	public int getVisibility(Object theObject) {
        return VISIBILITY_PUBLIC;
    }
    
}
