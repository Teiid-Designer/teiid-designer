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

public class NumberToBooleanTransform extends Transform {
	
	private Comparable falseVal;
	private Class<?> sourceType;
	
	public NumberToBooleanTransform(DataTypeManagerService dataTypeManager, Comparable falseVal) {
	    super(dataTypeManager);
		this.falseVal = falseVal;
		this.sourceType = falseVal.getClass();
	}

	@Override
	public Class<?> getSourceType() {
		return sourceType;
	}
	
	@Override
	public Class<?> getTargetType() {
		return DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass();
	}
	
	@Override
	public Object transformDirect(Object value) throws Exception {
		if (falseVal.compareTo(value) == 0) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	@Override
	public boolean isExplicit() {
		return true;
	}

}
