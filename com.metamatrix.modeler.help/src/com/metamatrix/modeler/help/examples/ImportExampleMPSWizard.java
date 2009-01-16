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

package com.metamatrix.modeler.help.examples;

import java.io.File;
import org.eclipse.jface.viewers.IStructuredSelection;
import com.metamatrix.modeler.internal.ui.wizards.ImportModelerProjectSetMainPage;
import com.metamatrix.modeler.internal.ui.wizards.ImportModelerProjectSetWizard;

public final class ImportExampleMPSWizard extends ImportModelerProjectSetWizard {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The directory where the example model project sets are located.
     * 
     * @since 6.0.0
     */
    private final File dir;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param dir the directory where the example model project sets are located
     * @since 6.0.0
     */
    public ImportExampleMPSWizard( File dir ) {
        super();
        this.dir = dir;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.wizards.ImportModelerProjectSetWizard#createMainPage(org.eclipse.jface.viewers.IStructuredSelection)
     * @since 6.0.0
     */
    @Override
    protected ImportModelerProjectSetMainPage createMainPage( IStructuredSelection selection ) {
        ImportExampleModelerProjectSetMainPage examPage = new ImportExampleModelerProjectSetMainPage(this.dir);
        return examPage;
    }
}
