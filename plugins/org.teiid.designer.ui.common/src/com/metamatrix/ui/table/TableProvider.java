/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.table;


/**
 * @param <T>
 */
public interface TableProvider<T> {

    /**
     * @param element
     */
    void doubleClicked( T element );

    /**
     * @return the contents of the {@link Table table}
     */
    T[] getElements();

    /**
     * @return <code>true</code> if the {@link Table table} performs some {@link #doubleClicked(Object) action} when one of its rows
     *         are double-clicked.
     */
    boolean isDoubleClickSupported();
}
