/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.aspects.validation.rules;

import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.jdom.Verifier;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.xml.XmlDocumentPlugin;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * AccessPatternColumnsRule
 */
public class XmlNamespaceRule implements ObjectValidationRule {
    final static String XML_NS = "http://www.w3.org/XML/1998/namespace"; //$NON-NLS-1$
    final static String XML = "xml"; //$NON-NLS-1$
    
    //These are the return message from the org.jdom.Verifier class
    final static String NUMBER_ERR = "Namespace prefixes cannot begin with a number"; //$NON-NLS-1$
    final static String DOLLAR_ERR = "Namespace prefixes cannot begin with a dollar sign ($)"; //$NON-NLS-1$
    final static String HYPHEN_ERR = "Namespace prefixes cannot begin with a hyphen (-)"; //$NON-NLS-1$
    final static String PERIOD_ERR = "Namespace prefixes cannot begin with a period (.)"; //$NON-NLS-1$
    final static String COLON_ERR = "Namespace prefixes cannot contain colons"; //$NON-NLS-1$
    final static String INVALID_CHAR_ERR = "Namespace prefixes cannot contain the character \""; //$NON-NLS-1$
    final static String XML_ERR = "Namespace prefixes cannot begin with \"xml\" in any combination of case"; //$NON-NLS-1$
    

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(EObject eObject, ValidationContext context) {
        ArgCheck.isInstanceOf(XmlNamespace.class, eObject);

        final XmlNamespace ns = (XmlNamespace) eObject;
        final String uri = ns.getUri();
        final ValidationResult result = new ValidationResultImpl(eObject);
        
        //validate the prefix
        validatePrefix(ns, result);
        
        //validate the uri 
        validateUri(uri, result);
        
        //validete that the element has only one target namespace
        validateElement(ns, result);

        // Add the results to the context (do this last, because not added to context if there are no results)
        context.addResult(result);
    }

    /**
     * @param element
     * @param context
     */
    private void validateElement(final XmlNamespace ns, final ValidationResult result) {
        final XmlElement element = ns.getElement();
        if(element == null){
            //Element attribute may not be null
            final String msg = XmlDocumentPlugin.Util.getString("XmlNamespaceRule.Namespace_Element_attribute_may_not_be_null_1"); //$NON-NLS-1$
            final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            result.addProblem(problem);
            return;
        }
        
        // Find the prefix for this namespace ...
        final String nsPrefix = ns.getPrefix();
        if(nsPrefix == null || nsPrefix.trim().length() == 0 ) {
            // This is a default namespace declaration, so see whether there are other globals ...
            final Iterator namespaces = element.getDeclaredNamespaces().iterator();
            int numDefaultNamespaceDeclarations = 0;
            while(namespaces.hasNext() ){
                XmlNamespace next = (XmlNamespace)namespaces.next();
                final String prefix = next.getPrefix();
                if(prefix == null || prefix.trim().length() == 0 ){
                    ++numDefaultNamespaceDeclarations;
                }
            }
            
            if(numDefaultNamespaceDeclarations > 1){
                // There were more than one, so mark this one with the problem; the others will be marked
                // when they are found (again)
                final String msg = XmlDocumentPlugin.Util.getString("XmlNamespaceRule.Namespace_Element_may_have_only_no_more_than_one_Namespace_Declaration_with_no_Prefix_(Target_Namespace)_2"); //$NON-NLS-1$
                final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                result.addProblem(problem);
            }
        
        }
        
    }

    /**
     * @param uri
     * @param context
     */
    private void validateUri(String uri, final ValidationResult result) {
        //The uri may not be null or zero length
        if(uri == null || uri.trim().length() == 0){
            final String msg = XmlDocumentPlugin.Util.getString("XmlNamespaceRule.Uri_may_not_be_null_or_zero_length_1"); //$NON-NLS-1$
            final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            result.addProblem(problem);
        }
    }

    /**
     * @param prefix
     */
    private void validatePrefix(final XmlNamespace ns, final ValidationResult result) {
        //1. The first character of a XML namespace declaration's prefix, if specified, must be from 
        //   the following set: [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D]
        //   | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] 
        //   | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]. (From http://www.w3.org/TR/xml-names11 ) 
        //2. The non-first characters of a XML namespace declaration's prefix, if specified, must be from the 
        //   following set: [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] 
        //   | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] 
        //   | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF] | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] 
        //   | [#x203F-#x2040]. (From http://www.w3.org/TR/xml-names11 ) 
        //3. Prefix may not begin with xml (case insensitive) 
        //4. The "xml" prefix may only be assigned to the uri "http://www.w3.org/XML/1998/namespace"
        final String prefix = ns.getPrefix();
         
        if(prefix == null){
            return;
        }
       
        //1-4 : Use the org.jdom Verifier class to validate the prefix characters
        final String errMsg = convertPrefixProblemString(Verifier.checkNamespacePrefix(prefix), ns);
        if(errMsg != null){
            final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, errMsg);
            result.addProblem(problem);
        }   
    }

    /**
     * @param errMsg
     */
    private String convertPrefixProblemString(final String errMsg, final XmlNamespace ns) {
        if(errMsg == null){
            return null;
        }

        String msg = null;
        if(errMsg.equals(NUMBER_ERR) ){
            msg = XmlDocumentPlugin.Util.getString("XmlNamespaceRule.Namespace_prefixes_cannot_begin_with_a_number_3"); //$NON-NLS-1$
        }else if(errMsg.equals(COLON_ERR) ){
            msg = XmlDocumentPlugin.Util.getString("XmlNamespaceRule.Namespace_prefixes_cannot_contain_colons_4"); //$NON-NLS-1$
        }else if(errMsg.equals(DOLLAR_ERR) ){
            msg = XmlDocumentPlugin.Util.getString("XmlNamespaceRule.Namespace_prefixes_cannot_begin_with_a_dollar_sign_($)_5"); //$NON-NLS-1$
        }else if(errMsg.equals(HYPHEN_ERR) ){
            msg = XmlDocumentPlugin.Util.getString("XmlNamespaceRule.Namespace_prefixes_cannot_begin_with_a_hyphen_(-)_6"); //$NON-NLS-1$
        }else if(errMsg.equals(PERIOD_ERR) ){
            msg = XmlDocumentPlugin.Util.getString("XmlNamespaceRule.Namespace_prefixes_cannot_begin_with_a_period_(.)_7"); //$NON-NLS-1$
        }else if(errMsg.startsWith(INVALID_CHAR_ERR) ){
            final String part1 = XmlDocumentPlugin.Util.getString("XmlNamespaceRule.Namespace_prefixes_cannot_contain_the_character___8"); //$NON-NLS-1$
            final String invalidChar = errMsg.substring(49,50);
            msg = part1 + invalidChar + "\""; //$NON-NLS-1$
        }else if(errMsg.equals(XML_ERR) ){
            if(XML_NS.equals(ns.getUri() ) && XML.equals(ns.getPrefix() ) ){
                //The ns may be xml as long as the uri = "http://www.w3.org/XML/1998/namespace"
                return null;
            }
            msg = XmlDocumentPlugin.Util.getString("XmlNamespaceRule.Namespace_prefixes_cannot_begin_with___xml___in_any_combination_of_case_9"); //$NON-NLS-1$
        }
        
        return msg;     
    }

}
