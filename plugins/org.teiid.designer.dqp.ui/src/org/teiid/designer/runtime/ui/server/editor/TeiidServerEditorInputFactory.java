/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.server.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * Factory for saving the state of an editing {@link TeiidServerEditorInput}
 */
public class TeiidServerEditorInputFactory implements IElementFactory {

    protected final static String FACTORY_ID = "org.teiid.designer.runtime.ui.server.editor.input.factory"; //$NON-NLS-1$
    
    protected final static String SERVER_URL = "server-url"; //$NON-NLS-1$

    @Override
    public IAdaptable createElement(IMemento memento) {
        // get the resource names
        String serverUrl = memento.getString(SERVER_URL);
        
        return new TeiidServerEditorInput(serverUrl);
    }

    /**
     * Saves the state of an element within a memento.
     *
     * @param memento the storage area for element state
     * @param input server editor input
     */
    public static void saveState(IMemento memento, TeiidServerEditorInput input) {
        if (input == null)
            return;
            
        if (input.getServerUrl() != null)
            memento.putString(SERVER_URL, input.getServerUrl());
    }
}
