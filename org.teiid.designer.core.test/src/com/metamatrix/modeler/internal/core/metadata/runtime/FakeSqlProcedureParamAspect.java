/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;

/**
 * FakeSqlProcedureParamAspect
 */
public class FakeSqlProcedureParamAspect implements SqlProcedureParameterAspect {

    public String name, fullName, nameInSource, datatypeName, runtimeType, datatypeUUID;
    public IPath path;
    public EObject datatype;
    public Object defaultValue, uuid, parentUuid;
    public int length, scale, precision, position, type, radix, nullType;
    public boolean optional;

    public EObject getDatatype(EObject eObject) { return datatype; }

    public String getDatatypeName(EObject eObject) { return datatypeName; }

    public String getDatatypeObjectID(EObject eObject) { return datatypeUUID; }

    public String getRuntimeType(EObject eObject) { return runtimeType; }

    public Object getDefaultValue(EObject eObject) { return defaultValue; }

    public int getNullType(EObject eObject) { return nullType; }

    public int getLength(EObject eObject) { return length; }

    public int getRadix(EObject eObject) { return radix; }

    public int getPosition(EObject eObject) { return position; }

    public int getScale(EObject eObject) { return scale; }

    public int getPrecision(EObject eObject) { return precision; }

    public int getType(EObject eObject) { return type; }

    public boolean isRecordType(char recordType) {return (recordType == IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER ); }

    public boolean isQueryable(EObject eObject) { return true; }

    public String getName(EObject eObject) { return name; }

    public String getFullName(EObject eObject) { return fullName; }

    public String getNameInSource(EObject eObject) { return nameInSource; }

    public Object getObjectID(EObject eObject) { return uuid; }

    public Object getParentObjectID(EObject eObject) { return parentUuid; }

    public IPath getPath(EObject eObject) { return path; }

    public MetamodelEntity getMetamodelEntity() { return null; }

    public String getID() { return null; }

    public boolean isOptional(EObject eObject) { return optional; }

    public void updateObject(EObject targetObject, EObject sourceObject) { }
    
    public boolean isDatatypeFeature(EObject eObject, EStructuralFeature eFeature) { return false; }

    public boolean canSetDatatype() { return false; }

    public void setDatatype(EObject eObject,  EObject datatype) {}
    
    public boolean canSetLength() { return false; }
    
    public boolean canSetNullType() { return false; }
    
    public void setLength(EObject eObject, int length) {}

    public void setNullType(EObject eObject, int nullType) {}

    public boolean isInputParam(EObject eObject) { return false; }
    
    public void setDirection(EObject eObject, int dir) {}
}
