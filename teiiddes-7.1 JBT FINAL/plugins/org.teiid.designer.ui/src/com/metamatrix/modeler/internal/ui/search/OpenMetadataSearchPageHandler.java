package com.metamatrix.modeler.internal.ui.search;

import static com.metamatrix.modeler.ui.UiConstants.Extensions.METADATA_SEARCH_PAGE;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public final class OpenMetadataSearchPageHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        NewSearchUI.openSearchDialog(window, METADATA_SEARCH_PAGE);
        return null; // per javadoc
    }

}
