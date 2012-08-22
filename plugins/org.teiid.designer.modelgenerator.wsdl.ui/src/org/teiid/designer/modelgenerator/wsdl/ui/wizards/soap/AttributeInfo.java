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
import org.teiid.query.sql.symbol.ElementSymbol;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;

public class AttributeInfo implements ModelGeneratorWsdlUiConstants {
	private static final StringNameValidator nameValidator = new RelationalStringNameValidator(false, true);

    /**
     * The unique column name (never <code>null</code> or empty).
     */
    private ElementSymbol nameSymbol;
	
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
        initNameSymbol(name);
        this.alias = getName();
		this.columnInfo = columnInfo;
		validate();
	}

    /**
     * Initialise the {@link ElementSymbol} to hold the name. This validates the symbol's character composition. The '.' character
     * is the only puntuation symbol that will cause problems for an element symbol so these are replaced these with '_'.
     */
    private void initNameSymbol( final String name ) {
        nameSymbol = new ElementSymbol(name.replaceAll("\\.", "_")); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Get the column name for display in the UI. This removes any quotes for aesthetic reasons. Use {@link #getSymbolName()} for
     * retrieving the fully validated column name.
     * 
     * @return the column name sans quotes.
     */
    public String getName() {
        String name = this.nameSymbol.toString();
        return name.replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Get the fully validated column name. This should be used in SQL string generation.
     * 
     * @return name the column name
     */
    public String getSymbolName() {
        return this.nameSymbol.toString();
    }

	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public void setName(String name) {
		CoreArgCheck.isNotNull(name, "name is null"); //$NON-NLS-1$
        initNameSymbol(name);
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

        String result = nameValidator.checkValidName(getAlias());
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
        text.append("  name =").append(getSymbolName()); //$NON-NLS-1$
        text.append("  alias =").append(getAlias()); //$NON-NLS-1$

        return text.toString();
    }
}
