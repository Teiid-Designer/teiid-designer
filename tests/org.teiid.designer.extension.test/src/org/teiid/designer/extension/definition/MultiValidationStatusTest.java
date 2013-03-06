/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.definition;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import org.junit.Test;

/**
 * A test class for {@link MultiValidationStatus}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class MultiValidationStatusTest {

    private static final MedStatus ERROR = ValidationStatus.createErrorMessage("error");
    private static final MedStatus INFO = ValidationStatus.createInfoMessage("info");
    private static final MedStatus OK = ValidationStatus.createOkMessage("ok");
    private static final MedStatus WARNING = ValidationStatus.createWarningMessage("warning");

    @Test
    public void shouldAddMultiStatus() {
        final MultiValidationStatus multi = MultiValidationStatus.create(ERROR);
        multi.add(WARNING);

        final MultiValidationStatus status = MultiValidationStatus.create(WARNING);
        status.add(INFO);
        status.add(multi);

        assertThat(status.isError(), is(true));
        assertThat(status.getChildren().size(), is(4));
        assertThat(status.getMessage(), is(ERROR.getMessage()));
    }

    @Test
    public void shouldCreatErrorStatus() {
        final MultiValidationStatus status = MultiValidationStatus.create(ERROR);
        assertThat(status.isError(), is(true));
        assertThat(status.getChildren().size(), is(1));
        assertThat(status.isMulti(), is(true));
    }

    @Test
    public void shouldCreatInfoStatus() {
        final MultiValidationStatus status = MultiValidationStatus.create(INFO);
        assertThat(status.isInfo(), is(true));
        assertThat(status.getChildren().size(), is(1));
        assertThat(status.isMulti(), is(true));
    }

    @Test
    public void shouldCreatOkStatus() {
        final MultiValidationStatus status = MultiValidationStatus.create(OK);
        assertThat(status.isOk(), is(true));
        assertThat(status.getChildren().isEmpty(), is(true));
        assertThat(status.isMulti(), is(true));
    }

    @Test
    public void shouldCreatWarningStatus() {
        final MultiValidationStatus status = MultiValidationStatus.create(WARNING);
        assertThat(status.isWarning(), is(true));
        assertThat(status.getChildren().size(), is(1));
        assertThat(status.isMulti(), is(true));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullAtConstruction() {
        MultiValidationStatus.create(null);
    }

    @Test
    public void shouldNotUpdateStatusFromErrorToInfo() {
        final MultiValidationStatus status = MultiValidationStatus.create(ERROR);
        status.add(INFO);
        assertThat(status.isError(), is(true));
        assertThat(status.getChildren().size(), is(2));
        assertThat(status.getMessage(), is(ERROR.getMessage()));
    }

    @Test
    public void shouldNotUpdateStatusFromErrorToOk() {
        final MultiValidationStatus status = MultiValidationStatus.create(ERROR);
        status.add(OK);
        assertThat(status.isError(), is(true));
        assertThat(status.getChildren().size(), is(1));
        assertThat(status.getMessage(), is(ERROR.getMessage()));
    }

    @Test
    public void shouldNotUpdateStatusFromInfoToOk() {
        final MultiValidationStatus status = MultiValidationStatus.create(INFO);
        status.add(OK);
        assertThat(status.isInfo(), is(true));
        assertThat(status.getChildren().size(), is(1));
        assertThat(status.getMessage(), is(INFO.getMessage()));
    }

    @Test
    public void shouldNotUpdateStatusFromWarningToInfo() {
        final MultiValidationStatus status = MultiValidationStatus.create(WARNING);
        status.add(INFO);
        assertThat(status.isWarning(), is(true));
        assertThat(status.getChildren().size(), is(2));
        assertThat(status.getMessage(), is(WARNING.getMessage()));
    }

    @Test
    public void shouldNotUpdateStatusFromWarningToOk() {
        final MultiValidationStatus status = MultiValidationStatus.create(WARNING);
        status.add(OK);
        assertThat(status.isWarning(), is(true));
        assertThat(status.getChildren().size(), is(1));
        assertThat(status.getMessage(), is(WARNING.getMessage()));
    }

    @Test
    public void shouldUpdateStatusFromErrorToWarning() {
        final MultiValidationStatus status = MultiValidationStatus.create(ERROR);
        status.add(WARNING);
        assertThat(status.isError(), is(true));
        assertThat(status.getChildren().size(), is(2));
        assertThat(status.getMessage(), is(ERROR.getMessage()));
    }

    @Test
    public void shouldUpdateStatusFromInfoToError() {
        final MultiValidationStatus status = MultiValidationStatus.create(INFO);
        status.add(ERROR);
        assertThat(status.isError(), is(true));
        assertThat(status.getChildren().size(), is(2));
        assertThat(status.getMessage(), is(ERROR.getMessage()));
    }

    @Test
    public void shouldUpdateStatusFromInfoToWarning() {
        final MultiValidationStatus status = MultiValidationStatus.create(INFO);
        status.add(WARNING);
        assertThat(status.isWarning(), is(true));
        assertThat(status.getChildren().size(), is(2));
        assertThat(status.getMessage(), is(WARNING.getMessage()));
    }

    @Test
    public void shouldUpdateStatusFromOkToError() {
        final MultiValidationStatus status = MultiValidationStatus.create(OK);
        status.add(ERROR);
        assertThat(status.isError(), is(true));
        assertThat(status.getChildren().size(), is(1));
        assertThat(status.getMessage(), is(ERROR.getMessage()));
    }

    @Test
    public void shouldUpdateStatusFromOkToInfo() {
        final MultiValidationStatus status = MultiValidationStatus.create(OK);
        status.add(INFO);
        assertThat(status.isInfo(), is(true));
        assertThat(status.getChildren().size(), is(1));
        assertThat(status.getMessage(), is(INFO.getMessage()));
    }

    @Test
    public void shouldUpdateStatusFromOkToWarning() {
        final MultiValidationStatus status = MultiValidationStatus.create(OK);
        status.add(WARNING);
        assertThat(status.isWarning(), is(true));
        assertThat(status.getChildren().size(), is(1));
        assertThat(status.getMessage(), is(WARNING.getMessage()));
    }

    @Test
    public void shouldUpdateStatusFromWarningToError() {
        final MultiValidationStatus status = MultiValidationStatus.create(WARNING);
        status.add(ERROR);
        assertThat(status.isError(), is(true));
        assertThat(status.getChildren().size(), is(2));
        assertThat(status.getMessage(), is(ERROR.getMessage()));
    }

}
