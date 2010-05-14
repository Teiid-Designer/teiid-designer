package net.sourceforge.sqlexplorer.sqlpanel;

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

final public class SqlRowElement {
    SqlTableModel tableModel;

    public final Object getValue( int k ) {
        if (el[k] != null) return el[k].toString();
        return "<NULL>"; //$NON-NLS-1$
    }

    public final Object getInternalValue( int k ) {
        return el[k];
    }

    public final Object[] getInternalArray() {
        return el;
    }

    final Object[] el;

    SqlRowElement( Object[] obj,
                   int count,
                   SqlTableModel tableModel ) throws java.lang.Exception {
        this.tableModel = tableModel;
        el = new Object[count];

        for (int i = 0; i < count; i++) {
            el[i] = obj[i];
        }
    }

    public final int getSize() {
        return el.length;
    }

    public final Object getValue( String property ) {
        int i = tableModel.getColumnIndex(property);
        if (i != -1) {
            return getValue(i);
        }
        return null;
    }

    public final Object getInternalValue( String property ) {
        int i = tableModel.getColumnIndex(property);
        if (i != -1) {
            return getInternalValue(i);
        }
        return null;
    }
}
