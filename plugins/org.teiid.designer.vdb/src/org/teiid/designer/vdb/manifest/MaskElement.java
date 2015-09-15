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

/**
*
*/
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "mask" )
public class MaskElement  implements Serializable {
			
    private static final long serialVersionUID = 1L;
    
    @XmlAttribute( name = "order", required = true )
    private String order;
    
    @XmlValue
    private String sql;
    
    /**
     * Used by JAXB when loading a VDB
     */
    public MaskElement() {
    	
    }
    
    /**
     * Used by JAXB when loading a VDB
     * @param sql the condition sql
     * @param order the is constraint
     */
    public MaskElement(String sql, int order) {
    	this.sql = sql;
    	this.order = Integer.toString(order);
    }

	/**
	 * @return the constraint
	 */
	public String getOrder() {
		return this.order;
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return this.sql;
	}

	/**
     * @param visitor
     */
    public void accept(Visitor visitor) {
        visitor.visit(this);
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
