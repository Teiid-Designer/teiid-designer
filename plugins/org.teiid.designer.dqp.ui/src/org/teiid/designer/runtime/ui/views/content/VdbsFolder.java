/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import org.teiid.designer.runtime.ui.DqpUiConstants;

/**
 *
 */
public class VdbsFolder extends TeiidFolder {

    private static final String VDBS_FOLDER_NAME = DqpUiConstants.UTIL.getString(VdbsFolder.class.getSimpleName() + ".label"); //$NON-NLS-1$
    
    /**
     * Create new instance
     * 
     * @param parentNode
     * @param values
     */
    public VdbsFolder(TeiidServerContainerNode parentNode, Object[] values ) {
        super(parentNode, values);
    }

    @Override
    public String getName() {
        return VDBS_FOLDER_NAME;
    }
}
