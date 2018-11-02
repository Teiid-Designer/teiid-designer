/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;


/**
 * @since 8.0
 */
public class DataRoleResolver {
	private static final char DELIM = CoreStringUtil.Constants.DOT_CHAR;
	private static final char B_SLASH = '/';
	private static final String DQUOTE_STR = "\""; //$NON-NLS-1$
	private static final char DQUOTE_CHAR = '"';
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
		if( dotPath.contains(DQUOTE_STR) ) {
			StringBuilder sb = new StringBuilder();
			boolean betweenDQuotes = false;
			
			for( char nextChar : dotPath.toCharArray() ) {
				if( nextChar == DQUOTE_CHAR) {
					if( !betweenDQuotes) {
						betweenDQuotes = true;
					} else {
						betweenDQuotes = false;
					}
					sb.append(nextChar);
				} else if( nextChar == DELIM ) {
					if( !betweenDQuotes ) {
						sb.append(B_SLASH);
					} else {
						sb.append(nextChar);
					}
				} else {
					sb.append(nextChar);
				}
			}
			return sb.toString();
			
		} else {
			return dotPath.replace(DELIM, B_SLASH);
		}
	}
	
	private boolean targetStillExists(IPath path) {
		if( path.segmentCount() == 1 ) {
			return true;
		}
	    
	    Resource resource = null;
	    
	    for( Resource res : tempContainer.getResources() ) {
	    	String modelName = res.getURI().lastSegment();
	    	if( modelName.equalsIgnoreCase(path.segment(0) + ".xmi")) { //$NON-NLS-1$
	    		resource = res;
	    		break;
	    	}
	    }
	    
	    EObject obj = null;
	    
	    if( resource != null ) {
	    	// Build object path in model
	    	IPath fragmentPath = new Path(""); //$NON-NLS-1$
	    	int nSegs = path.segmentCount();
	    	for(int i=1; i< nSegs; i++) {
	    		fragmentPath = fragmentPath.append(path.segment(i));
	    	}
	    	
	    	obj = ModelerCore.getModelEditor().findObjectByPath(resource, fragmentPath);
	    }
		
		if( obj == null ) {
			return false;
		}
		
		return true;
	}
}
