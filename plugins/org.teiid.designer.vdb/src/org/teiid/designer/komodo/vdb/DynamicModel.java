/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import java.util.HashMap;
import java.util.Map;
import org.teiid.designer.vdb.VdbSource;
import org.teiid.designer.vdb.VdbUnit;

/**
 * Represents a relational model.
 */
public class DynamicModel extends VdbUnit {
	
	/*
		<xs:complexType>
			<xs:sequence>
				<xs:element name="description" type="xs:string" minOccurs="0"/>
				<xs:element name="property" type="property" minOccurs="0" maxOccurs="unbounded"/>
				<xs:element name="source" minOccurs="0" maxOccurs="unbounded"></xs:element>
				<xs:element name="metadata" minOccurs="0" maxOccurs="1"></xs:element>
				<xs:element name="validation-error" minOccurs="0" maxOccurs="unbounded"></xs:element>
			</xs:sequence>
			<xs:attribute name="name" type="xs:string" use="required"/>
			<xs:attribute name="type" type="xs:string" default="PHYSICAL"/>
			<xs:attribute name="visible" type="xs:boolean" default="true"/>
			<xs:attribute name="path" type="xs:string"/>
		</xs:complexType>
	 */

    private Type modelType = Type.DEFAULT_VALUE;
	private boolean isVisible = true;
	private Map<String, VdbSource> modelSources = new HashMap<String, VdbSource>();
	private boolean allowMultiSource;
	private boolean addColumn;
	private String columnAlias = EMPTY_STRING;

	/**
	 * The metadata object. Can be null
	 */
	private Metadata metadata;

    /**
     * The type of a model.
     */
    @SuppressWarnings("javadoc")
    public enum Type {

        PHYSICAL,
        VIRTUAL;

        /**
         * The default model type. Value is {@value} .
         */
        public static final Type DEFAULT_VALUE = PHYSICAL;
        
        /**
         * @return actual type
         */
        public String getType() {
            String name = name();
            
            return name;
        }

        /** (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return getType();
        }

        public static Type fromString(String typeId) {
            for (Type type : Type.values()) {
                if (type.getType().equalsIgnoreCase(typeId))
                    return type;
            }

            return null;
        }
    }
    
    

    /**
     *
     */
    public DynamicModel() {
		super();
    }
    
    /**
     * @param name 
     * 
     */
    public DynamicModel(String name) {
		this();
		setName(name);
	}

	/**
     * @param source
     *        the name of the model source to create (cannot be empty)
     */
    public void addSource( final VdbSource source) {
    	this.modelSources.put(source.getName(), source);
    	source.setVdb(getVdb());
    }

    /**
     * @return model type of this model (never <code>null</code>)
     * @see Type#DEFAULT_VALUE
     */
    public Type getModelType() {
    	return this.modelType;
    }
    
    /**
     * @param type
     * @return enum type
     */
    private Type getTypeForString(String type) {
    	if( type.toUpperCase().equals(Type.PHYSICAL.toString())) {
    		return DynamicModel.Type.PHYSICAL;
    	}
    	if( type.toUpperCase().equals(Type.VIRTUAL.toString())) {
    		return DynamicModel.Type.VIRTUAL;
    	}
    	
    	return DynamicModel.Type.PHYSICAL;
    }

    /**
     * @return the model sources found in this model (can be empty)
     */
    public VdbSource[] getSources() {
    	return modelSources.values().toArray(new VdbSource[modelSources.size()]);
    }

    /**
     * @param sourceName
     *        the name of the model source being deleted (cannot be empty)
     */
    public void removeSource( final String sourceName ) {
    	VdbSource removed = modelSources.remove(sourceName);
		setChanged(removed != null);
    }

    /**
     * @param newModelType
     *        the new model type (can be <code>null</code>)
     * @see Type#DEFAULT_VALUE
     */
    public void setModelType( final Type newModelType ) {
    	this.modelType = newModelType;
    }
    
    /**
     * @param newModelType
     *        the new model type (can be <code>null</code>)
     */
    public void setModelType( final String newModelType ) {
    	setModelType(getTypeForString(newModelType));
    }

	/**
	 * @return isVisible
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * @param isVisible
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}



	/**
	 * @return the metadata
	 */
	public Metadata getMetadata() {
		return metadata;
	}



	/**
	 * @param metadata
	 */
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	
	
	/**
	 * @return allowMultiSource
	 */
	public boolean allowMultiSource() {
		return allowMultiSource;
	}

	/**
	 * @param allowMultiSource
	 */
	public void setAllowMultiSource(boolean allowMultiSource) {
		this.allowMultiSource = allowMultiSource;
	}

	/**
	 * @return is multi sourced
	 */
	public boolean isMultiSource() {
		return getSources().length > 1;
	}
	
	/**
	 * @param value
	 */
	public void setAddColumn(boolean value) {
		this.addColumn = value;
	}
	
	/**
	 * @return do add column
	 */
	public boolean doAddColumn() {
		return this.addColumn;
	}

	/**
	 * @return column alias
	 */
	public String getColumnAlias() {
		return columnAlias;
	}

	/**
	 * @param columnAlias
	 */
	public void setColumnAlias(String columnAlias) {
		this.columnAlias = columnAlias;
	}

	@Override
	public DynamicModel clone() {
	    DynamicModel clone = new DynamicModel();
	    cloneVdbObject(clone);
	    clone.setModelType(getModelType());
	    clone.setVisible(isVisible());
	    clone.setAllowMultiSource(allowMultiSource());
	    clone.setAddColumn(doAddColumn());
	    clone.setColumnAlias(getColumnAlias());

	    if (getMetadata() != null)
	        clone.setMetadata(getMetadata().clone());

	    for (VdbSource source : modelSources.values()) {
	        clone.addSource(source.clone());
	    }

	    return clone;
	}
}
