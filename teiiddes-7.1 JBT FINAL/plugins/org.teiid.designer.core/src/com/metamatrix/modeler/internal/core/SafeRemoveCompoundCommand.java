/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.edit.command.RemoveCommand;

/** Subclass of CompoundCommand that takes special precautions when dealing
  *  with RemoveCommands -- see internal comments.
  *  
  * @see com.metamatrix.modeler.internal.core.ModelEditorImpl.CompoundCommandFactory
  * @since 4.3
  */
public class SafeRemoveCompoundCommand extends CompoundCommand
{
    public SafeRemoveCompoundCommand(List list) {
        super(list);
    }

    /** Copied from CompoundCommand.  Makes sure that owner lists of RemoveCommands 
      *  contain the object they are trying to delete before executing the command.
      * This can happen with some XSD-related lists, where some are kept in sync with
      *  others via notifications.
      * @see CompoundCommand#execute()
      */
    @Override
    public void execute() {
        for (ListIterator commands = commandList.listIterator(); commands.hasNext();) {
            try {
                Command command = (Command) commands.next();
                if (command instanceof RemoveCommand) {
                    RemoveCommand rmc = (RemoveCommand) command;
                    Collection col = rmc.getCollection();
                    // We limit the case where this class does anything to when there is only 
                    //  one thing to be removed.  While there are times that a RemoveCommand can
                    //  have more than one thing to remove, these do not occur in the case this 
                    //  class was designed for:  deletions involving elists that listen to other 
                    //  elists.  For example, deletions in this case are all one-object affairs,
                    //  and are done after the primary deletion (the only one possibly containing
                    //  multiple objects to delete).
                    if (col.size() == 1
                     && !rmc.getOwnerList().containsAll(col)) {
                        // we are missing the one item we need to remove;
                        //  do not run the command (and remove it from the 
                        //  set of commands so we don't try to undo it.
                        commands.remove();
                        continue;
                    } // endif -- 1 item and owner list contains item to remove
                } //endif --

                command.execute();

            } catch (RuntimeException exception) {
                // Skip over the command that threw the exception.
                //
                commands.previous();

                try {
                    // Iterate back over the executed commands to undo them.
                    //
                    while (commands.hasPrevious()) {
                        Command command = (Command) commands.previous();
                        if (command.canUndo()) {
                            command.undo();
                        } else {
                            break;
                        }
                    }
                } catch (RuntimeException nestedException) {
                    CommonPlugin.INSTANCE.log(new WrappedException(
                                    CommonPlugin.INSTANCE.getString("_UI_IgnoreException_exception"), //$NON-NLS-1$
                                    nestedException).fillInStackTrace());
                } // endtry

                throw exception;
            }
        }
    }
}
