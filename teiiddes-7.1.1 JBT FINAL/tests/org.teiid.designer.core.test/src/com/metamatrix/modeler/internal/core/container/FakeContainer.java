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
public class FakeContainer extends ContainerImpl {
    //############################################################################################################################
	//# Methods                                                                                                                  #
	//############################################################################################################################
    
	/**
	 * @see com.metamatrix.mtk.core.impl.AbstractContainer#createDefaultEObjectFinder()
     * @since 3.1
	 */
	@Override
    protected EObjectFinder createDefaultEObjectFinder() {
        return new FakeFinder();
	}
}
