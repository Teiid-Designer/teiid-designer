/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal;

/** 
 * @since 4.1
 */
public interface IMessageListener {
    //============================================================================================================================
    // Controller Methods
    
    /**
     * @param message
     * @since 4.1
     */
    void messageSent(final IMessage message);
}
