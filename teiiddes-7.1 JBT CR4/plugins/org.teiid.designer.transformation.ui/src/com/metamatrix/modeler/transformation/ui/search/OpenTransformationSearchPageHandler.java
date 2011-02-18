package com.metamatrix.modeler.transformation.ui.search;

import static com.metamatrix.modeler.transformation.ui.UiConstants.Util;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenTransformationSearchPageHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        Shell shell = HandlerUtil.getActiveShell(event);
        Dialog dialog = new TransformationSearchDialog(shell, Util.getString("OpenTransformationSearchPageAction.dialog.title")); //$NON-NLS-1$
        dialog.open();
        return null; // per javadoc
    }

}
