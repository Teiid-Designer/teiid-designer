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
 
package net.sourceforge.sqlexplorer.dbviewer.actions.editdialog;

import org.eclipse.jface.viewers.ICellModifier;


public class TableCellModifier implements ICellModifier {

	EditorDialog editDialog;
	public TableCellModifier(EditorDialog editDialog){
		this.editDialog=editDialog;
	}
	public boolean canModify(Object element, String property)
	{
		return editDialog.canModify(element,property);
	}

	public Object getValue(Object element, String property)
	{
		return editDialog.getValue(element,property);
	}

	public void modify(Object element, String property, Object value)
	{
		editDialog.modify(element,property,value);
	}
}
