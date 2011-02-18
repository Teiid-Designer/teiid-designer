/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.ui.util.IModelerPerspectiveContributor;
import com.metamatrix.modeler.internal.ui.util.PerspectiveObject;


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
     * @see com.metamatrix.modeler.internal.ui.util.IModelerPerspectiveContributor#getContributions()
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
                 PerspectiveObject.LEFT_CENTER);
        contributions = new PerspectiveObject[1];
        contributions[0] = connectorsView;

    }
}
