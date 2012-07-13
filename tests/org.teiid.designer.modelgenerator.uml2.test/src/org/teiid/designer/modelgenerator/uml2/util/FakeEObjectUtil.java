/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.uml2.util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.modelgenerator.util.EObjectUtil;


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
     * @see org.teiid.designer.modelgenerator.util.EObjectUtil#clone(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject clone( EObject object ) {
        return object;
    }

}
