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

package org.teiid.query.resolver.command;

import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.metadata.Table.TriggerEvent;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.resolver.CommandResolver;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.sql.lang.Alter;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.AlterTrigger;
import org.teiid.query.sql.lang.Command;
import org.teiid.runtime.client.Messages;

/**
 *
 */
public class AlterResolver extends CommandResolver {

	/**
     * @param queryResolver
     */
    public AlterResolver(QueryResolver queryResolver) {
        super(queryResolver);
    }

    @Override
	public void resolveCommand(Command command, TempMetadataAdapter metadata,
			boolean resolveNullLiterals) throws Exception {
		Alter<? extends Command> alter = (Alter<? extends Command>)command;
		ResolverUtil.resolveGroup(alter.getTarget(), metadata);
		int type = ICommand.TYPE_QUERY;
		boolean viewTarget = true;
		if (alter instanceof AlterTrigger) {
			TriggerEvent event = ((AlterTrigger)alter).getEvent();
			switch (event) {
			case DELETE:
				type = ICommand.TYPE_DELETE;
				break;
			case INSERT:
				type = ICommand.TYPE_INSERT;
				break;
			case UPDATE:
				type = ICommand.TYPE_UPDATE;
				break;
			}
		} else if (alter instanceof AlterProcedure) {
			type = ICommand.TYPE_STORED_PROCEDURE;
			viewTarget = false;
		}
		if (viewTarget && !getQueryResolver().isView(alter.getTarget(), metadata)) {
			 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30116, alter.getTarget()));
		}
		if (alter.getDefinition() != null) {
			getQueryResolver().resolveCommand(alter.getDefinition(), alter.getTarget(), type, metadata.getDesignTimeMetadata(), false);
		}
	}

}
