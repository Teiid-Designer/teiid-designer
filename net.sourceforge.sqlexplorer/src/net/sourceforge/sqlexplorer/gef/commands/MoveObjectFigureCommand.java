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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;


public class MoveObjectFigureCommand extends Command {
	
	public MoveObjectFigureCommand(){
		
		
		
		
	}
	private AbstractModelObject table;
	private Point location;

	private Dimension dimension;
	private Point old_location;
	private Dimension old_dimension;

	public void setModelObject(AbstractModelObject table)
	{
		this.table = table;
	}
	public void setSize(Dimension dim)
   {
	   dimension = dim;
	//System.out.println("Movetable setSize "+current+":" +dimension);
   }
	
		public void setLocation(Rectangle rectangle)
		{
			setLocation(new Point(rectangle.x, rectangle.y));
			setSize(new Dimension(rectangle.width, rectangle.height));
		}

		private void setLocation(Point point)
		{
			
			if(point!=null)
				location = new Point(point);
		}




	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#getLabel()
	 */
	@Override
    public String getLabel() {
		
		return Messages.getString("MoveObjectFigureCommand.Move__1")+table.getName(); //$NON-NLS-1$
	}
	
	private Point getLocation(){
		return location;
	}
	
	@Override
    public void execute()
	{
		old_location=table.getLocation();
		old_dimension=table.getSize();
		if(dimension!=null)
			table.setSize(dimension);
		if(location!=null)
			table.setLocation(getLocation());
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
    public void redo() {
		execute();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
    public void undo() {
		if(old_location!=null){
			table.setLocation(old_location);
		}
		if(old_dimension!=null){
			table.setSize(old_dimension);
		}
	}

}
