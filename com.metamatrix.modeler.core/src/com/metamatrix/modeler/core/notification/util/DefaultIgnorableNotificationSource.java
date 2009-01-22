/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.notification.util;


/** 
 * @since 5.0
 */
public class DefaultIgnorableNotificationSource implements
                                               IgnorableNotificationSource {

    Object actualSource;
    /** 
     * 
     * @since 5.0
     */
    public DefaultIgnorableNotificationSource() {
        super();
    }
    
    /** 
     * 
     * @since 5.0
     */
    public DefaultIgnorableNotificationSource(Object source) {
        super();
        this.actualSource = source;
    }

    
    /** 
     * @return Returns the actualSource.
     * @since 5.0
     */
    public Object getActualSource() {
        return this.actualSource;
    }

}
