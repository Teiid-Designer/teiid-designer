/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.container;

import com.metamatrix.modeler.core.container.EObjectFinder;

/**
 * @since 3.1
 */
public class FakeFinder implements EObjectFinder {
    //############################################################################################################################
	//# Methods                                                                                                                  #
	//############################################################################################################################

	/**
	 * @see com.metamatrix.api.mtk.core.EObjectFinder#find(java.lang.Object)
     * @since 3.1
	 */
	public Object find(final Object key) {
		return null;
	}

	/**
	 * @see com.metamatrix.api.mtk.core.EObjectFinder#findKey(java.lang.Object)
     * @since 3.1
	 */
	public Object findKey(final Object object) {
		return null;
	}

}
