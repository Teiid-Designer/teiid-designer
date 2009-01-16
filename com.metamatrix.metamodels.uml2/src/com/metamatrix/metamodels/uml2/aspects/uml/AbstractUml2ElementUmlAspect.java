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

package com.metamatrix.metamodels.uml2.aspects.uml;

import java.util.Iterator;
import java.util.List;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Stereotype;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

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
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
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
        return StringUtil.Constants.EMPTY_STRING;
    }

    protected Element assertElement(Object eObject) {
        ArgCheck.isInstanceOf(Element.class, eObject);
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
