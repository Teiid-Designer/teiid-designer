/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.log;

import com.metamatrix.core.log.Logger;

/**
 * 
 */
public class NullLogger implements Logger {

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.core.log.Logger#log(int, java.lang.String)
     */
    @Override
    public void log( int arg0,
                     String arg1 ) {
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.core.log.Logger#log(int, java.lang.Throwable, java.lang.String)
     */
    @Override
    public void log( int arg0,
                     Throwable arg1,
                     String arg2 ) {
    }

}
