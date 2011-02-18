/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
 
 Note that the eclipse project name, org.teiid.designer.vdb.edit is used in a hard-coded static string constant in: 
 org.teiid.designer.core plugin.
 
 The class is:  
 	com.metamatrix.modeler.core.container.ResourceFinder
 
 Variable is:  
 	public static final String VDB_WORKING_FOLDER_URI_PATH_SEGEMENT = ".metadata/.plugins/teiid.designer.vdb.edit/vdbWorkingFolder"; //$NON-NLS-1$
 	
 Note that this should be cleaned up at some point.