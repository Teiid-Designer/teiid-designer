/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.vdb;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.vdb.VdbUtil;

/* This class will peek inside a VDB to determine if the VDB is of type 8.x or greater and assume that the VDB
 * is pre-8.0 (i.e. 7.x).  If the VDB is type 8.x, then it probably shouldn't be deployed on a Teiid 7.7 server
 * or at least WARNED not to do so.
 * 
 */
public class VdbAgeChecker {
	
	public static boolean doDeploy(final IFile vdbFile, final ITeiidServerVersion serverVersion) {
    	CoreArgCheck.isNotNull(vdbFile, "vdbFile"); //$NON-NLS-1$

    	
        if( serverVersion.isLessThan(Version.TEIID_8_0) && 
        	VdbUtil.isVdbTeiidVersion8orGreater(vdbFile) ) {
        	
        	String vdbName = FileUtils.getNameWithoutExtension(vdbFile);
        	final IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        		
        	boolean doDeploy = MessageDialog.openQuestion(window.getShell(), 
        				DqpUiConstants.UTIL.getString("VdbAgeChecker.oldVdb.title"),  //$NON-NLS-1$
        				DqpUiConstants.UTIL.getString("VdbAgeChecker.oldVdb.message", vdbName));  //$NON-NLS-1$
        		
        	return doDeploy;
        }
        
        return true;
    }
}