/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.actions.AdvisorActionFactory;

public class ImportSalesforceCommandHandler extends AbstractHandler {

   public Object execute( ExecutionEvent event ) throws ExecutionException {

	   AdvisorActionFactory.executeAction(AdvisorUiConstants.COMMAND_IDS.IMPORT_SALESFORCE);

       return null;
   }

}