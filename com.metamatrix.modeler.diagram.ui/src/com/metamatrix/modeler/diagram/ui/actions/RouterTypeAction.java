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

package com.metamatrix.modeler.diagram.ui.actions;

/** 
 * @since 4.2
 */
public class RouterTypeAction extends DiagramAction {
    private String type = null;
    private int index = -1;
    private RouterTypeMenuManager manager;
    /** 
     * @param thePlugin
     * @since 4.2
     */
    public RouterTypeAction(String type, int index, RouterTypeMenuManager manager) {
        super();
        this.type = type;
        setText(type);
        this.manager = manager;
        this.index = index;
    }


    /** 
     * @see com.metamatrix.ui.actions.AbstractAction#doRun()
     * @since 4.2
     */
    @Override
    protected void doRun() {
        manager.handleSelection(index, false);
    }

    public String getType() {
        return this.type;
    }
}
