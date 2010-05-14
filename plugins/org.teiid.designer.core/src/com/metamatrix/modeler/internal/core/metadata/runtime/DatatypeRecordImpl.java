/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;

/**
 * ColumnRecordImpl
 */
public class DatatypeRecordImpl extends com.metamatrix.metadata.runtime.impl.DatatypeRecordImpl {

    private static final long serialVersionUID = -7902919354575794443L;

	/**
	 * Flags to determine if values have been set.
	 */
	private boolean isAutoIncrementSet;
	private boolean isCaseSensitiveSet;
	private boolean isSignedSet;
	private boolean searchTypeSet;
	private boolean datatypeIDSet;
	private boolean basetypeIDSet;
    private boolean primitiveTypeIDSet;
	private boolean varietyTypeSet;
	private boolean lengthSet;
	private boolean scaleSet;
	private boolean nullTypeSet;
	private boolean runtimeTypeNameSet;
	private boolean varietyPropsSet;
	private boolean precisionLengthSet;
	private boolean javaClassNameSet;
	private boolean typeSet;
	private boolean radixSet;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public DatatypeRecordImpl(final SqlDatatypeAspect sqlAspect, final EObject eObject) {
		super(new ModelerMetadataRecordDelegate(sqlAspect, eObject));
		setRecordType(IndexConstants.RECORD_TYPE.DATATYPE);
		this.eObject = eObject;
	}

	private SqlDatatypeAspect getDatatypeAspect() {
		return (SqlDatatypeAspect) ((ModelerMetadataRecordDelegate)this.delegate).getSqlAspect();			
	}

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getLength()
     */
    @Override
    public int getLength() {
    	if(eObject != null && !lengthSet) {
			setLength(getDatatypeAspect().getLength((EObject)eObject));
    	}
        return super.getLength();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getPrecisionLength()
     */
    @Override
    public int getPrecisionLength() {
		if(eObject != null && !precisionLengthSet) {
			setPrecisionLength(getDatatypeAspect().getPrecisionLength((EObject)eObject));
		}
        return super.getPrecisionLength();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getScale()
     */
    @Override
    public int getScale() {
		if(eObject != null && !scaleSet) {
			setScale(getDatatypeAspect().getScale((EObject)eObject));
		}
        return super.getScale();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getRadix()
     */
    @Override
    public int getRadix() {
		if(eObject != null && !radixSet) {
			setRadix(getDatatypeAspect().getRadix((EObject)eObject));
		}
        return super.getRadix();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#isSigned()
     */
    @Override
    public boolean isSigned() {
		if(eObject != null && !isSignedSet) {
			setSigned(getDatatypeAspect().isSigned((EObject)eObject));
		}
        return super.isSigned();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#isAutoIncrement()
     */
    @Override
    public boolean isAutoIncrement() {
		if(eObject != null && !isAutoIncrementSet) {
			setAutoIncrement(getDatatypeAspect().isAutoIncrement((EObject)eObject));
		}
        return super.isAutoIncrement();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#isCaseSensitive()
     */
    @Override
    public boolean isCaseSensitive() {
		if(eObject != null && !isCaseSensitiveSet) {
			setCaseSensitive(getDatatypeAspect().isCaseSensitive((EObject)eObject));
		}
        return super.isCaseSensitive();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getType()
     */
    @Override
    public short getType() {
		if(eObject != null && !typeSet) {
			setType(getDatatypeAspect().getType((EObject)eObject));
		}
        return super.getType();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getSearchType()
     */
    @Override
    public short getSearchType() {
		if(eObject != null && !searchTypeSet) {
			setSearchType(getDatatypeAspect().getSearchType((EObject)eObject));
		}
        return super.getSearchType();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getNullType()
     */
    @Override
    public short getNullType() {
		if(eObject != null && !nullTypeSet) {
			setNullType(getDatatypeAspect().getNullType((EObject)eObject));
		}
        return super.getNullType();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getJavaClassName()
     */
    @Override
    public String getJavaClassName() {
		if(eObject != null && !javaClassNameSet) {
			setJavaClassName(getDatatypeAspect().getJavaClassName((EObject)eObject));
		}
        return super.getJavaClassName();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getRuntimeTypeName()
     */
    @Override
    public String getRuntimeTypeName() {
		if(eObject != null && !runtimeTypeNameSet) {
			setRuntimeTypeName(getDatatypeAspect().getRuntimeTypeName((EObject)eObject));
		}
        return super.getRuntimeTypeName();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getDatatypeID()
     */
    @Override
    public String getDatatypeID() {
		if(eObject != null && !datatypeIDSet) {
			setDatatypeID(getDatatypeAspect().getDatatypeID((EObject)eObject));
		}
        return super.getDatatypeID();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getBasetypeID()
     */
    @Override
    public String getBasetypeID() {
		if(eObject != null && !basetypeIDSet) {
			setBasetypeID(getDatatypeAspect().getBasetypeID((EObject)eObject));
		}
        return super.getBasetypeID();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getPrimitiveTypeID()
     * @since 4.3
     */
    @Override
    public String getPrimitiveTypeID() {
        if(eObject != null && !primitiveTypeIDSet) {
            setPrimitiveTypeID(getDatatypeAspect().getPrimitiveTypeID((EObject)eObject));
        }
        return super.getPrimitiveTypeID();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getVarietyType()
     */
    @Override
    public short getVarietyType() {
		if(eObject != null && !varietyTypeSet) {
			setVarietyType(getDatatypeAspect().getVarietyType((EObject)eObject));
		}
        return super.getVarietyType();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord#getVarietyProps()
     */
    @Override
    public List getVarietyProps() {
		if(eObject != null && !varietyPropsSet) {
			setVarietyProps(getDatatypeAspect().getVarietyProps((EObject)eObject));
		}
        return super.getVarietyProps();
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param string
     */
    @Override
    public void setBasetypeID(String string) {
        super.setBasetypeID(string);
		basetypeIDSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setPrimitiveTypeID(String string) {
        super.setPrimitiveTypeID(string);
        primitiveTypeIDSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setAutoIncrement(boolean b) {
        super.setAutoIncrement(b);
		isAutoIncrementSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setCaseSensitive(boolean b) {
        super.setCaseSensitive(b);
		isCaseSensitiveSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setSigned(boolean b) {
        super.setSigned(b);
		isSignedSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setJavaClassName(String string) {
        super.setJavaClassName(string);
		javaClassNameSet = true;
    }

    /**
     * @param i
     */
    @Override
    public void setLength(int i) {
        super.setLength(i);
		lengthSet = true;
    }

    /**
     * @param s
     */
    @Override
    public void setNullType(short s) {
        super.setType(s);
		nullTypeSet = true;
    }

    /**
     * @param i
     */
    @Override
    public void setPrecisionLength(int i) {
        super.setPrecisionLength(i);
		precisionLengthSet = true;
    }

    /**
     * @param i
     */
    @Override
    public void setRadix(int i) {
        super.setRadix(i);
		radixSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setRuntimeTypeName(String string) {
        super.setRuntimeTypeName(string);
		runtimeTypeNameSet = true;
    }

    /**
     * @param i
     */
    @Override
    public void setScale(int i) {
        super.setScale(i);
		scaleSet = true;
    }

    /**
     * @param s
     */
    @Override
    public void setSearchType(short s) {
        super.setSearchType(s);
		searchTypeSet = true;
    }

    /**
     * @param s
     */
    @Override
    public void setType(short s) {
        super.setType(s);
		typeSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setDatatypeID(String string) {
        super.setDatatypeID(string);
		datatypeIDSet = true;
    }

    /**
     * @param list
     */
    @Override
    public void setVarietyProps(List list) {
        super.setVarietyProps(list);
		varietyPropsSet = true;
    }

    /**
     * @param s
     */
    @Override
    public void setVarietyType(short s) {
        super.setVarietyType(s);
		varietyTypeSet = true;
    }

}
