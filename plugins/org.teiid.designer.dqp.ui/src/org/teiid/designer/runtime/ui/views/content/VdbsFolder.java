/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import java.util.Collection;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.DqpUiConstants;

/**
 *
 */
public class VdbsFolder extends AbstractTeiidFolder<ITeiidVdb> {

    private static final String VDBS_FOLDER_NAME = DqpUiConstants.UTIL.getString(VdbsFolder.class.getSimpleName() + ".label"); //$NON-NLS-1$
    
    /**
     * Create new instance
     * 
     * @param parentNode
     * @param vdbs
     */
    public VdbsFolder(TeiidServerContainerNode parentNode, Collection<ITeiidVdb> vdbs) {
        super(parentNode, vdbs);
    }

    @Override
    public String getName() {
        return VDBS_FOLDER_NAME;
    }
}
