/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.schema.extensions;

import org.teiid.designer.schema.tools.model.jdbc.DataType;

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

    @Override
	public String getTypeName() {
        return name;
    }

    @Override
	public void setTypeName(String name) {
        this.name = name;
    }

    @Override
	public String getTypeNamespace() {
        return namespace;
    }

    @Override
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
