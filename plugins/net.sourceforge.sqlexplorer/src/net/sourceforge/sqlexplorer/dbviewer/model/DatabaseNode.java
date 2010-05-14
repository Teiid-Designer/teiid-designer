package net.sourceforge.sqlexplorer.dbviewer.model;

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

import java.util.ArrayList;
import net.sourceforge.sqlexplorer.dbviewer.DetailManager;
import org.eclipse.swt.widgets.Composite;

public class DatabaseNode implements IDbModel {

    public Composite getComposite( DetailManager detailManager ) {
        return null;
    }

    ArrayList children = new ArrayList(10);

    public void add( IDbModel e ) {
        children.add(e);
    }

    public DatabaseNode( String txt ) {
        this.txt = txt;
    }

    public Object[] getChildren() {
        return children.toArray();
    }

    String txt;

    public Object getParent() {
        return null;
    }

    @Override
    public String toString() {
        return txt;
    }
}
