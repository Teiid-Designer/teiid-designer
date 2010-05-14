/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.transaction;

import com.metamatrix.modeler.core.ModelerCore;

/**
 * @author Lance Phillips
 *
 * @since 3.1
 */
public class TransactionStateConstants {
    public static final int UNINITIALIZED = -1;
    public static final int STARTED = 1;
    public static final int COMMITTING = 2;
    public static final int ROLLING_BACK = 3;
    public static final int COMPLETE = 4;
    public static final int FAILED = 5;
    public static final String UNINITIALIZED_STRING = ModelerCore.Util.getString("TransactionStateConstants.Unitinitialized_1"); //$NON-NLS-1$
    public static final String STARTED_STRING = ModelerCore.Util.getString("TransactionStateConstants.Started_2"); //$NON-NLS-1$
    public static final String COMMITTING_STRING = ModelerCore.Util.getString("TransactionStateConstants.Committing_3"); //$NON-NLS-1$
    public static final String ROLLING_BACK_STRING = ModelerCore.Util.getString("TransactionStateConstants.Rolling_back_4"); //$NON-NLS-1$
    public static final String COMPLETE_STRING = ModelerCore.Util.getString("TransactionStateConstants.Complete_5"); //$NON-NLS-1$
    public static final String FAILED_STRING = ModelerCore.Util.getString("TransactionStateConstants.Failed_6"); //$NON-NLS-1$
    public static final String UNKNOWN_STRING = ModelerCore.Util.getString("TransactionStateConstants.Unknown_7"); //$NON-NLS-1$
    
    public static String getDisplayValue(final int code){
        switch (code) {
            case -1 : return UNINITIALIZED_STRING;
            case 1  : return STARTED_STRING;
            case 2  : return COMMITTING_STRING;
            case 3  : return ROLLING_BACK_STRING;
            case 4  : return COMPLETE_STRING;
            case 5  : return FAILED_STRING;
            default :
                return UNKNOWN_STRING;
        }
    }
}
