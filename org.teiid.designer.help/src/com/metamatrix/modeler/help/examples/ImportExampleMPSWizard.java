/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
