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

package com.metamatrix.modeler.internal.core.container;

import java.util.Collection;

import org.eclipse.emf.edit.command.CopyCommand;
import org.eclipse.emf.edit.command.CopyToClipboardCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

import com.metamatrix.modeler.internal.core.ModelEditorImpl;

/**
 * CopyToClipboardCommandWithMapping
 */
public class CopyToClipboardCommandWithMapping extends CopyToClipboardCommand {

    private CopyCommand.Helper helper;

    /**
     * Construct an instance of CopyToClipboardCommandWithMapping.
     * @param domain
     * @param collection
     */
    public CopyToClipboardCommandWithMapping(final EditingDomain domain, final Collection collection) {
        super(domain, collection);
        helper = new CopyCommand.Helper();
    }
    
    public CopyCommand.Helper getCopyKeyedByOriginalMap() {
        return helper;
    }

    /**
     * @see org.eclipse.emf.edit.command.CopyToClipboardCommand#prepare()
     */
    @Override
    protected boolean prepare() {
        // Create a copy command that uses our helper
        copyCommand = ModelEditorImpl.createCopyCommand(this.domain,this.sourceObjects,helper);
        return copyCommand.canExecute();
    }
    
    @Override
    public void doExecute() {
        super.doExecute();
        // Also put the map of originals to copies onto the editing domain ...
        if ( domain instanceof ContainerEditingDomain ) {
            ((ContainerEditingDomain)domain).setClipboardMapping(helper);
        }
    }
    
}
