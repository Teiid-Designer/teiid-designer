/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.uml2.aspects.uml;

import java.util.Iterator;
import java.util.List;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Stereotype;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.core.util.CoreStringUtil;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;


/**
 * ColumnAspect
 */
public abstract class AbstractUml2ElementUmlAspect extends AbstractUml2UmlAspect {
    /**
     * Construct an instance of ColumnAspect.
     * @param entity
     */
    public AbstractUml2ElementUmlAspect(MetamodelEntity entity){
        super();
        setMetamodelEntity(entity);
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
	public String getStereotype(Object eObject) {
        final Element element = assertElement(eObject);
        return getStereotype(element);
    }

    protected String getStereotype(Element element) {
        final List stereotypes = element.getAppliedStereotypes();
        if ( stereotypes != null && stereotypes.size() != 0 ) {
            final StringBuffer sb = new StringBuffer();
            appendStereotype(element,sb,false);
            return sb.toString();
        }
        return CoreStringUtil.Constants.EMPTY_STRING;
    }

    protected Element assertElement(Object eObject) {
        CoreArgCheck.isInstanceOf(Element.class, eObject);
        return (Element)eObject;
    }

    protected void appendStereotype( final Element element, final StringBuffer sb,
                                     final boolean includeWrappers ) {
        boolean first = true;
        final List stereotypes = element.getAppliedStereotypes();
        if ( stereotypes.isEmpty() ) {
            return;
        }
        if ( includeWrappers ) {
            sb.append("<<"); //$NON-NLS-1$
        }
        final Iterator iter = stereotypes.iterator();
        while (iter.hasNext()) {
            final Stereotype stereotype = (Stereotype)iter.next();
            if ( !first ) {
                sb.append(","); //$NON-NLS-1$
            }
            first = false;
            sb.append(stereotype.getName());
        }
        if ( includeWrappers ) {
            sb.append(">>"); //$NON-NLS-1$
        }
    }

}
