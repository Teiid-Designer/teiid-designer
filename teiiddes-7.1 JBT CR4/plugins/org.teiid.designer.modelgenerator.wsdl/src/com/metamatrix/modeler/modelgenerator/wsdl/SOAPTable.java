/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl;

import com.metamatrix.modeler.schema.tools.model.jdbc.Table;

interface SOAPTable extends Table {

	public boolean isRequest();

	public String getSoapAction();

	public SoapBindingInfo getSoapBindingInfo();

}
