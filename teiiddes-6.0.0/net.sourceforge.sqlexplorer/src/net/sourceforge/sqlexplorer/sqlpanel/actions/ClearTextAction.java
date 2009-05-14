/*
 * Copyright (C) 2003 Luc Jouneau
 * ljouneau@yahoo.com
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

package net.sourceforge.sqlexplorer.sqlpanel.actions;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public class ClearTextAction extends Action {

    private ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getClearTextIcon());
    private SQLEditor txtComposite;

    public ClearTextAction( SQLEditor txtComposite ) {
        this.txtComposite = txtComposite;
    }

    @Override
    public String getText() {
        return Messages.getString("Clear_1"); //$NON-NLS-1$
    }

    @Override
    public void run() {
        txtComposite.clearText();
    }

    @Override
    public String getToolTipText() {
        return Messages.getString("Clear_2"); //$NON-NLS-1$
    }

    @Override
    public ImageDescriptor getHoverImageDescriptor() {
        return img;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return img;
    }
}
