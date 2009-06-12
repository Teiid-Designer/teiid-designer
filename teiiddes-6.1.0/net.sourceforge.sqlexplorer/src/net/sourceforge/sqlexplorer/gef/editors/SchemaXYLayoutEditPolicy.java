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
package net.sourceforge.sqlexplorer.gef.editors;

import net.sourceforge.sqlexplorer.gef.commands.CreateObjectModelCommand;
import net.sourceforge.sqlexplorer.gef.commands.MoveObjectFigureCommand;
import net.sourceforge.sqlexplorer.gef.model.AbstractModelObject;
import net.sourceforge.sqlexplorer.gef.model.Note;
import net.sourceforge.sqlexplorer.gef.model.Schema;
import net.sourceforge.sqlexplorer.gef.model.Table;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.*;
import org.eclipse.gef.requests.CreateRequest;

public class SchemaXYLayoutEditPolicy extends XYLayoutEditPolicy
{

    public SchemaXYLayoutEditPolicy()
    {
    }

    @Override
    protected Command createChangeConstraintCommand(EditPart editpart, Object obj)
    {
        MoveObjectFigureCommand movetablefigurecommand = new MoveObjectFigureCommand();
        movetablefigurecommand.setModelObject((AbstractModelObject)editpart.getModel());
        movetablefigurecommand.setLocation((Rectangle)obj);
        return movetablefigurecommand;
    }

    @Override
    protected EditPolicy createChildEditPolicy(EditPart editpart)
    {
        //if(editpart instanceof Table)
         //   return new NonResizableEditPolicy();
       // else
            return new ResizableEditPolicy();
    }

    @Override
    protected Command getCreateCommand(CreateRequest createRequest)
    {
    	Object objectType=createRequest.getNewObjectType();
		if(objectType == Table.class ||objectType == Note.class ){
			CreateObjectModelCommand createObjModelCommand = new CreateObjectModelCommand();
			createObjModelCommand.setSchema((Schema)getHost().getModel());
			AbstractModelObject tb=(AbstractModelObject)createRequest.getNewObject();
			createObjModelCommand.setNamedObject(tb);
			Rectangle rectangle = (Rectangle)getConstraintFor(createRequest);
			createObjModelCommand.setLocation(rectangle);
			createObjModelCommand.setLabel("Create object "+tb.getName());//$NON-NLS-1$
			return createObjModelCommand;
		}
		
        return null;

    }

    @Override
    protected Command getDeleteDependantCommand(Request request)
    {
        return null;
    }

    @Override
    protected Command getOrphanChildrenCommand(Request request)
    {
        return null;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	@Override
    protected Command createAddCommand(EditPart child, Object constraint) {
		return null;
	}
}
