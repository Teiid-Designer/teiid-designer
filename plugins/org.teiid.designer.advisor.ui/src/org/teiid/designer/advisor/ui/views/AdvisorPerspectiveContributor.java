/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.views;

import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.ui.util.IModelerPerspectiveContributor;
import org.teiid.designer.ui.util.PerspectiveObject;


public class AdvisorPerspectiveContributor implements IModelerPerspectiveContributor {
    
    PerspectiveObject[] contributions;
    /** 
     * 
     * @since 5.0
     */
    public AdvisorPerspectiveContributor() {
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
    	contributions = new PerspectiveObject[1];
    	
        PerspectiveObject guidesView = 
            new PerspectiveObject(
                 AdvisorUiConstants.Extensions.GUIDES_VIEW_ID,
                 false,
                 PerspectiveObject.TOP_LEFT);
        
        contributions[0] = guidesView;
        
//        PerspectiveObject statusView = 
//                new PerspectiveObject(
//                     AdvisorUiConstants.Extensions.STATUS_VIEW_ID,
//                     false,
//                     PerspectiveObject.BOTTOM_LEFT);
//        contributions[1] = statusView;
    }
}
