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

package org.teiid.query.validator;

import java.util.Iterator;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.validator.IValidator;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.metadata.TempMetadataStore;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.navigator.PreOrderNavigator;
import org.teiid.query.sql.visitor.CommandCollectorVisitor;


public class Validator implements IValidator<LanguageObject> {

    @Override
    public ValidatorReport validate(LanguageObject object, IQueryMetadataInterface metadata) throws Exception {
        ValidatorReport report1 = validate(object, metadata, new ValidationVisitor(object.getTeiidVersion()));
        return report1;
    }

    public static final ValidatorReport validate(LanguageObject object, IQueryMetadataInterface metadata, AbstractValidationVisitor visitor)
        throws Exception {

        // Execute on this command
        executeValidation(object, metadata, visitor);

        // Construct combined runtime / query metadata if necessary
        if(object instanceof Command) {                        
            // Recursively validate subcommands
            Iterator<Command> iter = CommandCollectorVisitor.getCommands((Command)object, true).iterator();
            while(iter.hasNext()) {
                Command subCommand = iter.next();
                validate(subCommand, metadata, visitor);
            }
        }
        
        // Otherwise, return a report
        return visitor.getReport();
    }

    private static final void executeValidation(LanguageObject object, final IQueryMetadataInterface metadata, final AbstractValidationVisitor visitor) 
        throws Exception {

        // Reset visitor
        visitor.reset();

		visitor.setMetadata(metadata);
        setTempMetadata(metadata, visitor, object);
        
        PreOrderNavigator nav = new PreOrderNavigator(visitor) {
        	
        	@Override
            protected void visitNode(LanguageObject obj) {
        		IQueryMetadataInterface previous = visitor.getMetadata();
        		setTempMetadata(metadata, visitor, obj);
        		super.visitNode(obj);
        		visitor.setMetadata(previous);
        	}
        	
        	@Override
        	protected void preVisitVisitor(LanguageObject obj) {
        		super.preVisitVisitor(obj);
        		visitor.stack.add(obj);
        	}
        	
        	@Override
        	protected void postVisitVisitor(LanguageObject obj) {
        		visitor.stack.pop();
        	}
        	
        };
        object.acceptVisitor(nav);        	
        
        // If an error occurred, throw an exception
        Exception e = visitor.getException();
        if(e != null) { 
            throw e;
        }                
    }
    
	private static void setTempMetadata(final IQueryMetadataInterface metadata,
			final AbstractValidationVisitor visitor,
			LanguageObject obj) {
		if (obj instanceof Command) {
			Command command = (Command)obj;
			visitor.currentCommand = command;
			TempMetadataStore tempMetadata = command.getTemporaryMetadata();
            if(tempMetadata != null && !tempMetadata.getData().isEmpty()) {
            	visitor.setMetadata(new TempMetadataAdapter(metadata, tempMetadata));
            }    
		}
	}
    
}    
