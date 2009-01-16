/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.schema.tools.model.schema.impl;

import org.eclipse.xsd.XSDTypeDefinition;

import com.metamatrix.modeler.schema.tools.model.schema.SchemaObjectKey;

public class TypeDefinitionKey implements SchemaObjectKey {

	XSDTypeDefinition type;
	
	public TypeDefinitionKey(XSDTypeDefinition type) {
		this.type = type;
	}
	
	@Override
    public boolean equals(Object obj) {
		boolean result = false;
		if(obj instanceof TypeDefinitionKey){
			TypeDefinitionKey other = (TypeDefinitionKey)obj;
			if(other.type == this.type){
				result = true;
			}
		}
		return result;
	}

	@Override
    public int hashCode() {
		return type.hashCode();
	}
}
