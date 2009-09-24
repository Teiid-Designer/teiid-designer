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

import net.sourceforge.sqlexplorer.gef.model.Link;
import net.sourceforge.sqlexplorer.gef.model.Note;
import net.sourceforge.sqlexplorer.gef.model.Schema;
import net.sourceforge.sqlexplorer.gef.model.Table;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * @author Mazzolini
 *
 */
public class SchemaPartFactory implements EditPartFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		Object obj1 = null;
		if(model instanceof Schema)
			obj1 = new SchemaEditPart();
		else if (model instanceof Table)
			obj1=new TableEditPart();
		else if (model instanceof Link)
			obj1=new LinkPart();
		else if (model instanceof Note)
			obj1=new NotePart();
		((EditPart) (obj1)).setModel(model);
		return ((EditPart) (obj1));

	}

}
