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

package org.teiid.metadata;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


/**
 * Allows connectors to build metadata for use by the engine.
 *
 * TODO: add support for datatype import
 * TODO: add support for unique constraints
 */
public class MetadataFactory implements Serializable {

	private static final String TEIID_RESERVED = "teiid_"; //$NON-NLS-1$
	private static final String TEIID_SF = "teiid_sf"; //$NON-NLS-1$
	private static final String TEIID_RELATIONAL = "teiid_rel"; //$NON-NLS-1$
	private static final String TEIID_WS = "teiid_ws"; //$NON-NLS-1$
	private static final String TEIID_MONGO = "teiid_mongo"; //$NON-NLS-1$
	private static final String TEIID_ODATA = "teiid_odata"; //$NON-NLS-1$

	public static final String SF_URI = "{http://www.teiid.org/translator/salesforce/2012}"; //$NON-NLS-1$
	public static final String WS_URI = "{http://www.teiid.org/translator/ws/2012}"; //$NON-NLS-1$
	public static final String MONGO_URI = "{http://www.teiid.org/translator/mongodb/2013}"; //$NON-NLS-1$
	public static final String ODATA_URI = "{http://www.jboss.org/teiiddesigner/ext/odata/2012}"; //$NON-NLS-1$

	public static final Map<String, String> BUILTIN_NAMESPACES;
	static {
		Map<String, String> map = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		map.put(TEIID_RELATIONAL, AbstractMetadataRecord.RELATIONAL_URI.substring(1, AbstractMetadataRecord.RELATIONAL_URI.length()-1));
		map.put(TEIID_SF, SF_URI.substring(1, SF_URI.length()-1));
		map.put(TEIID_WS, WS_URI.substring(1, WS_URI.length()-1));
		map.put(TEIID_MONGO, MONGO_URI.substring(1, MONGO_URI.length()-1));
		map.put(TEIID_ODATA, ODATA_URI.substring(1, ODATA_URI.length()-1));
		BUILTIN_NAMESPACES = Collections.unmodifiableMap(map);
	}

}
