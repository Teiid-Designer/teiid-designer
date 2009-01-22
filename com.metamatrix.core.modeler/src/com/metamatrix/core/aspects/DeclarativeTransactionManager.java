/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.aspects;

import java.lang.reflect.Method;


/** 
 * @since 4.1
 */
public interface DeclarativeTransactionManager {
//    boolean beginTxn(String description, Object source);
//    
//    void endTxn();
//    
//    void rollbackDeclarativeTxn();
//    
//    InvocationFactoryHelper[] getInvocationFactoryHelperArray();
//    
    boolean isWritable(Method method);
    
    Object executeInTransaction(Method method, Object target, Object[] parameters);
}
