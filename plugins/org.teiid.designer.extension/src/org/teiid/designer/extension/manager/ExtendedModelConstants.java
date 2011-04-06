/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExtendedModelConstants {
	public static final Map<String, String> METAMODEL_URI_MAP;
	public static final String RELATIONAL_PREFIX = "relational"; //$NON-NLS-1$
	public static final String TRANSFORM_PREFIX = "transform"; //$NON-NLS-1$
	public static final String WEBSERVICE_PREFIX = "webservice"; //$NON-NLS-1$
	public static final String XMLDOC_PREFIX = "relational"; //$NON-NLS-1$
	
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(RELATIONAL_PREFIX, "http://www.metamatrix.com/metamodels/Relational"); //$NON-NLS-1$
        aMap.put(TRANSFORM_PREFIX, "http://www.metamatrix.com/metamodels/Transformation"); //$NON-NLS-1$
        aMap.put(WEBSERVICE_PREFIX, "http://www.metamatrix.com/metamodels/WebService"); //$NON-NLS-1$
        aMap.put(XMLDOC_PREFIX, "http://www.metamatrix.com/metamodels/XmlDocument"); //$NON-NLS-1$
        METAMODEL_URI_MAP = Collections.unmodifiableMap(aMap);
    }

}
