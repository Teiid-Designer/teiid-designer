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
public abstract class DefaultTableProvider<T> implements TableProvider<T> {

    /**
     * {@inheritDoc}
     * <p>
     * This implementation does nothing.
     * </p>
     * 
     * @see com.metamatrix.ui.table.TableProvider#doubleClicked(java.lang.Object)
     */
    @Override
    public void doubleClicked( final T element ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>false</code>
     * @see com.metamatrix.ui.table.TableProvider#isDoubleClickSupported()
     */
    @Override
    public boolean isDoubleClickSupported() {
        return false;
    }
}
