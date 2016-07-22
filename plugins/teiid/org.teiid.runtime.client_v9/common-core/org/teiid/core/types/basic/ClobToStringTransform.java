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

package org.teiid.core.types.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import org.teiid.core.types.ClobType;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;


public class ClobToStringTransform extends AnyToStringTransform {

	public ClobToStringTransform(DataTypeManagerService dataTypeManager) {
		super(dataTypeManager, DefaultDataTypes.CLOB.getTypeClass());
	}
	
    /**
     * This method transforms a value of the source type into a value
     * of the target type.
     * @param value Incoming value of source type
     * @return Outgoing value of target type
     * @throws Exception if value is an incorrect input type or
     * the transformation fails
     */
    public Object transformDirect(Object value) throws Exception {
        ClobType source = (ClobType)value;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader (source.getCharacterStream());
            StringBuffer contents = new StringBuffer();
            
            int chr = reader.read();
            while (chr != -1 && contents.length() < DataTypeManagerService.MAX_STRING_LENGTH) {
                contents.append((char)chr);
                chr = reader.read();
            }
            return contents.toString();         
        } catch (SQLException e) {
              throw new TeiidClientException(e, Messages.gs(Messages.TEIID.TEIID10080, new Object[] {getSourceType().getName(), getTargetType().getName()}));
        } catch(IOException e) {
              throw new TeiidClientException(e, Messages.gs(Messages.TEIID.TEIID10080, new Object[] {getSourceType().getName(), getTargetType().getName()}));
        } finally {
        	if (reader != null) {
        		try {
					reader.close();
				} catch (IOException e) {
				}
        	}
        }
    }

    /** 
     * @see org.teiid.core.types.Transform#isExplicit()
     */
    public boolean isExplicit() {
        return true;
    }
}
