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

package com.metamatrix.modeler.transformation.aspects.uml;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.transformation.TransformationPlugin;

/**
 * InputSetUmlAspect
 */
public class InputSetUmlAspect extends AbstractTransformationUmlAspect implements UmlClassifier {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID;
    
    /**
     * Construct an instance of InputSetUmlAspect.
     * 
     */
    public InputSetUmlAspect() {
        super();
        setID(ASPECT_ID);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
//        try {
//            InputSet inputSet = assertInputSet(eObject);
//            inputSet.setName(newSignature);
//        } catch (Throwable e) {
//            return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, e.getMessage(), e);
//        }
        
        return new Status(IStatus.OK, TransformationPlugin.PLUGIN_ID, 0, TransformationPlugin.Util.getString("InputSetUmlAspect.Signature_set") + newSignature, null); //$NON-NLS-1$
    }
    
    protected String getName( Object eObject ) {
        return com.metamatrix.metamodels.transformation.TransformationPlugin.Util.getString("_UI_InputSet_type"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     */
    public String getSignature(Object eObject, int showMask) {
        final InputSet inputSet = assertInputSet(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(getName(inputSet));
                break;
            case 2 :
                //Stereotype
//                result.append("<<"); //$NON-NLS-1$
//                result.append(getStereotype(eObject) );
//                result.append(">>"); //$NON-NLS-1$
                break;
            case 3 :
                //Name and Stereotype
//                result.append("<<"); //$NON-NLS-1$
//                result.append(getStereotype(eObject) );
//                result.append(">> "); //$NON-NLS-1$                
            result.append(getName(inputSet));
                break;
            default :
                throw new MetaMatrixRuntimeException(TransformationPlugin.Util.getString("InputSetUmlAspect.Invalid_show_mask_for_getSignature") + showMask); //$NON-NLS-1$
        }
        return result.toString();
    }

    protected InputSet assertInputSet(Object eObject) {
        ArgCheck.isInstanceOf(InputSet.class, eObject);
        return (InputSet)eObject;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
     */
    public Collection getRelationships(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getSupertypes(java.lang.Object)
     */
    public Collection getSupertypes(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return com.metamatrix.metamodels.transformation.TransformationPlugin.Util.getString("_UI_InputSet_type"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     */
    public String getEditableSignature(Object eObject) {
//        return getSignature(eObject,UmlClassifier.SIGNATURE_NAME);
        return ""; //$NON-NLS-1$
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    public boolean isAbstract(Object eObject) {
        return false;
    }
    
}
