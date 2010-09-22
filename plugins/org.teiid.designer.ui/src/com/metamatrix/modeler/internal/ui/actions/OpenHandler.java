package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public final class OpenHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        // use action as it is used in context menus
        OpenAction openAction = new OpenAction();
        openAction.selectionChanged(HandlerUtil.getActivePart(event), HandlerUtil.getCurrentSelection(event));

        if (openAction.isEnabled()) {
            openAction.run();
        } else {
            assert false : "OpenAction should be enabled. Check plugin.xml handler enabledWhen."; //$NON-NLS-1$
        }

        return null; // per javadoc
    }

}
