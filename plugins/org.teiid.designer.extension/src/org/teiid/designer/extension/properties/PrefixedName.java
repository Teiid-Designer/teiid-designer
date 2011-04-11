/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension.properties;

import org.eclipse.osgi.util.NLS;
import org.teiid.designer.extension.Messages;

/**
 * Utility class to parse/hold simple name-space prefixed string names
 * 
 * Example:  salesforce:tableCapabilities can be passed in the fullName constructor or via ("salesforce", "tableCapabilites")
 * constructor.
 * 
 *
 */
public class PrefixedName {
	private final String prefix;
	
	private final String name;
	
	public PrefixedName(String prefix, String name) {
		super();
		this.prefix = prefix;
		this.name = name;
	}
	
	public PrefixedName(String fullName) {
		super();
		int semiColonIndex = fullName.indexOf(':');
		
		if( semiColonIndex > -1 ) {
			this.prefix = fullName.substring(0, semiColonIndex);
			this.name = fullName.substring(semiColonIndex+1, fullName.length());
		} else {
			throw new IllegalArgumentException(NLS.bind(Messages.PrefixedName_invalidPrefix, fullName));
		}
	}

	/**
	 * The prefix
	 * @return prefix the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * The name
	 * @return name the name value
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return prefix + ':' + name;
	}
}
