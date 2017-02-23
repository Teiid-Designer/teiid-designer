package org.teiid.designer.datasources.ui.sources;

import org.teiid.designer.datasources.ui.UiConstants;
import org.teiid.designer.ui.util.IModelerPerspectiveContributor;
import org.teiid.designer.ui.util.PerspectiveObject;

public class DataSourcesPerspectiveContributor implements IModelerPerspectiveContributor {
    
    PerspectiveObject[] contributions;
    /** 
     * 
     * @since 5.0
     */
    public DataSourcesPerspectiveContributor() {
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
    	
        PerspectiveObject connectorsView = 
            new PerspectiveObject(
                 UiConstants.Extensions.DATASOURCES_VIEW_ID,
                 false,
                 PerspectiveObject.TOP_RIGHT);
        
        contributions[0] = connectorsView;
    }
}
