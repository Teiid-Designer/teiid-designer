/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.EObjectFinder;

/**
 * @since 3.1
 */
public abstract class AbstractFinder implements EObjectFinder {
    //############################################################################################################################
	//# Methods                                                                                                                  #
	//############################################################################################################################

	/**
     * Finds the key associated with the specified proxy via the proxy's {@link ProxyHandler}.  The proxy must be an instance
     * of {@link Proxy} that was created via the MetadataToolKit (i.e., its {@link InvocationHandler} must be an instance of
     * ProxyHandler).
     * @param proxy The proxy for which to find a key; may not be null.
     * @return The key for the specified proxy; never null.
	 * @see com.metamatrix.api.mtk.core.EObjectFinder#findKey(java.lang.Object)
     * @since 3.1
	 */
	public Object findKey(final Object eObject) {
        ArgCheck.isNotNull(eObject);
        ArgCheck.isInstanceOf(EObject.class, eObject);
        return ModelerCore.getObjectId((EObject)eObject);
	}
}
