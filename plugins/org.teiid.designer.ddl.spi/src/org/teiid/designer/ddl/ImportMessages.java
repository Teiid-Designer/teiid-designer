/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * DdlImportMessage
 * Message object which holds different types of import messages
 *
 * @since 8.2
 */
public class ImportMessages {

	// Parse Error Message Details
	private String parseErrorMessage;
    private String parserId;
    private boolean hasParseError = false;
	private int parseErrorLineNumber = -1;
    private int parseErrorColNumber = -1;
    private int parseErrorIndex = -1;
    
    // Progress Messages
    private List<String> progressMessages;
    // Unhandled Type Messages
    private Map<String,Integer> unhandledTypeCountMap;

    /**
     * Constructor
     */
    public ImportMessages( ) {
    }
        
    /**
     * Clear the messages
     */
    public void clear() {
    	this.parseErrorMessage = null;
        this.parserId = null;
        this.hasParseError = false;
    	this.parseErrorLineNumber = -1;
        this.parseErrorColNumber = -1;
        this.parseErrorIndex = -1;

        this.progressMessages = null;
    	this.unhandledTypeCountMap = null;
    }

    /**
     * Add a progress messages
     * @param message the progress message
     */
    public void addProgressMessage(String message) {
        if (progressMessages == null) {
        	progressMessages = new ArrayList<String>();
        }
        progressMessages.add(message);
    }

    /**
     * Get the progress messages
     *
     * @return messages
     */
    public List<String> getProgressMessages() {
        if (progressMessages == null) {
        	progressMessages = new ArrayList<String>();
        }

        return progressMessages;
    }
    
    /**
     * Get the unhandled type messages
     *
     * @return messages
     */
    public List<String> getUnhandledTypeMessages() {
    	List<String> unhandledTypeMessages = new ArrayList<String>();
        if (unhandledTypeCountMap == null) {
            return new ArrayList<String>();
        }
        
        Iterator<String> keyIter = unhandledTypeCountMap.keySet().iterator();
        while(keyIter.hasNext()) {
        	String typeStr = keyIter.next();
        	Integer typeCount = unhandledTypeCountMap.get(typeStr);
        	String message = typeCount + " instances of a DDL statement of type ["+typeStr+"] were found, but cannot be processed"; //$NON-NLS-1$ //$NON-NLS-2$
        	unhandledTypeMessages.add(message);
        }

        return unhandledTypeMessages;
    }
    
    /**
     * Get all messages
     *
     * @return messages
     */
    public List<String> getAllMessages() {
    	// All messages consists of progress messages, plus unhandled type message
    	List<String> allMessages = new ArrayList<String>(getProgressMessages());
    	
    	allMessages.addAll(getUnhandledTypeMessages());
    	return allMessages;
    }

    /**
     * Increments count of unhandled instances of a particular type
     * @param typeStr the node mixin type string
     */
    public void incrementUnhandledNodeType(String typeStr) {
    	if(unhandledTypeCountMap==null) {
    		unhandledTypeCountMap = new HashMap<String,Integer>();
    	}
    	if(unhandledTypeCountMap.containsKey(typeStr)) {
    		Integer count = unhandledTypeCountMap.get(typeStr);
    		count += 1;
    		unhandledTypeCountMap.put(typeStr, count);
    	} else {
    		unhandledTypeCountMap.put(typeStr, new Integer(1));
    	}
    }

    /**
     * Set the parse error message
     * @param message the error message
     */
    public void setParseErrorMessage(String message) {
    	this.parseErrorMessage = message;
    }
    
    /**
     * Get the parse error message
     * @return the parse error message
     */
    public String getParseErrorMessage() {
    	return this.parseErrorMessage;
    }
   
    /**
	 * @return 'true' if hasParseError
	 */
	public boolean hasParseError() {
		return this.hasParseError;
	}

	/**
	 * @param hasParseError 'true' if parse error status is true
	 */
	public void setHasParseError(boolean hasParseError) {
		this.hasParseError = hasParseError;
	}

	/**
	 * @return the lineNumber
	 */
	public int getParseErrorLineNumber() {
		return this.parseErrorLineNumber;
	}

	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setParseErrorLineNumber(int lineNumber) {
		this.parseErrorLineNumber = lineNumber;
	}

	/**
	 * @return the colNumber
	 */
	public int getParseErrorColNumber() {
		return this.parseErrorColNumber;
	}

	/**
	 * @param colNumber the colNumber to set
	 */
	public void setParseErrorColNumber(int colNumber) {
		this.parseErrorColNumber = colNumber;
	}

	/**
	 * @return the parserId
	 */
	public String getParserId() {
		return parserId;
	}

	/**
	 * @param parserId the parserId to set
	 */
	public void setParserId(String parserId) {
		this.parserId = parserId;
	}

	/**
	 * @return the index
	 */
	public int getParseErrorIndex() {
		return parseErrorIndex;
	}

	/**
	 * @param index the index to set
	 */
	public void setParseErrorIndex(int index) {
		this.parseErrorIndex = index;
	}

}
