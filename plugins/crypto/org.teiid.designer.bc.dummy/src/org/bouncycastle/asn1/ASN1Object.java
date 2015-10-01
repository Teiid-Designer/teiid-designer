/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.bouncycastle.asn1;

import org.apache.commons.codec.binary.Hex;

/**
 *
 */
public class ASN1Object {

    private byte[] content;

    /**
     * @param content
     */
    public ASN1Object(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        if (content == null)
            return new String();

        return new String(Hex.encodeHex(content)); 
    }
}
