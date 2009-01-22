/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.util;

import org.eclipse.emf.ecore.EObject;

/**
 * NullSimpleDatatypeUtil
 */
public class NullSimpleDatatypeUtil implements SimpleDatatypeUtil{

    /**
     * Construct an instance of NullSimpleDatatypeUtil.
     * 
     */
    public NullSimpleDatatypeUtil() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.util.SimpleDatatypeUtil#isSimpleDatatypeNumeric(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSimpleDatatypeNumeric(EObject datatype) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.util.SimpleDatatypeUtil#isSimpleDatatypeBinary(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSimpleDatatypeString(EObject datatype) {
        return false;
    }

}
