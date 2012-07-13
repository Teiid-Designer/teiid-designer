/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui;

import org.teiid.designer.ui.util.IModelerPerspectiveContributor;
import org.teiid.designer.ui.util.PerspectiveObject;


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
     * @see org.teiid.designer.ui.util.IModelerPerspectiveContributor#getContributions()
     * @since 4.3
     */
    @Override
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
