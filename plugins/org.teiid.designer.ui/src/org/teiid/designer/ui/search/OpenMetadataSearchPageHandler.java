package org.teiid.designer.ui.search;

import static org.teiid.designer.ui.UiConstants.Extensions.METADATA_SEARCH_PAGE;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @since 8.0
 */
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
