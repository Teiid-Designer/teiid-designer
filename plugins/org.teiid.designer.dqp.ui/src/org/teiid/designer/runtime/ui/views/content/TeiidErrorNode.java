/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import org.teiid.designer.runtime.spi.ITeiidServer;


/**
 * @since 8.0
 */
public class TeiidErrorNode extends TeiidContentNode<ITeiidContainerNode<?>> {

    private final String text;
    private final ITeiidServer teiidServer;

    /**
     * @param container
     * @param text
     */
    protected TeiidErrorNode(ITeiidContainerNode parent, ITeiidServer teiidServer, String text) {
        super(parent, text);
        this.teiidServer = teiidServer;
        this.text = text;
    }
    
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * Return the error text
     * 
     * @return error text
     */
    public String getText() {
        return text;
    }
    
    /**
     * Get the {@link ITeiidServer} associated with this error
     * 
     * @return {@link ITeiidServer} or null if a server has not yet been adapted.
     */
    public ITeiidServer getTeiidServer() {
        return teiidServer;
    }

    @Override
    public String toString() {
        return text;
    }
}
