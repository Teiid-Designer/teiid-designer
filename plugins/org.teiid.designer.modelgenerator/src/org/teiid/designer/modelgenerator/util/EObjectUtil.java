/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;

/**
 * EObjectUtil
 *
 * @since 8.0
 */
public interface EObjectUtil {
    
    public EObject clone(EObject object) throws ModelerCoreException;

}
