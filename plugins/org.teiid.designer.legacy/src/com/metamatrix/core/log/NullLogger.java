/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.log;

import org.teiid.logging.Logger;

/**
 * 
 */
public class NullLogger implements Logger {

    /**
     *
     */
    public void log( int arg0,
                     String arg1 ) {
    }

    /**
     *
     */
    public void log( int arg0,
                     Throwable arg1,
                     String arg2 ) {
    }

	@Override
	public boolean isEnabled(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void log(int arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(int arg0, String arg1, Throwable arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
