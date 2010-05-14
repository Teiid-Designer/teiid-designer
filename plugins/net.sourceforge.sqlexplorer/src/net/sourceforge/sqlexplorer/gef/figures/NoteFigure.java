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
package net.sourceforge.sqlexplorer.gef.figures;

import net.sourceforge.sqlexplorer.gef.model.Note;

import org.eclipse.draw2d.ColorConstants;

import org.eclipse.draw2d.LineBorder;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;

/**
 * @author MAZZOLINI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NoteFigure extends FlowPage {
	Note note;
	TextFlow tf;
	/**
	 * @param note
	 */
	public NoteFigure(Note note) {
		this.note=note;
		
		tf=new TextFlow();
		tf.setText(" ");//$NON-NLS-1$
		//tf.setBackgroundColor(ColorConstants.yellow);
		add(tf);
				
		setOpaque(true);
		setBackgroundColor(ColorConstants.yellow);
		setBorder(new LineBorder());
	}
	public void setText(String txt){
		tf.setText(txt);
	}
	@Override
    public Insets getInsets(){
		return new Insets(5,5,5,5);
	}

}
