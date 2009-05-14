/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp;

import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;


/** 
 * @since 4.3
 */
public class MockDqpChangeListener implements IChangeListener{
    
    private boolean isChanged = false;
    
    public MockDqpChangeListener() {}
    
    
    public void stateChanged(IChangeNotifier theSource) {        
        isChanged = true;
        System.out.println("State changed : " + theSource.toString());//$NON-NLS-1$
    }
    
    public boolean isChange() {
        
        boolean newChanged = isChanged;
        isChanged = false;
        
        return newChanged;
    }
}
