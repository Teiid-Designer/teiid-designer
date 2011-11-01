/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;



/**
 * 
 */
public interface ExtendableMetaclassNameProvider {

    public String[] getExtendableMetaclassRoots();

    public String[] getExtendableMetaclassChildren( String metaclassName );

    public boolean hasChildren( String metaclassName );

    public String getParent( String metaclassName );

    public String getLabelText( String metaclassName );

    public String getMetamodelUri();

}
