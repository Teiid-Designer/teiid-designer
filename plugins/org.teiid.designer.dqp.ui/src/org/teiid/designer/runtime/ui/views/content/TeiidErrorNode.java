/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import org.jboss.ide.eclipse.as.ui.views.as7.management.content.ContentNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IContainerNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IErrorNode;
import org.teiid.designer.runtime.TeiidServer;

/**
 * @since 8.0
 */
public class TeiidErrorNode extends ContentNode<IContainerNode<?>> implements IErrorNode {

    private final String text;
    private final TeiidServer teiidServer;

    /**
     * @param container
     * @param text
     */
    protected TeiidErrorNode(IContainerNode parent, TeiidServer teiidServer, String text) {
        super(parent, text);
        this.teiidServer = teiidServer;
        this.text = text;
    }
    
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getText() {
        return text;
    }
    
    /**
     * Get the {@link TeiidServer} associated with this error
     * 
     * @return {@link TeiidServer} or null if a server has not yet been adapted.
     */
    public TeiidServer getTeiidServer() {
        return teiidServer;
    }

    

}
