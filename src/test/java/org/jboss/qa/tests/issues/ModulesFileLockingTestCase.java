package org.jboss.qa.tests.issues;

import org.junit.Test;

import java.util.Arrays;

/**
 * Tests for {install-dir}/modules not being deleted.
 *
 * Testing scenario where you first start embedded server (to do whatever), then stop it and eventually try to delete
 * the defined files/directories. This can fail on Windows because filehandle is kept open even after embedded
 * server and CLI terminates.
 */
public class ModulesFileLockingTestCase extends FileLockingTestBase
{

   /**
    * Try to delete modules directory in server installation dir after embedded server is stopped.
    */
   @Test
   public void canRemoveModulesAfterEmbeddedServerStarted() throws Exception
   {
      performDeleteTest(Arrays.asList("modules"), false);
   }

}
