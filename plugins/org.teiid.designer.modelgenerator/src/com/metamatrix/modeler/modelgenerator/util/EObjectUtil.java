/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * EObjectUtil
 */
public interface EObjectUtil {
    
    public EObject clone(EObject object) throws ModelerCoreException;

}
