/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.bouncycastle.x509.extension;

import org.bouncycastle.asn1.ASN1Object;

/**
 *
 */
public class X509ExtensionUtil {

    public static ASN1Object fromExtensionValue(byte[] content) {
        return new ASN1Object(content);
    }
}
