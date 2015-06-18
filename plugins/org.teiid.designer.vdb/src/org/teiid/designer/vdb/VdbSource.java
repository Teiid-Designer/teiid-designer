/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb;

import static org.teiid.designer.vdb.Vdb.Event.MODEL_JNDI_NAME;
import static org.teiid.designer.vdb.Vdb.Event.MODEL_TRANSLATOR;
import org.teiid.core.designer.util.StringUtilities;

/**
 * 
 */
public class VdbSource extends VdbUnit {

    private String jndiName;

    private String translatorName;

    
	/**
	 * @param vdb 
	 * @param name
	 * @param jndiName
	 * @param translatorName
	 */
	public VdbSource(Vdb vdb, String name, String jndiName, String translatorName) {
		super();
		setVdb(vdb);
		setName(name);
		this.jndiName = jndiName;
		this.translatorName = translatorName;
	}

	/**
	 * @return the jndiName
	 */
	public String getJndiName() {
		return this.jndiName;
	}


	/**
	 * @param jndiName the jndiName to set
	 */
	public void setJndiName(String jndiName) {
		if( !StringUtilities.equals(this.jndiName, jndiName) )  {
			this.jndiName = jndiName;
			setModified(this, MODEL_JNDI_NAME, jndiName, this.jndiName);
		}
	}


	/**
	 * @return the translatorName
	 */
	public String getTranslatorName() {
		return this.translatorName;
	}


	/**
	 * @param translatorName the translatorName to set
	 */
	public void setTranslatorName(String translatorName) {
		if( !StringUtilities.equals(this.translatorName, translatorName) )  {
			this.translatorName = translatorName;
			setModified(this, MODEL_TRANSLATOR, translatorName, this.translatorName);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("VdbSource : "); //$NON-NLS-1$
        text.append("\n\tsource name = ").append(getName()); //$NON-NLS-1$
        text.append("\n\ttranslator name = ").append(getTranslatorName()); //$NON-NLS-1$
        text.append("\n\tjndi name = ").append(getJndiName()); //$NON-NLS-1$

        return text.toString();
	}
	
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.jndiName == null) ? 0 : this.jndiName.hashCode());
        result = prime * result + ((this.translatorName == null) ? 0 : this.translatorName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VdbSource other = (VdbSource)obj;
        if (this.jndiName == null) {
            if (other.jndiName != null)
                return false;
        } else if (!this.jndiName.equals(other.jndiName))
            return false;
        if (this.translatorName == null) {
            if (other.translatorName != null)
                return false;
        } else if (!this.translatorName.equals(other.translatorName))
            return false;
        return true;
    }

    @Override
    public VdbSource clone() {
        VdbSource clone = new VdbSource(getVdb(), getName(), getJndiName(), getTranslatorName());
        clone.setDescription(getDescription());
        return clone;
    }
}
