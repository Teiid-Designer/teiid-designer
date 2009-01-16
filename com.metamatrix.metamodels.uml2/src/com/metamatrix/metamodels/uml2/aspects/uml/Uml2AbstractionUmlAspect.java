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

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.uml2.uml.Abstraction;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * AbstractionAspect
 */
public class Uml2AbstractionUmlAspect extends AbstractUml2DependencyUmlAspect {

    /**
     * Abstraction Aspect
     */
    public Uml2AbstractionUmlAspect( MetamodelEntity entity ) {
        super();
        setMetamodelEntity(entity);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#getSource(java.lang.Object)
     */
    public List getSource( Object relationship ) {
        Abstraction a = assertAbstraction(relationship);
        return a.getSources();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#getTarget(java.lang.Object)
     */
    public List getTarget( Object relationship ) {
        Abstraction a = assertAbstraction(relationship);
        return a.getTargets();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#getEndObjects(java.lang.Object, int)
     */
    public List getEndObjects( Object relationship,
                               int end ) {
        Abstraction a = assertAbstraction(relationship);
        if (end == 0) {
            return a.getSources();
        } else if (end == 1) {
            return a.getTargets();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#isAbstraction(java.lang.Object)
     */
    public boolean isAbstraction( Object relationship ) {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#isUsage(java.lang.Object)
     */
    public boolean isUsage( Object relationship ) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#isPermission(java.lang.Object)
     */
    public boolean isPermission( Object relationship ) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#isRealization(java.lang.Object)
     */
    public boolean isRealization( Object relationship ) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency#isSubstitution(java.lang.Object)
     */
    public boolean isSubstitution( Object relationship ) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype( Object eObject ) {
        return Uml2Plugin.getPluginResourceLocator().getString("_UI_Abstraction_type"); //$NON-NLS-1$;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     */
    public String getSignature( Object eObject,
                                int showMask ) {
        return StringUtil.Constants.EMPTY_STRING;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     */
    public String getEditableSignature( Object eObject ) {
        return StringUtil.Constants.EMPTY_STRING;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature( Object eObject,
                                 String newSignature ) {
        throw new UnsupportedOperationException();
    }

    protected Abstraction assertAbstraction( Object eObject ) {
        ArgCheck.isInstanceOf(Abstraction.class, eObject);
        return (Abstraction)eObject;
    }

}
