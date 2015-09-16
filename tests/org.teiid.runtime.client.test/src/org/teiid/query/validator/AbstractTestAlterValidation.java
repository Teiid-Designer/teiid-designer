/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.visitor.SQLStringVisitor;

@SuppressWarnings( {"nls", "javadoc"} )
public abstract class AbstractTestAlterValidation extends AbstractTest {

    /**
     * @param teiidVersion
     */
    public AbstractTestAlterValidation(Version teiidVersion) {
        super(teiidVersion);
    }

    private Command helpResolve(String sql, IQueryMetadataInterface metadata) {
        Command command = null;

        try {
            command = getQueryParser().parseCommand(sql);
            QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
            queryResolver.resolveCommand(command, metadata);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return command;
    }

    public ValidatorReport helpValidate(String sql, String[] expectedStringArray, IQueryMetadataInterface metadata) {
        Command command = helpResolve(sql, metadata);

        return helpRunValidator(command, expectedStringArray, metadata);
    }

    public ValidatorReport helpRunValidator(Command command, String[] expectedStringArray, IQueryMetadataInterface metadata) {
        try {
            ValidatorReport report = new Validator().validate(command, metadata);

            examineReport(command, expectedStringArray, report);
            return report;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void examineReport(Object command, String[] expectedStringArray, ValidatorReport report) {
        // Get invalid objects from report
        Collection<LanguageObject> actualObjs = new ArrayList<LanguageObject>();
        report.collectInvalidObjects(actualObjs);

        // Compare expected and actual objects
        Set<String> expectedStrings = new HashSet<String>(Arrays.asList(expectedStringArray));
        Set<String> actualStrings = new HashSet<String>();
        for (LanguageObject obj : actualObjs) {
            actualStrings.add(SQLStringVisitor.getSQLString(obj));
        }

        if (expectedStrings.size() == 0 && actualStrings.size() > 0) {
            fail("Expected no failures but got some: " + report.getFailureMessage()); //$NON-NLS-1$ 
        } else if (actualStrings.size() == 0 && expectedStrings.size() > 0) {
            fail("Expected some failures but got none for sql = " + command); //$NON-NLS-1$
        } else {
            assertEquals("Expected and actual sets of strings are not the same: ", expectedStrings, actualStrings); //$NON-NLS-1$
        }
    }

    @Test
    public void testValidateAlterView() {
        helpValidate("alter view SmallA_2589 as select 2", new String[] {"SELECT 2"}, getMetadataFactory().exampleBQTCached());
        helpValidate("alter view Defect15355 as select 'a', 1",
                     new String[] {"SELECT 'a', 1"},
                     getMetadataFactory().exampleBQTCached());
        helpValidate("alter view Defect15355 as select 'a', cast(1 as biginteger)",
                     new String[] {},
                     getMetadataFactory().exampleBQTCached());

        helpValidate("alter view SmallA_2589 as select * from bqt1.smalla",
                     new String[] {},
                     getMetadataFactory().exampleBQTCached());
    }

    @Test
    public void testValidateAlterViewDeep() {
        helpValidate("alter view Defect15355 as select xpathvalue('a', ':'), cast(1 as biginteger)",
                     new String[] {"xpathvalue('a', ':')"},
                     getMetadataFactory().exampleBQTCached());
    }

    @Test
    public void testValidateAlterTrigger() {
        helpValidate("alter trigger on SmallA_2589 instead of insert as for each row begin atomic select 1; end",
                     new String[] {"SmallA_2589"},
                     getMetadataFactory().exampleBQTCached());
    }
}
