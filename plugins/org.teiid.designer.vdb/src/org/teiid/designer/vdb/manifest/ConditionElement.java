/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.manifest;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.teiid.designer.vdb.manifest.adapters.XmlVdbAdapters;

/**
 *
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "condition" )
public class ConditionElement implements Serializable {
			
    private static final long serialVersionUID = 1L;
    
    @XmlAttribute( name = "constraint", required = true )
    @XmlJavaTypeAdapter(XmlVdbAdapters.ConstraintAttributeAdapter.class)
    private Boolean constraint;
    
    @XmlValue
    private String sql;
    
    /**
     * Used by JAXB when loading a VDB
     */
    public ConditionElement() {
    	
    }
    
    /**
     * Used by JAXB when loading a VDB
     * @param sql the condition sql
     * @param constraint the is constraint
     */
    public ConditionElement(String sql, boolean constraint) {
    	this.sql = sql;
    	this.constraint = Boolean.valueOf(constraint);
    }

	/**
	 * @return the constraint
	 */
	public Boolean getConstraint() {
		return this.constraint;
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return this.sql;
	}
    
	/*
	<data-role name="base-role" any-authenticated="true">
        <description>Conditional access</description>
 
        <permission>
            <resource-name>modelName.tblName</resource-name>
            <condition constraint="false">column1=user()</condition>
        </permission>
 
    </data-role>
	 */
}
