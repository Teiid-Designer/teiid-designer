/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metadata.runtime;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect;


/**
 * FakeSqlProcedureParamAspect
 */
public class FakeSqlProcedureParamAspect implements SqlProcedureParameterAspect {

    public String name, fullName, nameInSource, datatypeName, runtimeType, datatypeUUID;
    public IPath path;
    public EObject datatype;
    public String defaultValue;
    public Object uuid, parentUuid;
    public int length, scale, precision, position, type, radix, nullType;
    public boolean optional;

    @Override
	public EObject getDatatype(EObject eObject) { return datatype; }

    @Override
	public String getDatatypeName(EObject eObject) { return datatypeName; }

    @Override
	public String getDatatypeObjectID(EObject eObject) { return datatypeUUID; }

    @Override
	public String getRuntimeType(EObject eObject) { return runtimeType; }

    @Override
	public String getDefaultValue(EObject eObject) { return defaultValue; }

    @Override
	public int getNullType(EObject eObject) { return nullType; }

    @Override
	public int getLength(EObject eObject) { return length; }

    @Override
	public int getRadix(EObject eObject) { return radix; }

    @Override
	public int getPosition(EObject eObject) { return position; }

    @Override
	public int getScale(EObject eObject) { return scale; }

    @Override
	public int getPrecision(EObject eObject) { return precision; }

    @Override
	public int getType(EObject eObject) { return type; }

    @Override
	public boolean isRecordType(char recordType) {return (recordType == IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER ); }

    @Override
	public boolean isQueryable(EObject eObject) { return true; }

    @Override
	public String getName(EObject eObject) { return name; }

    @Override
	public String getFullName(EObject eObject) { return fullName; }

    @Override
	public String getNameInSource(EObject eObject) { return nameInSource; }

    @Override
	public Object getObjectID(EObject eObject) { return uuid; }

    @Override
	public Object getParentObjectID(EObject eObject) { return parentUuid; }

    @Override
	public IPath getPath(EObject eObject) { return path; }

    @Override
	public MetamodelEntity getMetamodelEntity() { return null; }

    @Override
	public String getID() { return null; }

    @Override
	public boolean isOptional(EObject eObject) { return optional; }

    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) { }
    
    @Override
	public boolean isDatatypeFeature(EObject eObject, EStructuralFeature eFeature) { return false; }

    @Override
	public boolean canSetDatatype() { return false; }

    @Override
	public void setDatatype(EObject eObject,  EObject datatype) {}
    
    @Override
	public boolean canSetLength() { return false; }
    
    @Override
	public boolean canSetNullType() { return false; }
    
    @Override
	public void setLength(EObject eObject, int length) {}

    @Override
	public void setNullType(EObject eObject, int nullType) {}

    @Override
	public boolean isInputParam(EObject eObject) { return false; }
    
    @Override
	public void setDirection(EObject eObject, int dir) {}
}
