/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.tools.textimport.ui.wizards;

import org.teiid.designer.core.validation.rules.StringNameValidator;




/** 
 * @since 4.2
 */
public abstract class AbstractRowObject implements IRowObject {
	private static final StringNameValidator nameValidator = new StringNameValidator();
	
    private String rawString;
    private String dataString;
    private String name;
    private String location;
    private String description;
    public int type;
    private boolean valid = false;
    /** 
     * 
     * @since 4.2
     */
    public AbstractRowObject(String row) {
        super();
        this.dataString = row;
    }
    
    
    @Override
	public abstract void parseRow();
    
    protected String parseDescription(String subString) {
        // Remove unneeded double quotes
        // Walk char by char
        StringBuffer buffer = new StringBuffer();
        boolean removedFirstDQuote = false;
        boolean addChar = false;
        int length = subString.length();
        for(int i=0; i<length; i++ ) {
            // check i and i+1 for dquotes
            if( i < length-2 && subString.charAt(i) == (DQUOTE) ) {
                if(removedFirstDQuote) {
                    if( subString.charAt(i+1) == (DQUOTE))
                        addChar = false;
                    else
                        addChar = true;
                } else {
                    // skip the first dQuote
                    removedFirstDQuote = true;
                }
            } else if( subString.charAt(i) == (DQUOTE) && i == length-1) {
                addChar = false; 
            } else {
                addChar = true;
            }
            
            if( addChar )
                buffer.append(subString.charAt(i));
        }
        
        return buffer.toString();
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Relational Row Object")             //$NON-NLS-1$
            .append("\n    Name               = " + name )               //$NON-NLS-1$
            .append("\n    Description        = " + description);   //$NON-NLS-1$
        
        return buffer.toString();
    }
    
    @Override
	public boolean isValid() {
        return this.valid;
    }
    
    @Override
	public String getDescription() {
        return this.description;
    }
    
    @Override
	public void setDescription(String desc) {
        this.description = desc;
    }
    
    @Override
	public String getName() {
        return this.name;
    }
    
    @Override
	public void setName(String name) {
        this.name = createValidName(name, true);
    }
    
    @Override
	public String getLocation() {
        return this.location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    @Override
	public int getObjectType() {
        return this.type;
    }
    public void setObjectType(int type) {
        this.type = type;
    }
    
    @Override
	public String getDataString() {
        return this.dataString;
    }
    @Override
	public String getRawString() {
        return this.rawString;
    }
    @Override
	public void setRawString(String rawString) {
        this.rawString = rawString;
    }
    
    public String createValidName(String input, boolean performValidityCheck) {
        String validName = nameValidator.createValidName(input, performValidityCheck);
        if( validName != null )
            return validName;
        
        return input;
    }
}
