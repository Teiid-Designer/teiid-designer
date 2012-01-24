/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.core.status;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

/**
 * 
 */
public class AdvisorStatus extends MultiStatus {

    private Map<String, IStatus> statusMap = new HashMap<String, IStatus>();

    private Object currentObject;

    /**
     * @param thePluginId
     * @param theCode
     * @param theNewChildren
     * @param theMessage
     * @param theException
     * @since 4.3
     */
    public AdvisorStatus( String thePluginId,
                          int theCode,
                          IStatus[] theNewChildren,
                          String theMessage,
                          Throwable theException ) {
        super(thePluginId, theCode, theNewChildren, theMessage, theException);
    }

    /**
     * @param thePluginId
     * @param theCode
     * @param theMessage
     * @param theException
     * @since 4.3
     */
    public AdvisorStatus( String thePluginId,
                          int theCode,
                          String theMessage,
                          Throwable theException ) {
        super(thePluginId, theCode, theMessage, theException);
    }

    /**
     * @see org.eclipse.core.runtime.MultiStatus#add(org.eclipse.core.runtime.IStatus)
     * @since 4.3
     */
    public void add( int id,
                     IStatus theStatus ) {
        super.add(theStatus);
        this.statusMap.put(Integer.toString(id), theStatus);
    }

    public IStatus get( int id ) {
        return this.statusMap.get(Integer.toString(id));
    }

    /**
     * @return currentObject
     */
    public Object getCurrentObject() {
        return currentObject;
    }

    /**
     * @param currentObject Sets currentObject to the specified value.
     */
    public void setCurrentObject( Object currentObject ) {
        this.currentObject = currentObject;
    }

}
