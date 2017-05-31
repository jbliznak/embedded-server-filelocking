package org.jboss.qa.tests.issues;

import org.apache.commons.io.FileUtils;
import org.jboss.as.cli.scriptsupport.CLI;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FileLockingTestBase
{
   protected static final String FS = File.separator;

   private final File BASE_TARGET_DIR = new File("target" + FS + "test-wildfly");

   private final File WF_HOME = new File(System.getProperty("wildfly.home"));

   private final File HANDLE = new File(System.getProperty("handle.exe"));

   /**
    * @param pathsToTest list of paths relative to Wildfly home dir that will be used for handle and for deletion test
    * @param performGC   whether to perform GC before actual deletion
    */
   protected void performDeleteTest(List<String> pathsToTest, boolean performGC)
         throws IOException, InterruptedException
   {
      //to guarantee different locks
      File TEST_TARGET = new File(BASE_TARGET_DIR.getAbsolutePath() + System.currentTimeMillis());
      FileUtils.deleteDirectory(TEST_TARGET);
      FileUtils.copyDirectory(WF_HOME, TEST_TARGET);

      List<String> handleFilters = pathsToTest
            .stream()
            .map((s -> TEST_TARGET.getName() + FS + s))
            .collect(Collectors.toList());
      List<File> filesToDelete = pathsToTest
            .stream()
            .map((s -> new File(TEST_TARGET + FS + s)))
            .collect(Collectors.toList());

      CLI cli = CLI.newInstance();
      listHandles("Handles after CLI initialized.", handleFilters);

      Assert.assertTrue(cli.cmd("embed-server --jboss-home=" + TEST_TARGET.getAbsolutePath()).isSuccess());
      listHandles("Handles after embedded server started.", handleFilters);

      Assert.assertTrue(cli.cmd("stop-embedded-server").isSuccess());
      listHandles("Handles after embedded server stopped, CLI is still running.", handleFilters);

      cli.terminate();
      Assert.assertTrue(cli.getCommandContext().isTerminated());
      listHandles("Handles after CLI was terminated, none expected here.", handleFilters);

      if (performGC)
      {
         System.gc();
         Thread.sleep(5000); //give GC some time to actually execute gc-cycle
         listHandles("Handles after GC cycle, none expected here.", handleFilters);
      }

      // this will fail on Windows because handles for some of to-be-deleted files are being hold by JVM
      for (File f : filesToDelete)
      {
         FileUtils.deleteDirectory(f);
      }
   }

   private void listHandles(String msg, List<String> filters)
   {
      if (!HANDLE.exists())
      {
         return;
      }

      System.out.println("=vvv==============================================================================");
      System.out.println(msg + " | filter: " + filters);
      System.out.println("----------------------------------------------------------------------------------");

      StringBuilder sb = new StringBuilder();
      for (String filter : filters)
      {
         sb.append(ProcessHandler.getCommandOutput(HANDLE.getAbsolutePath() + " " + filter))
               .append(System.lineSeparator());
      }
      System.out.println(sb.toString());
      System.out.println("=^^^==============================================================================");
      System.out.println();
   }
}
