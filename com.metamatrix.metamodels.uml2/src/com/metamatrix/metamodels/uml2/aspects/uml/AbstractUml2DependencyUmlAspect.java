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

import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Dependency;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency;

/**
 * AbstractUml2UmlAspect
 */
public abstract class AbstractUml2DependencyUmlAspect extends AbstractUml2UmlAspect implements UmlDependency {
        
    protected AbstractUml2DependencyUmlAspect(){
        super();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getName(java.lang.Object)
     */
    public String getName(Object eObject) {
        Dependency d = assertDependency(eObject);
        final String name = d.getName();
        return ( name != null ? name : StringUtil.Constants.EMPTY_STRING);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getToolTip(java.lang.Object)
     */
    public String getToolTip(Object eObject) {
        final StringBuffer sb = new StringBuffer(200);
        sb.append(this.getStereotype(eObject));
        sb.append(' ');
        List sources = this.getSource(eObject);
        List targets = this.getTarget(eObject);
        sb.append(this.getListAsString(sources));
        sb.append(" --> "); //$NON-NLS-1$
        sb.append(this.getListAsString(targets));
        return sb.toString();
    }
    
    protected String getListAsString(final List list) {
        final StringBuffer sb = new StringBuffer(200);
        sb.append('[');
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            final Classifier classifier = (Classifier)iter.next();
            if (classifier != null) {
                sb.append(classifier.getName());
            }
            if (iter.hasNext()) {
                sb.append(',');
                sb.append(' ');
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    protected Dependency assertDependency(Object eObject) {
        ArgCheck.isInstanceOf(Dependency.class, eObject);
        return (Dependency) eObject;
    }

}
