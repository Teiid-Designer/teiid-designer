/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.uml;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect;


/**
 * RelationalEntityAspect
 *
 * @since 8.0
 */
public abstract class RelationalEntityAspect extends AbstractMetamodelAspect implements UmlDiagramAspect {
    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID;
    
    protected RelationalEntityAspect(){
        setID(ASPECT_ID);
    }
    
    @Override
	public int getVisibility(Object eObject) {
        return VISIBILITY_PUBLIC;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getImage(java.lang.Object)
     * @since 4.2
     */
    @Override
	public Object getImage(Object eObject) {
        // get the adapter factory
        final AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
		// lookup item provider for the eobjet
		final IItemLabelProvider provider = (IItemLabelProvider)adapterFactory.adapt(eObject,IItemLabelProvider.class);
		// look up image
		return provider.getImage(eObject);
    }
    
    public String getArrayAsString(final String[] array) {
        if (array == null || array.length == 0) {
            return CoreStringUtil.Constants.EMPTY_STRING;
        }
        final StringBuffer sb = new StringBuffer(100);
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < (array.length-1)) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

}
