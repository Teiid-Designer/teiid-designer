package net.sourceforge.sqlexplorer.dbviewer.details;


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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
 
 
class ColumnDetailLabelProvider
	extends LabelProvider
	implements ITableLabelProvider {
		
	ColumnDetailTableModel cdtm;
	public ColumnDetailLabelProvider(ColumnDetailTableModel cdtm){
		this.cdtm=cdtm;
	}
	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(Object, int)
	 */
	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Object obj=cdtm.getValue(element,columnIndex);
		if(obj!=null)
			return obj.toString();
        return ""; //$NON-NLS-1$
	}

}
