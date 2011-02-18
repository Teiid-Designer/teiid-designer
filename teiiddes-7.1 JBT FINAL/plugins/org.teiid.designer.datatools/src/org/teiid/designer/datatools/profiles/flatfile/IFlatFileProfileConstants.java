package org.teiid.designer.datatools.profiles.flatfile;

public interface IFlatFileProfileConstants {
	// ODA Flat File plugin.properties values
	/*
		plugin.name=Eclipse Data Tools Platform Flat File ODA Runtime Driver
		datasource.name=Flat File Data Source
		dataset.name=Flat File Data Set
		oda.data.source.category.name=Flat File Data Source
		connection.profile.name=ODA Flat File Data Source Connection Profile
		
		KEY STRING
		-----------------	
		HOME       		datasource.property.home=Home &Folder
		DELIMTYPE  		datasource.property.csvdelimitertype=CSV &Type
		CHARSET    		datasource.property.charset=&Character Set
		INCLTYPELINE  	datasource.property.incltypeline=Use Second Line as &Data Type Indicator
		INCLCOLUMNNAME  datasource.property.inclcolumnnameline=Use First Line as Column &Name Indicator
		-----------------
		
		dataset.property.savedcolumnsinfo=The Information about Each Column
		
		property.value.yes=Yes
		property.value.no=No
		property.value.comma=COMMA
		property.value.semicolon=SEMICOLON
		property.value.pipe=PIPE
		property.value.tab=TAB
		#
		#  Below NLS messages apply to all ODA data sources; should be re-factored
		#
		oda.connection.factory.name=ODA Connection Factory
	 */
	
	// Sample Base Properties
	// profile.getBaseProperties()
	// (java.util.Properties) {INCLTYPELINE=YES, INCLCOLUMNNAME=YES, HOME=/home/blafond/TestDesignerFolder/example files/TextTest, DELIMTYPE=COMMA, CHARSET=UTF-8}
	
	
	String HOME_URL = "FlatFileHomeUrl"; //$NON-NLS-1$
	String DELIMETER = "FlatFileDelimeter"; //$NON-NLS-1$
	String CHARSET = "FlatFileCharSet"; //$NON-NLS-1$
	String FIRST_LINE_COLUMN_NAME = "FlatFileFirstLineColumnName"; //$NON-NLS-1$
	String SECOND_LINE_DATATYPE = "FlatFileSecondLineDataType"; //$NON-NLS-1$
	
	String HOME_KEY = "HOME"; //$NON-NLS-1$
	String DELIMTYPE_KEY = "DELIMTYPE"; //$NON-NLS-1$
	String CHARSET_KEY = "CHARSET"; //$NON-NLS-1$
	String INCLTYPELINE_KEY = "INCLTYPELINE"; //$NON-NLS-1$
	String INCLCOLUMNNAME_KEY = "INCLCOLUMNNAME"; //$NON-NLS-1$
	
	String TEIID_PARENT_DIRECTORY_KEY = "ParentDirectory"; //$NON-NLS-1$
}
