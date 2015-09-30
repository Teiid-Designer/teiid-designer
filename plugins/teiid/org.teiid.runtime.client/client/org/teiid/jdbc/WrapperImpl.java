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

package org.teiid.jdbc;

import java.sql.SQLException;
import java.sql.Wrapper;
import org.teiid.core.util.ArgCheck;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.runtime.client.Messages;


public class WrapperImpl implements Wrapper {

    private final ITeiidServerVersion teiidVersion;

    public WrapperImpl(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
    }

    /**
     * @return the teiidVersion
     */
    public ITeiidServerVersion getTeiidVersion() {
        return this.teiidVersion;
    }

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		ArgCheck.isNotNull(iface);
		
		return iface.isInstance(this);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (!isWrapperFor(iface)) {
			throw new SQLException(Messages.getString(Messages.JDBC.wrong_class, iface));
		}
		
		return iface.cast(this);
	}
}
