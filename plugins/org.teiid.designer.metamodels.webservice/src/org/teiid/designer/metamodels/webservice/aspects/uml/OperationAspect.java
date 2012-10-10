/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.uml;

import java.util.Collection;
import java.util.Collections;

import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.metamodels.webservice.Operation;
import org.teiid.designer.metamodels.webservice.WebServiceComponent;
import org.teiid.designer.metamodels.webservice.WebServiceMetamodelPlugin;


/**
 * @since 8.0
 */
public class OperationAspect extends WebServiceComponentAspect implements UmlClassifier {

    /**
     * @param entity
     * @since 4.2
     */
    public OperationAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
     * @since 4.2
     */
    @Override
	public Collection getRelationships( Object eObject ) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getSupertypes(java.lang.Object)
     * @since 4.2
     */
    @Override
	public Collection getSupertypes( Object eObject ) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     * @since 4.2
     */
    @Override
	public boolean isAbstract( Object eObject ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     * @since 4.2
     */
    @Override
	public String getStereotype( Object eObject ) {
        return WebServiceMetamodelPlugin.Util.getString("_UI_Operation_type"); //$NON-NLS-1$
    }

    protected Operation assertOperation( Object eObject ) { // NO_UCD
        CoreArgCheck.isInstanceOf(Operation.class, eObject);
        return (Operation)eObject;
    }

    /**
     * @see org.teiid.designer.metamodels.webservice.aspects.uml.WebServiceComponentAspect#assertWebServiceComponent(java.lang.Object)
     * @since 4.2
     */
    @Override
    protected WebServiceComponent assertWebServiceComponent( Object eObject ) {
        CoreArgCheck.isInstanceOf(Operation.class, eObject);
        return (Operation)eObject;
    }
}
