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

package com.metamatrix.modeler.modelgenerator.wsdl.schema.extensions;

import com.metamatrix.modeler.schema.tools.model.jdbc.DataType;

public class SoapDataTypeImpl implements DataType {

    private String name;
    private String namespace;
    private String arrayName;
    private String arrayNamespace;

    public SoapDataTypeImpl(String itemType, String itemNamespace, String arrayName, String arrayNamespace) {
        setTypeName(itemType);
        setTypeNamespace(itemNamespace);
        setArrayName(arrayName);
        setArrayNamespace(arrayNamespace);
    }

    public String getTypeName() {
        return name;
    }

    public void setTypeName(String name) {
        this.name = name;
    }

    public String getTypeNamespace() {
        return namespace;
    }

    public void setTypeNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getArrayName() {
        return arrayName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    public String getArrayNamespace() {
        return arrayNamespace;
    }

    public void setArrayNamespace(String arrayNamespace) {
        this.arrayNamespace = arrayNamespace;
    }

}
