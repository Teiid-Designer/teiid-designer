/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.sources;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.datatools.connectivity.ICategory;
import org.eclipse.datatools.connectivity.IConnectionProfile;

public class TeiidConnectionFolder {
	
	String label;
	String profileId;
	ICategory teiidCategory;

	public TeiidConnectionFolder(String label, String profileId, ICategory teiidCategory) {
		super();
		this.label = label;
		this.profileId = profileId;
		this.teiidCategory = teiidCategory;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public ICategory getCategory() {
		return this.teiidCategory;
	}
	
	public Object[] getChildren(Object[] allTeiidProfiles) {
		Collection<Object> profiles = new ArrayList<Object>();
		
		// return only the profile for this type of teiid profile
		for( Object obj : allTeiidProfiles ) {
			IConnectionProfile profile = (IConnectionProfile)obj;
			if( profile.getProviderId().equalsIgnoreCase(profileId)) {
				profiles.add(obj);
			}
		}
		
		return profiles.toArray(new Object[profiles.size()]);
	}
}
