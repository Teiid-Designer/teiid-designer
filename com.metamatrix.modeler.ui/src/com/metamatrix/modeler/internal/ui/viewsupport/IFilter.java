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

package com.metamatrix.modeler.internal.ui.viewsupport;

/**
 * @since 4.3
 */
public interface IFilter {

    // ===========================================================================================================================
    // Constants

    interface IConstants {
        
        /**
         * A filter that blocks all objects.
         * @since 4.3
         */
        IFilter BLOCKING_FILTER = new IFilter() {

            /**
             * @see com.metamatrix.modeler.internal.ui.viewsupport.IFilter#passes(java.lang.Object)
             * @since 4.3
             */
            public boolean passes(final Object object) {
                return false;
            }
        };

        /**
         * A filter that passes all objects.
         * @since 4.3
         */
        IFilter PASSING_FILTER = new IFilter() {

            /**
             * @see com.metamatrix.modeler.internal.ui.viewsupport.IFilter#passes(java.lang.Object)
             * @since 4.3
             */
            public boolean passes(final Object object) {
                return true;
            }
        };
    }

    // ===========================================================================================================================
    // Controller Methods

    /**
     * @param object
     *            The object to be tested.
     * @return True if the object passes through the filter.
     * @since 4.3
     */
    boolean passes(Object object);
}
