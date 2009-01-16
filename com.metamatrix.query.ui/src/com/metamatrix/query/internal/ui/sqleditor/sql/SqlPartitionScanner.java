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

package com.metamatrix.query.internal.ui.sqleditor.sql;
/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * The SQLPartitionScanner is a RulesBasedPartitionScanner.  The SQL document 
 * partitions are computed dynamically as events signal that the document has 
 * changed. The document partitions are based on tokens that represent comments
 * and SQL code sections.
 */
public class SqlPartitionScanner extends RuleBasedPartitionScanner {
	public final static String SQL_COMMENT= "sql_comment"; //$NON-NLS-1$
	public final static String SQL_MULTILINE_COMMENT= "sql_multiline_comment"; //$NON-NLS-1$
	public final static String SQL_CODE= "sql_code"; //$NON-NLS-1$

	/**
	 * Constructor for SQLPartitionScanner. Creates rules to parse comment 
	 * partitions in an SQL document. In the constructor, is defined the entire 
	 * set of rules used to parse the SQL document, in an instance of an 
	 * IPredicateRule. The coonstructor calls setPredicateRules method which
	 * associates the rules to the scanner and makes the document ready for 
	 * parsing.
	 */
	public SqlPartitionScanner() {
		super();
		IToken sqlCode= new Token(SQL_CODE);
		IToken comment= new Token(SQL_MULTILINE_COMMENT);
		// Syntax higlight

		List rules= new ArrayList();


		// Add rules for multi-line comments 
		rules.add(new MultiLineRule("/*", "*/", comment)); //$NON-NLS-1$ //$NON-NLS-2$
		// Add rules for sql doc.
		rules.add(new EndOfLineRule("///", sqlCode)); //$NON-NLS-1$
		// Add rule for single line comments.
		rules.add(new EndOfLineRule("--", Token.UNDEFINED)); //$NON-NLS-1$
		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", Token.UNDEFINED, '\\')); //$NON-NLS-1$ //$NON-NLS-2$ 
		rules.add(new SingleLineRule("'", "'", Token.UNDEFINED, '\\'));  //$NON-NLS-1$ //$NON-NLS-2$ 
		

		IPredicateRule[] result= new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
		
	}

}
