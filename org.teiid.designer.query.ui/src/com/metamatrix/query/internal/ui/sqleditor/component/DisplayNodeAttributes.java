/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import javax.swing.text.SimpleAttributeSet;

/**
 * The <code>DisplayNodeAttributes</code> class contains all of the settable
 * attributes of Display Nodes.
 */
public class DisplayNodeAttributes {

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    private boolean clauseIndentOn = true;
    private boolean statementIndentOn = false;
    private boolean clauseCROn = true;
    private boolean statementCROn = true;
    private int indentLevel = 0;
    private SimpleAttributeSet textAttribute = null;

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////
    public DisplayNodeAttributes( ) {
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sets the text attribute for this Display Node
     */
    public void setTextAttribute( SimpleAttributeSet attribute ) {
        this.textAttribute = attribute;
    }

    /**
     * Sets the indent level for this Display Node
     */
    public void setIndentLevel( int indent ) {
        this.indentLevel = indent;
    }

    /**
     * Sets the statement indent status for this Display Node
     */
    public void setStatementIndentOn( boolean status ) {
    	this.statementIndentOn=status;
    }
    
    /**
     * Sets the clause indent status for this Display Node
     */
    public void setClauseIndentOn( boolean status ) {
    	this.clauseIndentOn=status;
    }

     /**
     * Sets the statement CR (Carriage Return) status for this Display Node
     */
    public void setStatementCROn( boolean status ) {
    	this.statementCROn=status;
    }
    
     /**
     * Sets the clause CR (Carriage Return) status for this Display Node
     */
    public void setClauseCROn( boolean status ) {
    	this.clauseCROn=status;
    }

    /**
     * Gets the text attribute for this Display Node
     */
    public SimpleAttributeSet getTextAttribute( ) {
        return this.textAttribute;
    }

    /**
     * Gets the indent level for this Display Node
     */
    public int getIndentLevel( ) {
        return this.indentLevel;
    }
    
    /**
     * Gets the statement indent status for the this DisplayNode
     */
    public boolean isStatementIndentOn( ) {
    	return this.statementIndentOn;
    }
    
    /**
     * Gets the clause indent status for the this DisplayNode
     */
    public boolean isClauseIndentOn( ) {
       return this.clauseIndentOn;
    }
    
    /**
     * Gets the statement CR status for the this DisplayNode
     */
    public boolean isStatementCROn( ) {
    	return this.statementCROn;
    }
    
    /**
     * Gets the clause CR status for the this DisplayNode
     */
    public boolean isClauseCROn( ) {
    	return this.clauseCROn;
    }
    
    @Override
    public Object clone() {
    	DisplayNodeAttributes copy = new DisplayNodeAttributes();
    	copy.setClauseIndentOn(isClauseIndentOn());
    	copy.setClauseCROn(isClauseCROn());
    	copy.setStatementIndentOn(isStatementIndentOn());
    	copy.setStatementCROn(isStatementCROn());
    	copy.setIndentLevel(getIndentLevel());
    	copy.setTextAttribute(getTextAttribute());
    	return copy;
    }

}

