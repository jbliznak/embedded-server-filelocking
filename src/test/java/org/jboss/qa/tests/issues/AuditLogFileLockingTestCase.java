package org.jboss.qa.tests.issues;

import org.junit.Test;

import java.util.Arrays;

/**
 * Tests for audit.log not being deleted.
 *
 * Testing scenario where you first start embedded server (to do whatever), then stop it and eventually try to delete
 * the defined files/directories. This can fail on Windows because filehandle is kept open even after embedded
 * server and CLI terminates.
 */
public class AuditLogFileLockingTestCase extends FileLockingTestBase
{

   /**
    * There is kept some reference to audit.log file even after embedded server stopped, this reference is celaned on GC,
    * so to be able to reproduce this we need to ensure we have big enough heap to prevent GC.
    */
   @Test
   public void canRemoveAuditLogAfterEmbeddedServerStarted() throws Exception
   {
      performDeleteTest(Arrays.asList("standalone" + FS + "log", "domain" + FS + "log"), false);
   }

   /**
    * If GC is performed before deletion, handle is closed and audit.log should be deleted without issue.
    */
   @Test
   public void canRemoveAuditLogAfterEmbeddedServerStartedWithGC() throws Exception
   {
      performDeleteTest(Arrays.asList("standalone" + FS + "log", "domain" + FS + "log"), true);
   }

}
