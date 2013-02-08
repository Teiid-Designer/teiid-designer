/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * A test class of a {@link KomodoCommand}.
 */
@SuppressWarnings( {"javadoc"} )
public class KomodoCommandTest extends ShellCommandTest {

    private class TestCommand extends KomodoCommand<Boolean> {

        TestCommand() {
            super();
        }

        TestCommand(final boolean cancelable) {
            super(cancelable);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.shell.command.KomodoCommand#doExecute(java.lang.String[])
         */
        @Override
        protected Boolean doExecute(final String... args) throws Exception {
            if (isCancelable()) {
                Thread.sleep(1000);
            }

            return RESULT;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.overlord.sramp.shell.api.ShellCommand#printHelp()
         */
        @Override
        public void printHelp() {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         *
         * @see org.overlord.sramp.shell.api.ShellCommand#printUsage()
         */
        @Override
        public void printUsage() {
            // nothing to do
        }

    }

    private static final Boolean RESULT = Boolean.TRUE;

    private KomodoCommand command;

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.command.ShellCommandTest#getCommand()
     */
    @Override
    protected KomodoCommand getCommand() {
        if (this.command == null) {
            this.command = new TestCommand();
        }

        return this.command;
    }

    @Test( expected = InterruptedException.class )
    public void shouldHaveCanceledResultWhenStopped() throws Exception {
        final KomodoCommand cmd = new TestCommand(true);
        cmd.setWaitTime(20L);
        cmd.execute();
        cmd.stop();
        cmd.getResult();
    }

    @Test
    public void shouldHaveDefaultWaitTimeAfterConstruction() {
        assertThat(this.command.getWaitTime(), is(KomodoCommand.DEFAULT_WAIT_TIME));
    }

    @Test
    public void shouldHaveExpectedResultWhenNotStopped() throws Exception {
        final TestCommand cmd = new TestCommand(true);
        cmd.setWaitTime(20L);
        cmd.execute();
        assertThat(cmd.getResult(), is(RESULT));
    }

    @Test
    public void shouldNotBeCancelableUsingNoArgConstructor() {
        assertThat(this.command.isCancelable(), is(false));
    }

    @Test
    public void shouldSetCancelableAtConstruction() {
        final KomodoCommand cmd = new TestCommand(true);
        assertThat(cmd.isCancelable(), is(true));
    }

    @Test
    public void shouldSetWaitTime() {
        final long newWaitTime = (KomodoCommand.DEFAULT_WAIT_TIME - 1);
        this.command.setWaitTime(newWaitTime);
        assertThat(this.command.getWaitTime(), is(newWaitTime));
    }

}
