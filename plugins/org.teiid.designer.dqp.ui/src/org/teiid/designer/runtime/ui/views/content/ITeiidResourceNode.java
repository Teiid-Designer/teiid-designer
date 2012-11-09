/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import org.teiid.designer.runtime.TeiidServer;


/**
 *
 */
public interface ITeiidResourceNode extends ITeiidContainerNode {

    /** Represents the root of the management tree. */
    public static final String ROOT_TYPE = "root"; //$NON-NLS-1$
    
    /** 
     * @return TeiidServer
     */
    TeiidServer getTeiidServer();

}
