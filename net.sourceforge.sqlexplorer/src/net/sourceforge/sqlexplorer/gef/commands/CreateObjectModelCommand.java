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
package net.sourceforge.sqlexplorer.gef.commands;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.gef.model.AbstractModelObject;
import net.sourceforge.sqlexplorer.gef.model.Schema;
import net.sourceforge.sqlexplorer.gef.model.Table;



import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

public class CreateObjectModelCommand extends Command {
	public Schema getSchema()
	{
		return schema;
	}

	@Override
    public void redo()
	{
		execute();
	}

	public void setLocation(Rectangle rectangle)
	{
		rect = rectangle;
	}

	public void setSchema(Schema schema)
	{
		this.schema = schema;
	}

	

	public AbstractModelObject getNamedObject()
	{
		return obj;
	}

	public void setNamedObject(AbstractModelObject table)
	{
		this.obj = table;
	}

	private AbstractModelObject obj;
	private Rectangle rect;
	private Schema schema;
	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
    public void execute() {
		Point point = new Point(rect.x, rect.y);
		obj.setLocation(point);
		if(rect.isEmpty()){
		}
		Dimension dimension = new Dimension(rect.width, rect.height);
		obj.setSize(dimension);
				
		if(obj instanceof Table){
			Table target=(Table) obj;
			target.createLinks(schema);
		}
		schema.addChild(obj);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
    public void undo() {
		schema.removeChild(obj);
		if(obj instanceof Table){
			Table target=(Table) obj;
			target.removeLinks(schema);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#getLabel()
	 */
	@Override
    public String getLabel() {
		
		return Messages.getString("CreateObjectModelCommand.Create_object__1")+obj; //$NON-NLS-1$
	}

}
