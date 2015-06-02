/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;


/**
 * Represents a VDB model source.
 */
public class ModelSource extends VdbObject {
	
	String jndiName;
	String translatorName;
	

    /**
     * The type identifier.
     */
    int TYPE_ID = ModelSource.class.hashCode();

    /**
     * Identifier of this object
     */
    TeiidType IDENTIFIER = TeiidType.VDB_MODEL_SOURCE;

    /**
     * An empty array of model sources.
     */
    ModelSource[] NO_SOURCES = new ModelSource[0];
    
    /**
     * 
     */
    public ModelSource() {
    	super();
    }
    /**
     * @param name
     * @param jndiName
     * @param translatorName
     */
    public ModelSource(String name, String jndiName, String translatorName) {
    	this();
    	setName(name);
    	this.jndiName = jndiName;
    	this.translatorName = translatorName;
    }

    /**
     * @return the value of the <code>JNDI name</code> property (can be empty)
     */
    public String getJndiName() {
    	return this.jndiName;
    }

    /**
     * @return the value of the <code>translator name</code> property (can be empty)
     */
    public String getTranslatorName(){
    	return this.translatorName;
    }

    /**
     * @param newJndiName
     *        the new value of the <code>JNDI name</code> property (can only be empty when removing)
     */
    public void setJndiName( final String newJndiName ) {
    	setChanged(this.jndiName, newJndiName);
		this.jndiName = newJndiName;
    }

    /**
     * @param newTranslatorName
     *        the new value of the <code>translator name</code> property (can only be empty when removing)
     */
    public void setTranslatorName( final String newTranslatorName ) {
    	setChanged(this.translatorName, newTranslatorName);
		this.translatorName = newTranslatorName;
    }
}
