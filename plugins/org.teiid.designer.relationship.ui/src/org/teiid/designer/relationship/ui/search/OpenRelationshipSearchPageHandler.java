package org.teiid.designer.relationship.ui.search;

import static org.teiid.designer.relationship.ui.UiConstants.Extensions.SearchPage.ID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public final class OpenRelationshipSearchPageHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        NewSearchUI.openSearchDialog(window, ID);
        return null; // per javadoc
    }

}
