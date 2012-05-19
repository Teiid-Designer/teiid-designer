/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;

public class AttributeInfo implements ModelGeneratorWsdlUiConstants {
	private static final StringNameValidator nameValidator = new RelationalStringNameValidator(false, true);
	
    /**
     * The unique attribute name (never <code>null</code> or empty).
     */
	private String name;
	
	/**
     * The unique alias name (never <code>null</code> or empty).
     */
	private String alias; 
	
	/**
     * The xml element
     */
	private Object xmlElement;
	
	/**
     * The xml element
     */
	private ColumnInfo columnInfo;
	
	/**
	 * Current <code>IStatus</code> representing the state of the input values for this instance of
	 * <code>AttributeInfo</code>
	 */
	private IStatus status;
	
	/**
	 * 
	 * @param name the attribute name (never <code>null</code> or empty).
	 */
	public AttributeInfo(Object xmlElement, String name, ColumnInfo columnInfo) {
		super();
		this.xmlElement = xmlElement;
		this.name = name;
		this.alias = name;
		this.columnInfo = columnInfo;
		validate();
	}

	/**
	 * 
	 * @return name the attribute name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public void setName(String name) {
		CoreArgCheck.isNotNull(name, "name is null"); //$NON-NLS-1$
		this.name = name;
		validate();
	}
	
	/**
	 * 
	 * @return name the attribute alias
	 */
	public String getAlias() {
		return this.alias;
	}

	/**
	 * 
	 * @param name the attribute alias (never <code>null</code> or empty).
	 */
	public void setAlias(String name) {
		CoreArgCheck.isNotNull(name, "alias is null"); //$NON-NLS-1$
		this.alias = name;
		validate();
	}
	
	public String getSignature() {
		StringBuffer sb = new StringBuffer();
		sb.append(getAlias()).append(" [").append(getName()).append(']'); //$NON-NLS-1$
		return sb.toString();
	}
	
	public void setXmlElement(Object element) {
		this.xmlElement = element;
	}
	
	/**
	 * 
	 * @return xmlElement the xmlElement
	 */
	public Object getXmlElement() {
		return this.xmlElement;
	}
	
	/**
	 * 
	 * @return name the attribute alias
	 */
	public ColumnInfo getColumnInfo() {
		return this.columnInfo;
	}

	/**
	 * 
	 * @return status the <code>IStatus</code> representing the validity of the data in this info object
	 */
	public IStatus getStatus() {
		return this.status;
	}

	/**
	 * 
	 * @param status the <code>IStatus</code> representing the validity of the data in this info object
	 */
	public void setStatus(IStatus status) {
		this.status = status;
	}
	
	public void validate() {

		String result = nameValidator.checkValidName(getName());
		if( result != null ) {
			setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.InvalidAttributeName + getName()));
			return;
		}
		
		result = nameValidator.checkValidName(getAlias());
		if( result != null ) {
			setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.InvalidAttributeAliasName + getAlias()));
			return;
		}
		
		// Validate Paths
		
		setStatus(Status.OK_STATUS);
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("AttributeInfo: "); //$NON-NLS-1$
        text.append("  name =").append(getName()); //$NON-NLS-1$
        text.append("  alias =").append(getAlias()); //$NON-NLS-1$

        return text.toString();
    }
}
