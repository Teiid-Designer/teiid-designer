/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.part;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;


/**
 * This command is used to move or to resize the nodes in  Hello Gef editor.
 * Support for undo/redo is ready but not used in the example.
 */
public class SetConstraintCommand extends Command {

	private Point newPos;
	private Dimension newSize;
	private Point oldPos;
	private Dimension oldSize;
	private NavigationModelNode m_model;
	
	@Override
    public void execute() {
		oldSize = m_model.getSize();
		oldPos = m_model.getPosition();
		m_model.setPosition(newPos);
		m_model.setSize(newSize);
	}

	@Override
    public void redo() {
		m_model.setSize(newSize);
		m_model.setPosition(newPos);
	}

	public void setLocation(Rectangle r) {
		setLocation(r.getLocation());
		setSize(r.getSize());
	 }

	public void setLocation(Point p) {
		newPos = p;
	}

	public void setModel(NavigationModelNode iModel) {
		this.m_model = iModel;
	}

	public void setSize(Dimension p) {
		newSize = p;
	}

	@Override
    public void undo() {
		m_model.setSize(oldSize);
		m_model.setPosition(oldPos);
	}

}
