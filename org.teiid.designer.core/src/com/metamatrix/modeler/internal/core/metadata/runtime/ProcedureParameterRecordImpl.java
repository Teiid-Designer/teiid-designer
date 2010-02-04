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
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;

/**
 * ProcedureParameterRecordImpl
 */
public class ProcedureParameterRecordImpl extends com.metamatrix.metadata.runtime.impl.ProcedureParameterRecordImpl {

    private static final long serialVersionUID = 5966620687324761894L;

    /**
	 * Flags to determine if values have been set.
	 */
	private boolean datatypeUUIDSet;
	private boolean optionalSet;
	private boolean defaultValueSet;
	private boolean lengthSet;
	private boolean scaleSet;
	private boolean nullTypeSet;
	private boolean runtimeTypeSet;
	private boolean precisionSet;
	private boolean positionSet;
	private boolean radixSet;
	private boolean typeSet;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public ProcedureParameterRecordImpl(final SqlProcedureParameterAspect sqlAspect, final EObject eObject) {
		super(new ModelerMetadataRecordDelegate(sqlAspect, eObject));
		setRecordType(IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER);
		this.eObject = eObject;
	}

	private SqlProcedureParameterAspect getParameterAspect() {
		return (SqlProcedureParameterAspect) ((ModelerMetadataRecordDelegate)this.delegate).getSqlAspect();			
	}

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord#getDefaultValue()
     */
    @Override
    public Object getDefaultValue() {
		if(eObject != null && !defaultValueSet) {
			setDefaultValue(getParameterAspect().getDefaultValue((EObject)eObject));
		}
        return super.getDefaultValue();
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord#getType()
     */
    @Override
    public short getType() {
		if(eObject != null && !typeSet) {
			setType((short) getParameterAspect().getType((EObject)eObject));
		}
        return super.getType();
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord#getDatatypeUUID()
     */
    @Override
    public String getDatatypeUUID() {
		if(eObject != null && !datatypeUUIDSet) {
			EObject dataType = getParameterAspect().getDatatype((EObject)eObject);
			setDatatypeUUID(((ModelerMetadataRecordDelegate)this.delegate).getObjectID(dataType)); 
		}
        return super.getDatatypeUUID();
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord#getRuntimeType()
     */
    @Override
    public String getRuntimeType() {
		if(eObject != null && !runtimeTypeSet) {
			setRuntimeType(getParameterAspect().getRuntimeType((EObject)eObject));
		}
        return super.getRuntimeType();
    }

    /**
     * @return
     */
    @Override
    public int getLength() {
		if(eObject != null && !lengthSet) {
			setLength(getParameterAspect().getLength((EObject)eObject));
		}
        return super.getLength();
    }

    /**
     * @return
     */
    @Override
    public int getPrecision() {
		if(eObject != null && !precisionSet) {
			setPrecision(getParameterAspect().getPrecision((EObject)eObject));
		}
        return super.getPrecision();
    }

    /**
     * @return
     */
    @Override
    public int getScale() {
		if(eObject != null && !scaleSet) {
			setScale(getParameterAspect().getScale((EObject)eObject));
		}
        return super.getScale();
    }

    /**
     * @return
     */
    @Override
    public int getRadix() {
		if(eObject != null && !radixSet) {
			setRadix(getParameterAspect().getRadix((EObject)eObject));
		}
        return super.getRadix();
    }

    /**
     * @return
     */
    @Override
    public int getPosition() {
		if(eObject != null && !positionSet) {
			setPosition(getParameterAspect().getPosition((EObject)eObject));
		}
        return super.getPosition();
    }

    /**
     * @return
     */
    @Override
    public int getNullType() {
		if(eObject != null && !nullTypeSet) {
			setNullType(getParameterAspect().getNullType((EObject)eObject));
		}
        return super.getNullType();
    }

	/*
	 * @see com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord#isOptional()
	 */
	@Override
    public boolean isOptional() {
		if(eObject != null && !optionalSet) {
			return getParameterAspect().isOptional((EObject)eObject);
		}
		return super.isOptional();
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
    public void setPrecision(int i) {
		super.setPrecision(i);
		precisionSet = true;
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
	 * @param i
	 */
	@Override
    public void setRadix(int i) {
		super.setRadix(i);
		radixSet = true;
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
	 * @param i
	 */
	@Override
    public void setPosition(int i) {
		super.setPosition(i);
		positionSet = true;
	}

	/**
	 * @param string
	 */
	@Override
    public void setRuntimeType(String string) {
		super.setRuntimeType(string);
		runtimeTypeSet = true;
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
	 * @param i
	 */
	@Override
    public void setType(int i) {
		super.setType(i);
		typeSet = true;		
	}

    /**
     * @param b
     */
    @Override
    public void setOptional(boolean b) {
        super.setOptional(b);
		optionalSet = true;
    }

}
