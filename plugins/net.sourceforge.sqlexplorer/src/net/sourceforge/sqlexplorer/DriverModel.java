package net.sourceforge.sqlexplorer;

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

import java.net.MalformedURLException;
import java.util.Comparator;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;

public class DriverModel {
    // private int count=0;
    // private ArrayList ls=new ArrayList();
    private SortedList sl;
    private DataCache cache;

    public DriverModel( DataCache c ) {
        cache = c;

        sl = new SortedList(iSQLDriverComparator);
        sl.addAll(c.drivers());

    }

    // private DataCache cache;
    public int size() {
        return sl.size();

    }

    public ISQLDriver getElement( int i ) {

        return (ISQLDriver)sl.get(i);

    }

    public Object[] getElements() {
        return sl.toArray();
    }

    public void removeDriver( ISQLDriver dv ) {
        cache.removeDriver(dv);

        sl.remove(dv);

    }

    public ISQLDriver getDriver( IIdentifier id ) {
        return cache.getDriver(id);
    }

    public ISQLDriver createDriver( IIdentifier id ) {
        return cache.createDriver(id);
    }

    public void addDriver( ISQLDriver dv )
        throws DuplicateObjectException, ClassNotFoundException, java.lang.IllegalAccessException,
        java.lang.InstantiationException, MalformedURLException {
        cache.addDriver(dv);
        sl.add(dv);

    }

    private static ISQLDriverComparator iSQLDriverComparator = new ISQLDriverComparator();

    static class ISQLDriverComparator implements Comparator {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare( Object o1,
                            Object o2 ) {
            return ((ISQLDriver)o1).getName().compareToIgnoreCase(((ISQLDriver)o2).getName());
        }

    }
}
