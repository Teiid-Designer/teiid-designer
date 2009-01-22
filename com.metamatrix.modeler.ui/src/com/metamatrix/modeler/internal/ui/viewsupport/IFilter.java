/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
