/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui;

import com.metamatrix.modeler.internal.ui.util.IModelerPerspectiveContributor;
import com.metamatrix.modeler.internal.ui.util.PerspectiveObject;


/** 
 * @since 4.3
 */
public class RelationshipPerspectiveContributor implements
                                               IModelerPerspectiveContributor {
    
    PerspectiveObject[] contributions;
    /** 
     * 
     * @since 4.3
     */
    public RelationshipPerspectiveContributor() {
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
        PerspectiveObject navigationView = 
            new PerspectiveObject(
                 UiConstants.Extensions.Navigator.VIEW_ID,
                 true,
                 PerspectiveObject.BOTTOM_LEFT);
        contributions = new PerspectiveObject[1];
        contributions[0] = navigationView;
    }

}
