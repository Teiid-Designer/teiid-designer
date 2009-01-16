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

import java.util.ArrayList;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.gef.model.Schema;
import net.sourceforge.sqlexplorer.gef.model.Table;
import net.sourceforge.sqlexplorer.gef.wizards.TableAdapter;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;


public class AddTablesToSchemaCommand extends Command {
	Schema schema; 
	ArrayList tableList=new ArrayList();
	/**
	 * @param schema
	 * @param tableAdapterList
	 */
	public AddTablesToSchemaCommand(Schema schema, ArrayList tableAdapterList) {
		this.schema=schema; 
		
		for(int i=0;i<tableAdapterList.size();i++){
			TableAdapter tadapt=(TableAdapter) tableAdapterList.get(i);
			Table tb=tadapt.adapt();
			tb.setLocation(new Point(10+i*40,10+i*40));
			tb.setSize(new Dimension(-1,-1));
			tableList.add(tb);
		}		
		
	}


	

	/**
	 * @param label
	 */
	public AddTablesToSchemaCommand(String label) {
		super(label);
	}
	@Override
    public void redo()
	{
		execute();
	}
	@Override
    public void execute() {
		//System.out.println("AddTablesToSchemaCommand.execute");
		for(int i=0;i<tableList.size();i++){
			Table tb=(Table) tableList.get(i);
			tb.createLinks(schema);
			schema.addChild(tb);
		}	
	}
	@Override
    public void undo()
	{
		for(int i=0;i<tableList.size();i++){
			Table tb=(Table) tableList.get(i);
			tb.removeLinks(schema);
			schema.removeChild((Table) tableList.get(i));
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#getLabel()
	 */
	@Override
    public String getLabel() {
		
		return Messages.getString("AddTablesToSchemaCommand.Add_tables_to_current_schema_visualizer_1"); //$NON-NLS-1$
	}

}
