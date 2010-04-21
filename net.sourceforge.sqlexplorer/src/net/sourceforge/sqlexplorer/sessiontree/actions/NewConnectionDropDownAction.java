package net.sourceforge.sqlexplorer.sessiontree.actions;

import net.sourceforge.sqlexplorer.AliasModel;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 */
public class NewConnectionDropDownAction extends Action implements IMenuCreator, IViewActionDelegate {
    private Menu menu;

    public NewConnectionDropDownAction() {
        setText("Open New Connection");
        setToolTipText("Open New Connection");
        setImageDescriptor(ImageDescriptor.createFromURL(SqlexplorerImages.getCreateDriverIcon()));
        setMenuCreator(this);
    }

    protected void addActionToMenu( final Menu parent,
                                    final Action action ) {
        final ActionContributionItem item = new ActionContributionItem(action);
        item.fill(parent, -1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IMenuCreator#dispose()
     */
    public void dispose() {
        if (menu != null) menu.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
     */
    public Menu getMenu( final Control parent ) {
        if (menu != null) {
            menu.dispose();
            menu = null;
        }

        final AliasModel aliasModel = SQLExplorerPlugin.getDefault().getAliasModel();
        final Object[] aliases = aliasModel.getElements();
        if (aliases != null) {
            menu = new Menu(parent);
            for (final Object aliase : aliases) {
                final NewConnection action = new NewConnection((ISQLAlias)aliase);
                addActionToMenu(menu, action);
            }
        }
        return menu;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
     */
    public Menu getMenu( final Menu parent ) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init( final IViewPart view ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( final IAction action ) {
        System.out.println("This rocks");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( final IAction action,
                                  final ISelection selection ) {
    }
}
