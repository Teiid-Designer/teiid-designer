/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.EObjectFinder;

/**
 * @since 3.1
 */
public abstract class AbstractFinder implements EObjectFinder {
    //############################################################################################################################
	//# Methods                                                                                                                  #
	//############################################################################################################################

	/**
     * Finds the key associated with the specified proxy via the proxy's handler.  The proxy must be an instance
     * of {@link Proxy} that was created via the MetadataToolKit (i.e., its {@link InvocationHandler} must be an instance of
     * ProxyHandler).
     * @return The key for the specified proxy; never null.
	 * @see EObjectFinder#findKey(java.lang.Object)
     * @since 3.1
	 */
	@Override
	public Object findKey(final Object eObject) {
        CoreArgCheck.isNotNull(eObject);
        CoreArgCheck.isInstanceOf(EObject.class, eObject);
        return ModelerCore.getObjectId((EObject)eObject);
	}
}
