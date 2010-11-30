package org.teiid.designer.roles.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;

import com.metamatrix.modeler.core.container.Container;

public class DataRoleResolver {
	private static final char DELIM = StringUtil.Constants.DOT_CHAR;
	private static final char B_SLASH = '/';
    private static final String SYS_TABLE_TARGET = "sys"; //$NON-NLS-1$
    private static final String PG_CATALOG_TARGET = "pg_catalog"; //$NON-NLS-1$
	
	private Container tempContainer;
	
	public DataRoleResolver(Container tempContainer) {
		super();
		
		this.tempContainer = tempContainer;
	}
	
	/**
	 * 
	 * @param originalDataRole
	 * @return
	 */
	public DataRole resolveDataRole(DataRole originalDataRole) {
		DataRole changedRole = null;
		Collection<Permission> keepPerms = new ArrayList<Permission>();
		boolean changedPerms = false;
		
		for( Permission perm : originalDataRole.getPermissions() ) {
			String targetName = perm.getTargetName();
			
			if(  targetName.equals(SYS_TABLE_TARGET) || targetName.equals(PG_CATALOG_TARGET)) {
				keepPerms.add(perm);
				continue;
			}
			IPath targetPath = new Path(getTargetNamePath(targetName));
			
			if( targetStillExists(targetPath) ) {
				keepPerms.add(perm);
			} else {
				changedPerms = true;
			}
			
		}
		
		if( changedPerms ) {
			changedRole = new DataRole(originalDataRole.getName());
			changedRole.setDescription(originalDataRole.getDescription());
			if( !originalDataRole.getRoleNames().isEmpty() ) {
				changedRole.setRoleNames(originalDataRole.getRoleNames());
			}
			changedRole.setPermissions(keepPerms);
			changedRole.setAnyAuthenticated(originalDataRole.isAnyAuthenticated());
		}
		
		return changedRole;
	}
	
	private String getTargetNamePath(String dotPath) {
		return dotPath.replace(DELIM, B_SLASH);
	}
	
	private boolean targetStillExists(IPath path) {
		if( path.segmentCount() == 1 ) {
			return true;
		}
	    
	    URI uri = URI.createURI(path.toString());
	    
	    EObject obj = tempContainer.getEObject(uri, false);
		
		if( obj == null ) {
			return false;
		}
		
		return true;
	}
}
