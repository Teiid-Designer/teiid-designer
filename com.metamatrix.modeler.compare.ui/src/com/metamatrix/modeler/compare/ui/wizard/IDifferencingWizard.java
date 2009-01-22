/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.ui.wizard;

import java.util.List;

import org.eclipse.ui.IImportWizard;

import com.metamatrix.modeler.compare.DifferenceReport;


/** 
 * @since 4.3
 */
public interface IDifferencingWizard extends IImportWizard {
    List<DifferenceReport> getDifferenceReports();
}
