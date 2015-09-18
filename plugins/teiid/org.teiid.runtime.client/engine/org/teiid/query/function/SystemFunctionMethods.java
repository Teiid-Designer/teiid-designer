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

package org.teiid.query.function;

import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.metadata.FunctionMethod.Determinism;
import org.teiid.query.function.metadata.FunctionCategoryConstants;
import org.teiid.query.util.CommandContext;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;

@Since(Version.TEIID_8_0)
public class SystemFunctionMethods {
	
	private static final int MAX_VARIABLES = 512;

	@TeiidFunction(category=FunctionCategoryConstants.SYSTEM, nullOnNull=true, determinism=Determinism.COMMAND_DETERMINISTIC)
	public static Object teiid_session_get(CommandContext context, String key) {
		return context.getSessionVariable(key);
	}

	@TeiidFunction(category=FunctionCategoryConstants.SYSTEM, determinism=Determinism.COMMAND_DETERMINISTIC)
	public static Object teiid_session_set(CommandContext context, String key, Object value) throws Exception {
	    Object oldValue = context.getSessionVariable(key);
		if (context.getSessionVariableCount() > MAX_VARIABLES && oldValue == null) {
			throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID31136, MAX_VARIABLES));
		}
		return context.setSessionVariable(key, value);
	}

}
