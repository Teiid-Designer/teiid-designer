/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid8.type;

import java.lang.reflect.Field;
import java.util.Set;
import org.teiid.core.types.DataTypeManager;
import org.teiid.core.types.DataTypeManager.DefaultDataClasses;
import org.teiid.core.types.DataTypeManager.DefaultDataTypes;
import org.teiid.core.util.ArgCheck;
import org.teiid.designer.type.IDataTypeManagerService;

/**
 *
 */
public class DataTypeManagerService implements IDataTypeManagerService {

    @Override
    public Class<?> getDataTypeClass(String name) {
        ArgCheck.isNotNull(name);

        Class<?> dataTypeClass = DataTypeManager.getDataTypeClass(name);
        return dataTypeClass != null ? dataTypeClass : null;
    }

    @Override
    public String getDataTypeName(Class<?> typeClass) {
        ArgCheck.isNotNull(typeClass);

        return DataTypeManager.getDataTypeName(typeClass);
    }

    @Override
    public Set<String> getAllDataTypeNames() {
        return DataTypeManager.getAllDataTypeNames();
    }
    
    private <T> T getStaticValue(DataTypeName dataTypeName, Class<?> klazz, Class<T> returnClassType) {
        for (Field field : klazz.getDeclaredFields()) {
            if (field.getName().equals(dataTypeName.name())) {
                field.setAccessible(true);
                try {
                    return (T) field.get(null);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        
        throw new RuntimeException();
    }

    @Override
    public String getDefaultDataType(DataTypeName dataTypeName) {
        ArgCheck.isNotNull(dataTypeName);

        return getStaticValue(dataTypeName, DefaultDataTypes.class, String.class);
    }
    
    @Override
    public Class<?> getDefaultDataClass(DataTypeName dataTypeName) {
        ArgCheck.isNotNull(dataTypeName);

        return getStaticValue(dataTypeName, DefaultDataClasses.class, Class.class);
    }
    
    @Override
    public boolean isExplicitConversion(String srcType, String tgtType) {
        return DataTypeManager.isExplicitConversion(srcType, tgtType);
    }
    
    @Override
    public boolean isImplicitConversion(String srcType, String tgtType) {
        return DataTypeManager.isImplicitConversion(srcType, tgtType);
    }
    
    @Override
    public String getCanonicalString(String name) {
        return DataTypeManager.getCanonicalString(name);
    }

}
