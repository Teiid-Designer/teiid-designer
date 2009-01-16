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

package com.metamatrix.modeler.core.refactor;


/** 
 * This interface provides non-ui plugins to listener for changes in resources within the Designer workspace.
 * 
 * Currently Refactor actions for Move, Rename & Delete are wired to notify when commands are completed including Undo/Redo
 * @since 5.0
 */
public interface IRefactorResourceListener {

    void notifyRefactored(RefactorResourceEvent event);
}
