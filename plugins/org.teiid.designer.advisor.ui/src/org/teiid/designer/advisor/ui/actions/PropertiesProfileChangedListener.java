/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import com.metamatrix.modeler.ui.viewsupport.DesignerPropertiesUtil;

public class PropertiesProfileChangedListener implements IProfileListener {
	Properties designerProperties;

	public PropertiesProfileChangedListener(Properties properties) {
		super();
		designerProperties = properties;
	}
    @Override
    public void profileAdded( IConnectionProfile profile ) {
        handleProfileAdded(profile);
    }

    @Override
    public void profileChanged( IConnectionProfile profile ) {
    	// nothing
    }

    @Override
    public void profileDeleted( IConnectionProfile profile ) {
        // nothing
    }
    
	private void handleProfileAdded(IConnectionProfile newProfile) {
		if( this.designerProperties != null ) {
            DesignerPropertiesUtil.setConnectionProfileName(this.designerProperties, newProfile.getName());
		}
	}
}
