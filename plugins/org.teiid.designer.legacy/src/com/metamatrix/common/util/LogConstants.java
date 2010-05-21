/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.util;

public interface LogConstants {
	// add the new contexts to the Log4JUtil.java class, for configuration purpose
	public static final String CTX_CONFIG = "CONFIG"; //$NON-NLS-1$
	public static final String CTX_COMMUNICATION = "COMMUNICATION"; //$NON-NLS-1$
    public static final String CTX_POOLING = "RESOURCE_POOLING"; //$NON-NLS-1$
	public static final String CTX_SESSION = "SESSION"; //$NON-NLS-1$
	public static final String CTX_MEMBERSHIP = "MEMBERSHIP"; //$NON-NLS-1$
	public static final String CTX_AUTHORIZATION = "AUTHORIZATION"; //$NON-NLS-1$
	public static final String CTX_AUTHORIZATION_ADMIN_API = "AUTHORIZATION_ADMIN_API"; //$NON-NLS-1$
	public static final String CTX_SERVER= "Server"; //$NON-NLS-1$
	public static final String CTX_ADMIN = "ADMIN"; //$NON-NLS-1$	
}
