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

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import net.sourceforge.sqlexplorer.Messages;

class IndexDetailRow implements IndexInterface {

    private ArrayList ar = new ArrayList(10);
    private IndexDetailTableModel parent;
    private Object[] el;

    IndexDetailRow( String str,
                    boolean nonUnique,
                    short type,
                    IndexDetailTableModel idtm ) throws java.lang.Exception {
        parent = idtm;
        el = new Object[5];
        el[0] = str;
        if (nonUnique) el[1] = Messages.getString("No_1"); //$NON-NLS-1$
        else el[1] = Messages.getString("Yes_2"); //$NON-NLS-1$
        if (type == DatabaseMetaData.tableIndexClustered) el[2] = new String(Messages.getString("Clustered_3")); //$NON-NLS-1$
        else if (type == DatabaseMetaData.tableIndexHashed) el[2] = new String(Messages.getString("Hashed_4")); //$NON-NLS-1$
        else if (type == DatabaseMetaData.tableIndexOther) el[2] = new String(Messages.getString("Other_5")); //$NON-NLS-1$

    }

    public Object getValue( int k ) {
        return el[k];
    }

    public void addChild( IndexDetailSubRow sub ) {
        ar.add(sub);
    }

    public Object[] getChildren() {
        return ar.toArray();
    }

    public Object getParent() {
        return parent;
    }
}
