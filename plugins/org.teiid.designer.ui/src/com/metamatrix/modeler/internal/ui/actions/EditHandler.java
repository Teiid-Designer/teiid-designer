package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public final class EditHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        // use action as it is used in context menus
        EditAction editAction = new EditAction();
        editAction.selectionChanged(HandlerUtil.getActivePart(event), HandlerUtil.getCurrentSelection(event));

        if (editAction.isEnabled()) {
            editAction.run();
        } else {
            assert false : "EditAction should be enabled. Check EditHandler's enabledWhen logic in plugin.xml."; //$NON-NLS-1$
        }

        return null; // per javadoc
    }

}
