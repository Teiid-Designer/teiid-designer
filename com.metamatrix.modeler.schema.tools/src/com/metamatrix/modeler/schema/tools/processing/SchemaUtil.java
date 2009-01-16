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

package com.metamatrix.modeler.schema.tools.processing;

import java.util.Map;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.modeler.schema.tools.ToolsPlugin;
import com.metamatrix.modeler.schema.tools.model.schema.QName;
import com.metamatrix.modeler.schema.tools.model.schema.impl.QNameImpl;

public class SchemaUtil {

    private static XSDSimpleTypeDefinition stringType = null;

    public static XSDSimpleTypeDefinition getStringType( XSDSchema schema ) throws SchemaProcessingException {
        if (stringType == null) {
            XSDSchema schemaForSchema;
            if (null == schema) {
                schemaForSchema = XSDFactory.eINSTANCE.createXSDSchema().getSchemaForSchema();
            } else {
                schemaForSchema = schema.getSchemaForSchema();
            }
            if (null == schemaForSchema) {
                throw new SchemaProcessingException(ToolsPlugin.Util.getString("SchemaUtil.missingSchemaOfSchemasReference")); //$NON-NLS-1$
            }
            Map typeIdMap = schemaForSchema.getSimpleTypeIdMap();
            Object o = typeIdMap.get("string"); //$NON-NLS-1$
            stringType = (XSDSimpleTypeDefinition)o;
        }
        return stringType;
    }

    public static String shortenFileName( String name ) {
        if (name == null) return name;
        String retVal;
        String tempStr = name.replace('\\', '/');
        if (tempStr.indexOf('/') >= 0) {
            retVal = tempStr.substring(tempStr.lastIndexOf('/') + 1);
        } else {
            retVal = name;
        }
        return retVal;
    }

    public static QName getQName( String namespace,
                                  String lName ) {
        return new QNameImpl(namespace, lName);
    }
}
