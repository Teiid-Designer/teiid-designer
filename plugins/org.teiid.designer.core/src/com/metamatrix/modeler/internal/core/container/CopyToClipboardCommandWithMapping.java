/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
