/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import org.jboss.vfs.VirtualFile;
import org.teiid.adminapi.impl.VDBMetaData;
import org.teiid.metadata.VDBResource;

public class VDBResources {
	
	public static final String DEPLOYMENT_FILE = "vdb.xml"; // !!! DO NOT CHANGE VALUE as this would cause problems with existing VDBs having DEF files !!! //$NON-NLS-1$
	public static final String VDB_ARCHIVE_EXTENSION = ".vdb"; //$NON-NLS-1$
	public final static String INDEX_EXT        = ".INDEX";     //$NON-NLS-1$
	public final static String SEARCH_INDEX_EXT = ".SEARCH_INDEX";     //$NON-NLS-1$
	public final static String MODEL_EXT = ".xmi";     //$NON-NLS-1$
	
	public static class Resource implements VDBResource {
		public Resource(VirtualFile file) {
			this.file = file;
		}
		VirtualFile file;
		@Override
		public InputStream openStream() throws IOException {
			return file.openStream();
		}
		@Override
		public long getSize() {
			return file.getSize();
		}
		@Override
		public String getName() {
			return file.getName();
		}
		public VirtualFile getFile() {
			return file;
		}
	}

	private LinkedHashMap<String, VDBResources.Resource> vdbEntries;
	
	public VDBResources(VirtualFile root, VDBMetaData vdb) throws IOException {
		LinkedHashMap<String, VDBResources.Resource> visibilityMap = new LinkedHashMap<String, VDBResources.Resource>();
		for(VirtualFile f: root.getChildrenRecursively()) {
			if (f.isFile()) {
				// remove the leading vdb name from the entry
				String path = f.getPathName().substring(root.getPathName().length());
				if (!path.startsWith("/")) { //$NON-NLS-1$
					path = "/" + path; //$NON-NLS-1$
				}
				visibilityMap.put(path, new VDBResources.Resource(f)); 
			}
		}
		this.vdbEntries = visibilityMap;
	}
	
	public LinkedHashMap<String, VDBResources.Resource> getEntriesPlusVisibilities(){
		return this.vdbEntries;
	}

}
