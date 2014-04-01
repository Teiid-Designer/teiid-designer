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

package org.teiid.net.socket;

import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

public enum AuthenticationType {
    @Since("8.7.0")
    USERPASSWORD,

    GSS,

    @Removed("8.7.0")
    CLEARTEXT;

    private static boolean lessThan87(ITeiidServerVersion teiidVersion) {
        return teiidVersion.isLessThan(Version.TEIID_8_7.get());
    }

    /**
     * @param teiidVersion
     * @param readByte
     *
     * @return enum value
     */
    public static AuthenticationType value(ITeiidServerVersion teiidVersion, byte readByte) {
        switch (readByte) {
            case 0:
                if (lessThan87(teiidVersion))
                    return CLEARTEXT;
                else
                    return USERPASSWORD;
            case 1:
                return GSS;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * @param teiidVersion
     * @return Same as ordinal but handle deprecated inclusion of CLEARTEXT
     */
    public int index(ITeiidServerVersion teiidVersion) {
        if (lessThan87(teiidVersion) && this == CLEARTEXT)
            return 0;

        return this.ordinal();
    }
}