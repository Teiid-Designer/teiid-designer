/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.choice;

import java.util.List;
import org.eclipse.emf.ecore.EObject;

/**
 * IChoiceObject
 */
public interface IChoiceObject {

    List getOrderedOptions();

    void setOrderedOptions( List lst );

    String getName( Object option ); // NO_UCD

    /**
     * @return The UUID form of the criteria.
     */
    String getCriteria( Object option );

    /**
     * Sets the criteria to the specified string, which is in a UUID form.
     */
    void setCriteria( Object option,
                      String criteria );

    /**
     * @return The standard SQL form of the criteria.
     */
    String getSqlCriteria( Object option );

    /**
     * Sets the criteria to the specified string, which is in a standard SQL form.
     */
    void setSqlCriteria( Object option,
                         String criteria );

    boolean isIncluded( Object option );

    void setIncluded( Object option,
                      boolean b );

    void move( int iNewPosition,
               Object option );

    void move( int iNewPosition,
               int iOldPosition );

    int getMinOccurs(); // NO_UCD

    String getDefaultErrorMode();

    void setDefaultErrorMode( String sValue );

    String[] getValidErrorModeValues();

    Object getDefaultOption();

    void setDefaultOption( Object option );

    EObject getRoot();

    EObject getChoice();

    EObject getParent();

}
