/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.ArrayList;
import java.util.Collection;
import com.metamatrix.modeler.core.transaction.Undoable;
import com.metamatrix.modeler.core.transaction.UndoableListener;

/**
 * FakeUndoableListener
 */
public class FakeUndoableListener implements UndoableListener {
    final Collection undoables;   
    /**
     * Construct an instance of FakeUndoableListener.
     * 
     */
    public FakeUndoableListener() {
        undoables = new ArrayList();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.UndoableListener#processEvent(com.metamatrix.modeler.core.transaction.UndoableEditEvent)
     */
    public void process(Undoable undoable) {
        if(undoable.isSignificant() ){
            undoables.add(undoable);
        }
    }
    
    public Collection getUndoables(){
        return undoables;
    }

}
