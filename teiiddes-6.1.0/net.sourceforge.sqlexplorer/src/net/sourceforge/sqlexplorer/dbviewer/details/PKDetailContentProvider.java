package net.sourceforge.sqlexplorer.dbviewer.details;

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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

class PKDetailContentProvider implements ITreeContentProvider {

    public void dispose() {
    }

    public void inputChanged( Viewer viewer,
                              Object arg1,
                              Object arg2 ) {
    }

    public Object[] getChildren( Object input ) {
        return ((PKInterface)input).getChildren();
    }

    public Object getParent( Object e ) {
        return ((PKInterface)e).getParent();
    }

    public boolean hasChildren( Object element ) {
        Object[] ar = getChildren(element);
        if (ar == null) return false;
        return (ar.length > 0);
    }

    public Object[] getElements( Object input ) {
        return getChildren(input);
    }
}
