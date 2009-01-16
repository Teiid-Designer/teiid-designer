/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.diagram.ui.figure;

import org.eclipse.draw2d.IFigure;

/**
 * ContainerFigure
 * 
 * This interface class is designed to provide a generic interface between an container type 
 * edit part and it's content pane.  The ClassifierContainer needs to act like a "canvas" and thus
 * provide it's own content pane to the gef world.
 */
public interface ContainerFigure {
    
    IFigure getContentsPane();

}
