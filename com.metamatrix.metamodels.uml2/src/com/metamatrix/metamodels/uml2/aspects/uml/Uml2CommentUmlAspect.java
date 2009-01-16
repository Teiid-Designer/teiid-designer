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

package com.metamatrix.metamodels.uml2.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Comment;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlComment;

/**
 * Comment Aspect
 */
public class Uml2CommentUmlAspect extends AbstractUml2UmlAspect implements UmlComment {

	/**
	 * @param entity
	 */
	public Uml2CommentUmlAspect(MetamodelEntity entity) {
		super();
		setMetamodelEntity(entity);
	}


	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlComment#getText(java.lang.Object)
	 */
	public String getText(Object eObject) {
		final Comment c = assertComment(eObject);
		return c.getBody();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlComment#getOwner(java.lang.Object)
	 */
	public EObject getOwner(Object eObject) {
		final Comment c = assertComment(eObject);
		return c.getOwner();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
	 */
	public String getEditableSignature(Object eObject) {
		return ""; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
	 */
	public String getSignature(Object eObject, int showMask) {
		return ""; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
	 */
	public String getStereotype(Object eObject) {
		return Uml2Plugin.getPluginResourceLocator().getString("_UI_Comment_type"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
	 */
	public IStatus setSignature(Object eObject, String newSignature) {
		throw new UnsupportedOperationException(Uml2Plugin.Util.getString("Uml2CommentUmlAspect.Signature_may_not_be_set_on_a__1",getStereotype(eObject))); //$NON-NLS-1$
	}
	
	protected Comment assertComment(Object eObject) {
		ArgCheck.isInstanceOf(Comment.class, eObject);
		return (Comment) eObject;
	}

}
