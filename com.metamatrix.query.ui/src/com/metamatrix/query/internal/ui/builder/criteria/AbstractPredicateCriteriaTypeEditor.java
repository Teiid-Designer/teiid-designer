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

package com.metamatrix.query.internal.ui.builder.criteria;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectEditor;
import com.metamatrix.query.internal.ui.builder.model.AbstractPredicateCriteriaTypeEditorModel;

/**
 * AbstractPredicateCriteriaTypeEditor
 */
public abstract class AbstractPredicateCriteriaTypeEditor extends AbstractLanguageObjectEditor
		implements IPredicateCriteriaTypeEditor {
			
	protected AbstractPredicateCriteriaTypeEditor(Composite theParent, Class theEditorType,
			AbstractPredicateCriteriaTypeEditorModel theModel) {
		super(theParent, theEditorType, theModel);
	}
	
	@Override
    protected final void createUi(Composite parent) {
		// don't all this method to be called by subclasses.
		// the parameter being passed in is ignored because all these editors don't use the parent
		// all subclasses left and right components are added to different containers.
		// SWT must reserve some size to empty Composites. so doing the following reduced the size. 
		GridData gd = new GridData();
		gd.widthHint = 0;
		gd.heightHint = 0;
		parent.setLayoutData(gd);
		parent.setVisible(false);
		
		if (parent.getLayout() instanceof GridLayout) {
			GridLayout layout = (GridLayout)parent.getLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
		}
	}
}
