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

package com.metamatrix.modeler.internal.xml.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.xsd.XSDElementDeclaration;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.wizards.AbstractNewModelContributorWizard;
import com.metamatrix.modeler.internal.xml.ui.wizards.VirtualDocumentWizardContributor;
import com.metamatrix.modeler.ui.wizards.INewModelWizardContributor;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiPlugin;


/** 
 * @since 5.0.2
 */
public class ViewXmlMessageStructureWizard extends AbstractNewModelContributorWizard {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private VirtualDocumentWizardContributor contributor;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public ViewXmlMessageStructureWizard(ModelResource theModelResource,
                                         ISelection theSchemaFileSelection) {
        super(ModelerXmlUiPlugin.getDefault(),
              ModelerXmlUiConstants.Util.getString(I18nUtil.getPropertyPrefix(ViewXmlMessageStructureWizard.class) + "title"), //$NON-NLS-1$
              null,
              theModelResource,
              theSchemaFileSelection);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private VirtualDocumentWizardContributor accessContributor() {
        return (VirtualDocumentWizardContributor)getContributor();
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.ui.wizards.AbstractNewModelContributorWizard#getContributor()
     * @since 5.0.2
     */
    @Override
    protected INewModelWizardContributor getContributor() {
        if (this.contributor == null) {
            this.contributor = new VirtualDocumentWizardContributor();
        }
        
        return this.contributor;
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.ui.wizards.AbstractNewModelContributorWizard#getMetamodelDescriptor()
     * @since 5.0.2
     */
    @Override
    protected MetamodelDescriptor getMetamodelDescriptor() {
        return null;
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.ui.wizards.AbstractNewModelContributorWizard#getSelection()
     * @since 5.0.2
     */
    @Override
    protected ISelection getSelection() {
        if (this.selection == null) {
            this.selection = new StructuredSelection(getFile());
        }
        
        return super.getSelection();
    }

    /** 
     * @see com.metamatrix.modeler.internal.ui.wizards.AbstractNewModelContributorWizard#isVirtual()
     * @since 5.0.2
     */
    @Override
    protected boolean isVirtual() {
        return true;
    }

    /**
     * Sets the document roots for the new model. 
     * @param theRoots the XSD root elements
     * @since 5.0.2
     */
    public void setDocumentRoots(XSDElementDeclaration[] theRoots) {
        accessContributor().setDocumentRoots(theRoots);
    }
    
}
