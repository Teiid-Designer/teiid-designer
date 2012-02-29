/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.views;

import org.teiid.designer.advisor.ui.AdvisorUiConstants;

import com.metamatrix.modeler.internal.ui.util.IModelerPerspectiveContributor;
import com.metamatrix.modeler.internal.ui.util.PerspectiveObject;

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
     * @see com.metamatrix.modeler.internal.ui.util.IModelerPerspectiveContributor#getContributions()
     * @since 4.3
     */
    public PerspectiveObject[] getContributions() {
        return contributions;
    }
    
    private void createContributions() {
    	contributions = new PerspectiveObject[2];
    	
        PerspectiveObject connectorsView = 
            new PerspectiveObject(
                 AdvisorUiConstants.Extensions.ADVISOR_VIEW_ID,
                 false,
                 PerspectiveObject.TOP_RIGHT);
        
        contributions[0] = connectorsView;
        
        PerspectiveObject statusView = 
                new PerspectiveObject(
                     AdvisorUiConstants.Extensions.STATUS_VIEW_ID,
                     false,
                     PerspectiveObject.TOP_RIGHT);
        contributions[1] = statusView;
    }
}
