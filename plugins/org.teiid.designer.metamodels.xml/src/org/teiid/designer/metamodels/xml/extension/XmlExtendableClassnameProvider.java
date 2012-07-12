/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml.extension;

import org.teiid.designer.core.extension.AbstractMetaclassNameProvider;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;

/**
 * 
 */
public class XmlExtendableClassnameProvider extends AbstractMetaclassNameProvider {

    public XmlExtendableClassnameProvider() {
        super(XmlDocumentPackage.eNS_URI);
    }

}
