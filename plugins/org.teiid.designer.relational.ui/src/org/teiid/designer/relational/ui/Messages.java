/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
	public static String elipsisLabel;
	public static String addLabel;
	public static String deleteLabel;
	public static String moveUpLabel;
	public static String moveDownLabel;
    public static String nameLabel;
    public static String includeLabel;
    public static String propertiesLabel;
    public static String changeLabel;
    public static String editLabel;
    public static String fkNameLabel;
    public static String descriptionLabel;
    //public static String xxxxx;
    //public static String xxxxx;
    //public static String xxxxx;

    public static String baseTableActionText;
    public static String createRelationalTableTitle;
    public static String createRelationalTableInitialMessage;
    public static String createRelationalTableExceptionMessage;
    public static String createRelationalTableHelpText;

    public static String modelFileLabel;

    public static String nameInSourceLabel;
    public static String cardinalityLabel;
    public static String materializedLabel;
    public static String tableReferenceLabel;
    public static String supportsUpdateLabel;
    public static String systemTableLabel;
    public static String foreignKeysLabel;
    public static String primaryKeyLabel;
    public static String columnsLabel;
    public static String uniqueConstraintLabel;
    public static String columnNameLabel;
    public static String dataTypeLabel;
    public static String lengthLabel;

	public static String validationOkCreateObject;
	public static String selectColumnsTitle;
	public static String selectColumnsSubTitle;
	public static String selectColumnsMessage;
	public static String createForeignKeyTitle;
	public static String editForeignKeyTitle;
	public static String foreignKeyMultiplicity;
	public static String uniqueKeyMultiplicity;
	public static String selectPrimaryKeyOrUniqueConstraint;
	public static String selectColumnReferencesToFK;
	public static String newForeignKeyMessage;
	public static String cardinalityErrorTitle;
	public static String cardinalityMustBeAnInteger;
    		
    static {
        NLS.initializeMessages("org.teiid.designer.relational.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
