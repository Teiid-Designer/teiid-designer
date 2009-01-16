/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.query.internal.ui.builder.util;

import org.eclipse.jface.viewers.TreeViewer;

import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.Command;

/**
 * The <code>CriteriaStrategy</code> interface is used by the <code>SetCriteriaController</code>
 * when subquery are used.
 * @author Dan Florian
 * @since 3.1
 * @version 1.0
 */
public interface ICriteriaStrategy {

    /**
     * Gets an appropriate <code>Command</code> for the given node.
     * @param theNode the node to create the command from
     * @return the requested command or <code>null</code> if a command can't be created
     */
    Command getCommand(Object theNode);
    
    /**
     * Gets the runtime full name of the given node. This full name will be used when creating 
     * a <code>LanguageObject</code> for that node.
     * @param theNode the node whose runtime full name is being requested
     * @return the full name
     */
    String getRuntimeFullName(Object theNode);
    
    /**
     * Converts the given <code>LanguageObject</code> to an object type appropriate for the strategy.
     * @param theLangObj the object being converted
     * @return an object typed according to the strategy
     */
    Object getNode(LanguageObject theLangObj);
    
    /**
     * Gets a user message indicating why the given node is invalid.
     * @param theNode the node whose associated invalid message is being requested
     * @return a user message
     */
    String getInvalidMessage(Object theNode);
    
    /**
     * Gets the <code>TreeViewer</code> associated with this strategy.
     * @return the viewer
     */
    TreeViewer getTreeViewer();

    /**
     * Indicates if the given node can be used to create a valid {@link Command}.
     * @param theNode the node being checked
     * @return <code>true</code> if the node is valid; <code>false</code> otherwise.
     */
    boolean isValid(Object theNode);
    
    /**
     * Sets the <code>TreeViewer</code> on the strategy.
     * @param theView the view being set
     */
    void setTreeViewer(TreeViewer theView);

}
