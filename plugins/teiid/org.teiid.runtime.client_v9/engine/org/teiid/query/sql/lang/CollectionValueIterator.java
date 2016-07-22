/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.sql.lang;

import java.util.Collection;
import java.util.Iterator;
import org.teiid.designer.query.sql.lang.ICollectionValueIterator;
import org.teiid.query.sql.util.ValueIterator;


/**
 *
 */
public class CollectionValueIterator implements ValueIterator, ICollectionValueIterator {

    private Collection vals;
    
    private Iterator instance = null;
    
    /**
     * @param vals
     */
    public CollectionValueIterator(Collection vals) {
        this.vals = vals;
    }
    
    /** 
     * @see org.teiid.query.sql.util.ValueIterator#hasNext()
     * @since 4.3
     */
    @Override
    public boolean hasNext() throws Exception {
        if (instance == null) {
            this.instance = vals.iterator();
        }
        return this.instance.hasNext();
    }

    /** 
     * @see org.teiid.query.sql.util.ValueIterator#next()
     * @since 4.3
     */
    @Override
    public Object next() throws Exception {
        if (instance == null) {
            this.instance = vals.iterator();
        }
        return this.instance.next();
    }

    /** 
     * @see org.teiid.query.sql.util.ValueIterator#reset()
     * @since 4.3
     */
    @Override
    public void reset() {
        this.instance = null;
    }
    
}