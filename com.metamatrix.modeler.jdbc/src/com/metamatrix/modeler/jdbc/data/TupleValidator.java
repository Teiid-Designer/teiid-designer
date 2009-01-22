/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.data;

import java.util.List;


/** 
 * @since 4.3
 */
public interface TupleValidator {
    /**
     * Whether the given tuple from the result set is valid
     * @return True if it is valid, false otherwise
     * @since 4.3
     */
    boolean isTupleValid(List tuple);
}
