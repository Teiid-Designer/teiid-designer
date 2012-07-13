/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.uml;

import java.util.Collection;
import java.util.Collections;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.relational.RelationalPlugin;


/**
 * ViewAspect
 */
public class ViewAspect extends TableAspect {
    /**
     * Construct an instance of ViewAspect.
     * 
     * @param entity
     */
    public ViewAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
    public String getStereotype( Object eObject ) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_View_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
     */
    @Override
	public Collection getRelationships( Object eObject ) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    @Override
	public boolean isAbstract( Object eObject ) {
        return false;
    }

}
