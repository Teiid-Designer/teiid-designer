/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.views;

import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.ui.util.IModelerPerspectiveContributor;
import org.teiid.designer.ui.util.PerspectiveObject;



/** 
 * @since 5.0
 */
public class TeiidPerspectiveContributor implements IModelerPerspectiveContributor {
    
    PerspectiveObject[] contributions;
    /** 
     * 
     * @since 5.0
     */
    public TeiidPerspectiveContributor() {
        super();
        createContributions();
    }

    /** 
     * @see org.teiid.designer.ui.util.IModelerPerspectiveContributor#getContributions()
     * @since 4.3
     */
    public PerspectiveObject[] getContributions() {
        return contributions;
    }
    
    private void createContributions() {
        PerspectiveObject connectorsView = 
            new PerspectiveObject(
                 DqpUiConstants.Extensions.CONNECTORS_VIEW_ID,
                 false,
                 PerspectiveObject.BOTTOM_RIGHT);
        contributions = new PerspectiveObject[1];
        contributions[0] = connectorsView;

    }
}
