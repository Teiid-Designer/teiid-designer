/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb;

import static org.teiid.designer.vdb.Vdb.Event.MODEL_JNDI_NAME;
import static org.teiid.designer.vdb.Vdb.Event.MODEL_SOURCE_NAME;
import static org.teiid.designer.vdb.Vdb.Event.MODEL_TRANSLATOR;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringUtilities;

/**
 * 
 */
public class VdbSource {
	private Vdb vdb;
	
    private String name;

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
		this.vdb = vdb;
		this.name = name;
		this.jndiName = jndiName;
		this.translatorName = translatorName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		if( !StringUtilities.equals(this.name, name) )  {
			this.name = name;
			vdb.setModified(this, MODEL_SOURCE_NAME, name, this.name);
		}
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
			vdb.setModified(this, MODEL_JNDI_NAME, jndiName, this.jndiName);
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
			vdb.setModified(this, MODEL_TRANSLATOR, translatorName, this.translatorName);
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
	
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object object ) {
        if (this == object) {
            return true;
        }

        if ((object == null) || !getClass().equals(object.getClass())) {
            return false;
        }

        VdbSource other = (VdbSource)object;
        return this.vdb.equals(other.vdb) && CoreStringUtil.equals(this.name, other.name) && CoreStringUtil.equals(this.translatorName, other.translatorName) && CoreStringUtil.equals(this.jndiName, other.jndiName) ;
    }
	
}
