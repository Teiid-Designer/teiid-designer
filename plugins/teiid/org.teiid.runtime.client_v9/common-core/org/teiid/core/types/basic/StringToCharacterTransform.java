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

package org.teiid.core.types.basic;

import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.Transform;

public class StringToCharacterTransform extends Transform {

	/**
     * @param dataTypeManager
     */
    public StringToCharacterTransform(DataTypeManagerService dataTypeManager) {
        super(dataTypeManager);
    }

    /**
	 * This method transforms a value of the source type into a value
	 * of the target type.
	 * @param value Incoming value of source type
	 * @return Outgoing value of target type
	 * @throws Exception if value is an incorrect input type or
	 * the transformation fails
	 */
	public Object transformDirect(Object value) throws Exception {
		String s = (String) value;
		
		if (s.length() == 0) {
			return Character.valueOf(' '); 
		}
		
        return Character.valueOf(s.charAt(0));
	}

	/**
	 * Type of the incoming value.
	 * @return Source type
	 */
	public Class<?> getSourceType() {
		return String.class;
	}

	/**
	 * Type of the outgoing value.
	 * @return Target type
	 */
	public Class<?> getTargetType() {
		return Character.class;
	}
	
	@Override
	public boolean isExplicit() {
		return true;
	}

}
