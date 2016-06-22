/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng.api;

import java.util.List;


/**
 * @author vanhalbert
 *
 */
public interface AnnotationType {

	public String getClassAnnotation(Table t);

	public String getAttributeAnnotation(Column c);

	public String getGetterMethodAnnotation(Column c);

	public List<String> getImports();

}