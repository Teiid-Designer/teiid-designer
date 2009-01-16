package net.sourceforge.sqlexplorer.ext;

import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;



/**
 * @author Mazzolini
 *
 */
public abstract class DefaultEditorPlugin extends DefaultPlugin implements IEditorPlugin {
	public IContributionItem[] getContextMenuActions(SQLEditor editor) {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.ext.IEditorPlugin#getEditorToolbarActions(net.sourceforge.sqlexplorer.plugin.editors.SQLEditor)
	 */
	public IAction[] getEditorToolbarActions(SQLEditor editor) {
		return null;
	}

}
