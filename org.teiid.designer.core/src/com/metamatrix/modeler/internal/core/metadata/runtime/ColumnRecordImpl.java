/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;

/**
 * ColumnRecordImpl
 */
public class ColumnRecordImpl extends com.metamatrix.metadata.runtime.impl.ColumnRecordImpl {

    private static final long serialVersionUID = 1657510358041575492L;

    /**
	 * Flags to determine if values have been set.
     * Used for performance.
	 */
	private boolean datatypeUUIDSet;
	private boolean selectableSet;
	private boolean updatableSet;
	private boolean autoIncrementableSet;
	private boolean caseSensitiveSet;
	private boolean signedSet;
	private boolean currencySet;
	private boolean fixedLengthSet;
	private boolean searchTypeSet;
	private boolean defaultValueSet;
	private boolean minValueSet;
	private boolean maxValueSet;
	private boolean lengthSet;
	private boolean scaleSet;
	private boolean nullTypeSet;
    private boolean nullValuesSet;
    private boolean distinctValuesSet;
	private boolean runtimeTypeNameSet;
	private boolean nativeTypeSet;
	private boolean formatSet;
	private boolean precisionSet;
	private boolean charOctetLengthSet;
	private boolean positionSet;
	private boolean radixSet;
    private boolean tranformationInputParameterSet;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public ColumnRecordImpl(final SqlColumnAspect sqlAspect, EObject eObject) {
        super(new ModelerMetadataRecordDelegate(sqlAspect, eObject));
		setRecordType(IndexConstants.RECORD_TYPE.COLUMN);
		this.eObject = eObject;
	}

	private SqlColumnAspect getColumnAspect() {
		return (SqlColumnAspect) ((ModelerMetadataRecordDelegate)this.delegate).getSqlAspect();
	}

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getCharOctetLength()
     */
    @Override
    public int getCharOctetLength() {
    	if((EObject)this.eObject != null && !charOctetLengthSet) {
			setCharOctetLength(getColumnAspect().getCharOctetLength((EObject)this.eObject));
    	}
        return super.getCharOctetLength();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getRuntimeType()
     */
    @Override
    public String getRuntimeType() {
		if((EObject)this.eObject != null && !runtimeTypeNameSet) {
			setRuntimeType(getColumnAspect().getRuntimeType((EObject)this.eObject));
		}
        return super.getRuntimeType();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getDatatypeUUID()
     */
    @Override
    public String getDatatypeUUID() {
		if((EObject)this.eObject != null && !datatypeUUIDSet) {
			setDatatypeUUID(getColumnAspect().getDatatypeObjectID((EObject)this.eObject));
		}
        return super.getDatatypeUUID();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getDefaultValue()
     */
    @Override
    public Object getDefaultValue() {
		if((EObject)this.eObject != null && !defaultValueSet) {
			setDefaultValue(getColumnAspect().getDefaultValue((EObject)this.eObject));
		}
        return super.getDefaultValue();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getLength()
     */
    @Override
    public int getLength() {
		if((EObject)this.eObject != null && !lengthSet) {
			setLength(getColumnAspect().getLength((EObject)this.eObject));
		}
        return super.getLength();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getMaxValue()
     */
    @Override
    public Object getMaxValue() {
		if((EObject)this.eObject != null && !maxValueSet) {
			setMaxValue(getColumnAspect().getMaxValue((EObject)this.eObject));
		}
        return super.getMaxValue();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getMinValue()
     */
    @Override
    public Object getMinValue() {
		if((EObject)this.eObject != null && !minValueSet) {
			setMinValue(getColumnAspect().getMinValue((EObject)this.eObject));
		}
        return super.getMinValue();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getPrecision()
     */
    @Override
    public int getPrecision() {
		if((EObject)this.eObject != null && !precisionSet) {
			setPrecision(getColumnAspect().getPrecision((EObject)this.eObject));
		}
        return super.getPrecision();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getPosition()
     */
    @Override
    public int getPosition() {
		if((EObject)this.eObject != null && !positionSet) {
			setPosition(getColumnAspect().getPosition((EObject)this.eObject));
		}
        return super.getPosition();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getScale()
     */
    @Override
    public int getScale() {
		if((EObject)this.eObject != null && !scaleSet) {
			setScale(getColumnAspect().getScale((EObject)this.eObject));
		}
        return super.getScale();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getSearchTye()
     */
    @Override
    public int getSearchType() {
		if((EObject)this.eObject != null && !searchTypeSet) {
			setSearchType(getColumnAspect().getSearchType((EObject)this.eObject));
		}
        return super.getSearchType();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getFormat()
     */
    @Override
    public String getFormat() {
		if((EObject)this.eObject != null && !formatSet) {
			setFormat(getColumnAspect().getFormat((EObject)this.eObject));
		}
        return super.getFormat();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#isAutoIncrementable()
     */
    @Override
    public boolean isAutoIncrementable() {
		if((EObject)this.eObject != null && !autoIncrementableSet) {
			setAutoIncrementable(getColumnAspect().isAutoIncrementable((EObject)this.eObject));
		}
        return super.isAutoIncrementable();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#isCaseSensitive()
     */
    @Override
    public boolean isCaseSensitive() {
		if((EObject)this.eObject != null && !caseSensitiveSet) {
			setCaseSensitive(getColumnAspect().isCaseSensitive((EObject)this.eObject));
		}
        return super.isCaseSensitive();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#isCurrency()
     */
    @Override
    public boolean isCurrency() {
		if((EObject)this.eObject != null && !currencySet) {
			setCurrency(getColumnAspect().isCurrency((EObject)this.eObject));
		}
        return super.isCurrency();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#isFixedLength()
     */
    @Override
    public boolean isFixedLength() {
		if((EObject)this.eObject != null && !fixedLengthSet) {
			setFixedLength(getColumnAspect().isFixedLength((EObject)this.eObject));
		}
        return super.isFixedLength();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#isTranformationInputParameter()
     * @since 4.2
     */
    @Override
    public boolean isTranformationInputParameter() {
        if((EObject)this.eObject != null && !tranformationInputParameterSet) {
            this.setTransformationInputParameter(getColumnAspect().isTranformationInputParameter((EObject)this.eObject));
        }
        return super.isTranformationInputParameter();
    }

    /**
     * @return
     */
    @Override
    public int getNullType() {
		if((EObject)this.eObject != null && !nullTypeSet) {
			setNullType(getColumnAspect().getNullType((EObject)this.eObject));
		}
        return super.getNullType();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#isSelectable()
     */
    @Override
    public boolean isSelectable() {
		if((EObject)this.eObject != null && !selectableSet) {
			setSelectable(getColumnAspect().isSelectable((EObject)this.eObject));
		}
        return super.isSelectable();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#isSigned()
     */
    @Override
    public boolean isSigned() {
		if((EObject)this.eObject != null && !signedSet) {
			setSigned(getColumnAspect().isSigned((EObject)this.eObject));
		}
        return super.isSigned();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#isUpdatable()
     */
    @Override
    public boolean isUpdatable() {
		if((EObject)this.eObject != null && !updatableSet) {
			setUpdatable(getColumnAspect().isUpdatable((EObject)this.eObject));
		}
        return super.isUpdatable();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getRadix()
     */
    @Override
    public int getRadix() {
		if((EObject)this.eObject != null && !radixSet) {
			setRadix(getColumnAspect().getRadix((EObject)this.eObject));
		}
        return super.getRadix();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getNativeType()
     * @since 4.2
     */
    @Override
    public String getNativeType() {
		if((EObject)this.eObject != null && !nativeTypeSet) {
			setNativeType(getColumnAspect().getNativeType((EObject)this.eObject));
		}
        return super.getNativeType();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getDistinctValues()
     * @since 4.3
     */
    @Override
    public int getDistinctValues() {
        if((EObject)this.eObject != null && !distinctValuesSet) {
            setDistinctValues(getColumnAspect().getDistinctValues((EObject)this.eObject));
        }
        return super.getDistinctValues();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnRecord#getNullValues()
     * @since 4.3
     */
    @Override
    public int getNullValues() {
        if((EObject)this.eObject != null && !nullValuesSet) {
            setNullValues(getColumnAspect().getNullValues((EObject)this.eObject));
        }
        return super.getNullValues();
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param b
     */
    @Override
    public void setAutoIncrementable(boolean b) {
    	super.setAutoIncrementable(b);
		autoIncrementableSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setCaseSensitive(boolean b) {
    	super.setCaseSensitive(b);
		caseSensitiveSet = true;
    }

    /**
     * @param i
     */
    @Override
    public void setCharOctetLength(int i) {
    	super.setCharOctetLength(i);
		charOctetLengthSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setCurrency(boolean b) {
    	super.setCurrency(b);
		currencySet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setRuntimeType(String string) {
    	super.setRuntimeType(string);
		runtimeTypeNameSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setDatatypeUUID(String string) {
    	super.setDatatypeUUID(string);
		datatypeUUIDSet = true;
    }

    /**
     * @param object
     */
    @Override
    public void setDefaultValue(Object object) {
        super.setDefaultValue(object);
		defaultValueSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setFixedLength(boolean b) {
        super.setFixedLength(b);
		fixedLengthSet = true;
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
     * @param i
     */
    @Override
    public void setNullType(int i) {
        super.setNullType(i);
        nullTypeSet = true;
    }

    /**
     * @param object
     */
    @Override
    public void setMaxValue(Object object) {
        super.setMaxValue(object);
        maxValueSet = true;
    }

    /**
     * @param object
     */
    @Override
    public void setMinValue(Object object) {
    	super.setMinValue(object);
        minValueSet = true;
    }

    /**
     * @param i
     */
    @Override
    public void setPrecision(int i) {
        super.setPrecision(i);
        precisionSet = true;
    }

    /**
     * @param i
     */
    @Override
    public void setPosition(int i) {
    	super.setPosition(i);
        positionSet = true;
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
    public void setSearchType(int s) {
        super.setSearchType(s);
        searchTypeSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setSelectable(boolean b) {
        super.setSelectable(b);
        selectableSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setSigned(boolean b) {
        super.setSigned(b);
        signedSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setUpdatable(boolean b) {
        super.setUpdatable(b);
		updatableSet = true;
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
    public void setFormat(String string) {
        super.setFormat(string);
        formatSet = true;
    }

    /**
     * @param distinctValues The distinctValues to set.
     * @since 4.3
     */
    @Override
    public void setDistinctValues(int distinctValues) {
        super.setDistinctValues(distinctValues);
        this.distinctValuesSet = true;
    }

    /**
     * @param nullValues The nullValues to set.
     * @since 4.3
     */
    @Override
    public void setNullValues(int nullValues) {
        super.setNullValues(nullValues);
        this.nullValuesSet = true;
    }

    /**
     * @param nativeType The nativeType to set.
     * @since 4.2
     */
    @Override
    public void setNativeType(String nativeType) {
    	super.setNativeType(nativeType);
        nativeTypeSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setTransformationInputParameter(boolean b) {
        super.setTransformationInputParameter(b);
        this.tranformationInputParameterSet = true;
    }

}
