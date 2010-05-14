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



import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.gef.model.Column;
import net.sourceforge.sqlexplorer.gef.model.Table;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;


/**
 * @author Mazzolini
 *
 */
public class TableFigure extends Figure {
	static Font tableLabelFont = new Font(null, "Arial", 10, SWT.BOLD);//$NON-NLS-1$
	protected Hashtable connectionAnchors = new Hashtable(7);
	protected Vector inputConnectionAnchors = new Vector(2,2);
	protected Vector outputConnectionAnchors = new Vector(2,2);
	Label label;
	RectangleFigure rectFigure;
	public static CompartmentFigureBorder containerBorder = new CompartmentFigureBorder();
	public static Color classColor = ColorConstants.white;
	ColumnContainerFigure  container;
	Table table;
	public ConnectionAnchor getConnectionAnchor(String terminal) {
		//System.out.println("getConnectionAnchor");
		return (ConnectionAnchor)connectionAnchors.get(terminal);
	}

	public String getConnectionAnchorName(ConnectionAnchor c){
		//System.out.println("getConnectionAnchorName");
		Enumeration enumkeys = connectionAnchors.keys();
		String key;
		while (enumkeys.hasMoreElements()){
			key = (String)enumkeys.nextElement();
			if (connectionAnchors.get(key).equals(c))
				return key;
		}
		return null;
	}


	/**
	 * @param table
	 */
	public TableFigure(Table table) {
		this.table=table;
		this.setToolTip(new Label(table.getQualifiedName()));
		label=new Label(table.getQualifiedName());
		label.setFont(tableLabelFont);
		label.setBorder(getBorder());
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black,1));
		setBackgroundColor(classColor);
		setOpaque(true);

		add(label);
		container=new ColumnContainerFigure(table,table.isShowColumnDetail());
		container.setBorder(containerBorder);

		add(container);
		Column[] columns=table.getColumns();
		container.setColumns(columns);
	}

	public ConnectionAnchor getSourceConnectionAnchorAt(Point p) {
		ConnectionAnchor closest = null;
		long min = Long.MAX_VALUE;

		Enumeration e = getSourceConnectionAnchors().elements();
		while (e.hasMoreElements()) {
			ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
			Point p2 = c.getLocation(null);
			long d = p.getDistance2(p2);
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		return closest;
	}

	/**
	 * @param string
	 */
	public void setLabel(String string) {
		label.setText(string);
	}
	public void setColumnTypeVisible(boolean b) {
		container.setColumnTypeVisible(b);
	}

	public ConnectionAnchor getTargetConnectionAnchorAt(Point p) {
		ConnectionAnchor closest = null;
		long min = Long.MAX_VALUE;

		Enumeration e = getTargetConnectionAnchors().elements();
		while (e.hasMoreElements()) {
			ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
			Point p2 = c.getLocation(null);
			long d = p.getDistance2(p2);
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		return closest;
	}
	public Vector getTargetConnectionAnchors() {
		return inputConnectionAnchors;
	}
	public Vector getSourceConnectionAnchors() {
		return outputConnectionAnchors;
	}

}

class ColumnContainerFigure extends Figure{
	static Font columnLabelFont = new Font(null, "Arial", 8, SWT.BOLD);//$NON-NLS-1$
	static Font columnTypeFont = new Font(null, "Arial", 8, SWT.NORMAL);//$NON-NLS-1$
	static Image keyImage=ImageDescriptor.createFromURL(SqlexplorerImages.getKeyIcon()).createImage();
	static Image nullImage=ImageDescriptor.createFromURL(SqlexplorerImages.getNullIcon()).createImage();
	ColumnFigure left=new ColumnFigure();
	ColumnFigure right=new ColumnFigure();
	Table table;
	public ColumnContainerFigure(Table table,boolean showColumnTypes){
		this.table=table;
		ToolbarLayout layout = new ToolbarLayout(true);
		layout.setSpacing(5);
		setLayoutManager(layout);
		layout.setStretchMinorAxis(false);
		this.add(left);
		left.setFont(columnLabelFont);
		right.setVisible(showColumnTypes);
		add(right);
		right.setFont(columnTypeFont);


	}
	/**
	 * @param b
	 */
	public void setColumnTypeVisible(boolean b) {
		right.setVisible(b);

	}
	public void setColumns(Column[] columns){
		for(int i=0;i<columns.length;i++){
			Label leftLabel=new Label(columns[i].columnName);
			if(table.isPrimaryKey(columns[i]))
				leftLabel.setIcon(keyImage);
			else
				leftLabel.setIcon(nullImage);
			left.add(leftLabel);
			Label rightLabel=new Label(columns[i].typeNameExtended);
			rightLabel.setIcon(nullImage);
			right.add(rightLabel);
		}
	}

}

class ColumnFigure extends Figure{
	public ColumnFigure(){
		ToolbarLayout layout = new ToolbarLayout(false);
		layout.setSpacing(5);
		setLayoutManager(layout);
		layout.setStretchMinorAxis(false);
	}
}


class CompartmentFigureBorder extends AbstractBorder {
	public Insets getInsets(IFigure figure) {
	  return new Insets(8,5,5,5);
	}
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
	  graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(),
						tempRect.getTopRight());
	  graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft().getTranslated(0,5),
								tempRect.getTopRight().getTranslated(0,5));
	}
  }