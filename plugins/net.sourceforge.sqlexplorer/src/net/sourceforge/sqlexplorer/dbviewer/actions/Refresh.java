package net.sourceforge.sqlexplorer.dbviewer.actions;

/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.plugin.views.DBView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Mazzolini
 */
public class Refresh extends Action {
    private ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getRefreshIcon());
    DBView dbView;

    @Override
    public ImageDescriptor getImageDescriptor() {
        return img;
    }

    public Refresh( DBView dbView ) {
        super();
        this.dbView = dbView;
    }

    @Override
    public void run() {
        dbView.refresh();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#getText()
     */
    @Override
    public String getText() {

        return Messages.getString("Refresh.refresh_1"); //$NON-NLS-1$
    }

}
