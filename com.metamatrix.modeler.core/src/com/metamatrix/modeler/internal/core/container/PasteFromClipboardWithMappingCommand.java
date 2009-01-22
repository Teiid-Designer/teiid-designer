/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.container;

import java.util.Collections;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandWrapper;
import org.eclipse.emf.common.command.StrictCompoundCommand;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CopyCommand;
import org.eclipse.emf.edit.command.PasteFromClipboardCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;

/**
 * PasteFromClipboardWithMappingCommand
 */
public class PasteFromClipboardWithMappingCommand extends PasteFromClipboardCommand {

    /**
     * This creates a command to add copies from the clipboard to the specified feature of the owner.
     */
    public static Command create( EditingDomain domain,
                                  Object owner,
                                  Object feature ) {
        return create(domain, owner, feature, CommandParameter.NO_INDEX);
    }

    /**
     * This creates a command to add copies from the clipboard to the specified feature of the owner and at the given index.
     */
    public static Command create( EditingDomain domain,
                                  Object owner,
                                  Object feature,
                                  int index ) {
        if (domain == null) {
            return new PasteFromClipboardWithMappingCommand(domain, owner, feature, index, true);
        }
        Command command = domain.createCommand(PasteFromClipboardCommand.class, new CommandParameter(owner, feature,
                                                                                                     Collections.EMPTY_LIST,
                                                                                                     index));
        return command;
    }

    private CopyCommand.Helper helper;

    /**
     * This constructs an instance from the domain, which provides access the clipboard collection via
     * {@link EditingDomain#getCommandStack}.
     */
    public PasteFromClipboardWithMappingCommand( EditingDomain domain,
                                                 Object owner,
                                                 Object feature,
                                                 int index ) {
        this(domain, owner, feature, index, true);
    }

    public PasteFromClipboardWithMappingCommand( EditingDomain domain,
                                                 Object owner,
                                                 Object feature,
                                                 int index,
                                                 boolean optimize ) {
        super(domain, owner, feature, index, optimize);
        this.helper = new CopyCommand.Helper();
    }

    public CopyCommand.Helper getHelper() {
        return this.helper;
    }

    @Override
    protected boolean prepare() {
        // Create a strict compound command to do a copy and then add the result
        //
        command = new StrictCompoundCommand();

        // Create a command to copy the clipboard.
        //
        final Command copyCommand = ModelEditorImpl.createCopyCommand(domain, domain.getClipboard(), helper);
        command.append(copyCommand);

        // Create a proxy that will create an add command.
        //
        command.append(new CommandWrapper() {
            @Override
            protected Command createCommand() {
                Command addCommand = AddCommand.create(getDomain(), getOwner(), getFeature(), copyCommand.getResult(), getIndex());
                return addCommand;
            }
        });

        boolean result;
        if (optimize) {
            // This will determine canExecute as efficiently as possible.
            //
            result = optimizedCanExecute();
        } else {
            // This will actually execute the copy command in order to check if the add can execute.
            //
            result = command.canExecute();
        }

        return result;
    }
}
