/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.navigator.model;

import org.eclipse.osgi.util.NLS;

/**
 * 
 */
public class ModelNavigatorMessages extends NLS {

    private static final String BUNDLE_NAME = "org.teiid.designer.ui.navigator.model.messages"; //$NON-NLS-1$

    public static String createRenameActionErrorMessage;
    public static String defaultCopyActionNotFoundMessage;
    public static String dotProjectResourceDropErrorMessage;
    public static String dotProjectResourceDropErrorTitle;
    public static String genericDiagramLabel;
    public static String genericTransformationLabel;
    public static String invalidProjectErrorTitle;
    public static String mixedDropQuestion;
    public static String moveRefactorActionText;
    public static String moveRefactorActionToolTip;
    public static String problemMarkerBrackets;
    public static String refreshActionText;
    public static String refreshActionToolTip;
    public static String renameNotSupportedMessage;
    public static String showImportsActionText;
    public static String showNonModelsActionText;
    public static String sortModelContentsActionText;
    public static String viewToolTip;
    
    static {
        reloadMessages();
    }

    public static void reloadMessages() {
        NLS.initializeMessages(BUNDLE_NAME, ModelNavigatorMessages.class);
    }

    /**
     * Don't allow instances.
     */
    private ModelNavigatorMessages() {
        // nothing to do
    }
}
