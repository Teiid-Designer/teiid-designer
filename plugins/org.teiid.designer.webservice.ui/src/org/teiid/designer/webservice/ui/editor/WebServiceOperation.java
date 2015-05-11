package org.teiid.designer.webservice.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDElementDeclaration;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import org.teiid.designer.metamodels.webservice.Interface;
import org.teiid.designer.metamodels.xml.XmlDocument;
//import org.teiid.designer.relational.RelationalPlugin;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.webservice.ui.WebServiceUiPlugin;

public class WebServiceOperation extends RelationalReference {
	String inputMessageName;
	String outputMessageName;
	
	XSDElementDeclaration inputContentViaElement;
	XSDElementDeclaration outputContentViaElement;
	XmlDocument xmlDocument;
	
	List<String> existingNames;
	

    private String transformationSQL;
    
    
	public WebServiceOperation() {
		super();
        setType(TYPES.OPERATION);
        setNameValidator(new RelationalStringNameValidator(false));
	}

	public WebServiceOperation(String name, Interface intFace) {
		super(name);
        setType(TYPES.OPERATION);
        setNameValidator(new RelationalStringNameValidator(false));
        init(intFace);
	}
	
	private void init(Interface intFace) {
		existingNames = new ArrayList<String>();
		for( Object oper : intFace.getOperations()) {
			String name = ModelerCore.getModelEditor().getName((EObject)oper);
			existingNames.add(name.toUpperCase());
		}
		String name = getName();
		setName(getNewUniqueName(name));
	}

    /**
     * @param sql the transformation SQL
     */
    public void setTransformationSQL( String sql ) {
        this.transformationSQL = sql;
    }

    /**
     * @return the transformation SQL
     */
    public String getTransformationSQL() {
        return this.transformationSQL;
    }

	public String getInputMessageName() {
		return inputMessageName;
	}

	public void setInputMessageName(String inputMessageName) {
		this.inputMessageName = inputMessageName;
	}

	public String getOutputMessageName() {
		return outputMessageName;
	}

	public void setOutputMessageName(String outputMessageName) {
		this.outputMessageName = outputMessageName;
	}

	public XSDElementDeclaration getInputContentViaElement() {
		return inputContentViaElement;
	}

	public void setInputContentViaElement(XSDElementDeclaration inputContentViaElement) {
		this.inputContentViaElement = inputContentViaElement;
	}

	public XSDElementDeclaration getOutputContentViaElement() {
		return outputContentViaElement;
	}

	public void setOutputContentViaElement(XSDElementDeclaration outputContentViaElement) {
		this.outputContentViaElement = outputContentViaElement;
	}

	public XmlDocument getXmlDocument() {
		return xmlDocument;
	}

	public void setXmlDocument(XmlDocument xmlDocument) {
		this.xmlDocument = xmlDocument;
	}

	@Override
	public void validate() {
		// Walk through the properties for the table and set the status
		super.validate();
		
		if( getStatus().getSeverity() == IStatus.ERROR ) {
			return;
		}
		
		if( existingNames.contains(getName().toUpperCase()) ) {
			setStatus(new Status(IStatus.ERROR, WebServiceUiPlugin.PLUGIN_ID, 
					WebServiceUiPlugin.UTIL.getString("WebServiceOperation.validate_nameAlreadyExists", getName()) ));
			return;
		}
		
		// Check for null input message name
		if( StringUtilities.isEmpty(this.inputMessageName)) {
			setStatus(new Status(IStatus.WARNING, WebServiceUiPlugin.PLUGIN_ID, 
					WebServiceUiPlugin.UTIL.getString("WebServiceOperation.validate_inputMessageEmptyOrNull") ));
			return;
		}
		
		// Check for null output message name
		if( StringUtilities.isEmpty(this.outputMessageName)) {
			setStatus(new Status(IStatus.WARNING, WebServiceUiPlugin.PLUGIN_ID, 
					WebServiceUiPlugin.UTIL.getString("WebServiceOperation.validate_outputMessageEmptyOrNull") ));
			return;
		}
		
		// Check for null input content via element
		if( this.inputContentViaElement == null ) {
			setStatus(new Status(IStatus.WARNING, WebServiceUiPlugin.PLUGIN_ID, 
					WebServiceUiPlugin.UTIL.getString("WebServiceOperation.validate_inputContentViaElementNull") ));
			return;
		}
		
		// Check for null output content via element
		if( this.outputContentViaElement == null ) {
			setStatus(new Status(IStatus.WARNING, WebServiceUiPlugin.PLUGIN_ID, 
					WebServiceUiPlugin.UTIL.getString("WebServiceOperation.validate_outputContentViaElementNull") ));
			return;
		}
		
		// Check for null output xml document
		if( this.xmlDocument == null ) {
			setStatus(new Status(IStatus.WARNING, WebServiceUiPlugin.PLUGIN_ID, 
					WebServiceUiPlugin.UTIL.getString("WebServiceOperation.validate_outputXmlDocumentNull") ));
			return;
		}
	}
	
    private String getNewUniqueName(String proposedName) {

    	
    	if( existingNames.contains(proposedName.toUpperCase()) ) {
	    	
    		// We have duplicate model names
    		int count = 1;
    		String newName = proposedName + '_' + Integer.toString(count);
    		
    		while( existingNames.contains(newName.toUpperCase()) && count < 20 ) {
    			count++;
    			if( !existingNames.contains(newName.toUpperCase()) ) {
    				return newName;
    			}
    			newName = proposedName + '_' + Integer.toString(count);
    		}
    		return newName;
    	}
    	
    	return proposedName;
    }
    
    
}
