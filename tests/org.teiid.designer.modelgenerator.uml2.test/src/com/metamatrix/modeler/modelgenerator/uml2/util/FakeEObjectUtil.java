/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.uml2.util;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.modelgenerator.util.EObjectUtil;

/**
 * NullEObjectUtil
 */
public class FakeEObjectUtil implements EObjectUtil {

    /**
     * Construct an instance of NullEObjectUtil.
     */
    public FakeEObjectUtil() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.util.EObjectUtil#clone(org.eclipse.emf.ecore.EObject)
     */
    public EObject clone( EObject object ) {
        return object;
    }

}
