package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public final class CloneHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        // use action as it could be used in context menus so keep execute logic in one place
        CloneAction action = new CloneAction();
        action.selectionChanged(HandlerUtil.getActivePart(event), HandlerUtil.getCurrentSelection(event));

        if (action.isEnabled()) {
            action.run();
        } else {
            assert false : "CloneAction is not enabled. Check plugin.xml handler enabledWhen."; //$NON-NLS-1$
        }

        return null; // per javadoc
    }

}
