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
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.core.types.Transform;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;

public class ObjectToAnyTransform extends Transform {

    private Class targetClass;
    
    public ObjectToAnyTransform(DataTypeManagerService dataTypeManager, Class targetClass) {
        super(dataTypeManager);
        this.targetClass = targetClass;
    }
    
    /**
     * Type of the incoming value.
     * @return Source type
     */
    public Class getSourceType() {
        return DataTypeManagerService.DefaultDataTypes.OBJECT.getTypeClass();
    }
    
    public Class getTargetType() {
        return targetClass;
    }

    public Object transformDirect(Object value) throws Exception {
        if(targetClass.isAssignableFrom(value.getClass())) {
            return value;
        }

        DefaultDataTypes valueDataType = dataTypeManager.getDataType(value.getClass());
        DefaultDataTypes targetDataType = dataTypeManager.getDataType(getTargetType());
        Transform transform = dataTypeManager.getTransform(valueDataType, targetDataType);
        boolean valid = true;
        if (transform instanceof ObjectToAnyTransform) {
        	Object v1 = dataTypeManager.convertToRuntimeType(value, true);
        	if (v1 != value) {
				try {
					return transformDirect(v1);
				} catch (Exception e) {
					throw new TeiidClientException(e, Messages.gs(Messages.TEIID.TEIID10076, getSourceType(),
					                                                                                          targetClass, value));
				}
        	}
        	valid = false;
        }
        
        if (transform == null || !valid) {
            Object[] params = new Object[] { getSourceType(), targetClass, value};
              throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID10076, params));
        }
        
        try {
            return transform.transform(value);    
        } catch (Exception e) {
            Object[] params = new Object[] { getSourceType(), targetClass, value};
              throw new TeiidClientException(e, Messages.gs(Messages.TEIID.TEIID10076, params));
        }
    }
    
    /** 
     * @see org.teiid.core.types.Transform#isExplicit()
     */
    public boolean isExplicit() {
        return true;
    }
}
