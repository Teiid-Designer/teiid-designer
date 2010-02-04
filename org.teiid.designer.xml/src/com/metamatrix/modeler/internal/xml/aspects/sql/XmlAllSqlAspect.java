/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.aspects.sql;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.xml.XmlDocumentPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * XmlSequenceSqlAspect
 */
public class XmlAllSqlAspect extends XmlContainerNodeSqlAspect {

    /**
     * Construct an instance of XmlSequenceSqlAspect.
     * 
     */
    public XmlAllSqlAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(final EObject eObject) {
        return XmlDocumentPlugin.getPluginResourceLocator().getString("_UI_XmlAll_type"); //$NON-NLS-1$
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
