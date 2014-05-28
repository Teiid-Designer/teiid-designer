/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.ide.eclipse.as.storage;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.ide.eclipse.as.storage.Messages"; //$NON-NLS-1$

	// Storage framework
	public static String StorageManagerExportStatusMsg;
	public static String StorageManagerImportStatusMsg;

	public static String DirectoryStoragePropNullError;
	public static String DirectoryStorageInvalidLocation;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	private Messages() {
	}
}
