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

package org.teiid.runtime.client.types.basic;

import org.teiid.runtime.client.types.DataTypeManagerService;
import org.teiid.runtime.client.types.Transform;

public class BooleanToNumberTransform extends Transform {

	private Object trueVal;
	private Object falseVal;
	private Class<?> targetType;
	
	public BooleanToNumberTransform(Object trueVal, Object falseVal) {
		this.trueVal = trueVal;
		this.falseVal = falseVal;
		this.targetType = trueVal.getClass();
	}

	@Override
	public Class getSourceType() {
		return DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass();
	}
	
	@Override
	public Class getTargetType() {
		return targetType;
	}
	
	@Override
	public Object transformDirect(Object value) throws Exception {
		return value.equals(Boolean.TRUE)?trueVal:falseVal;
	}
	
}
