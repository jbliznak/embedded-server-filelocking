package org.jboss.qa.tests.issues;

import org.junit.Assert;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessHandler
{
   private static final Logger log = Logger.getLogger(ProcessHandler.class.getSimpleName());

   private Process process;

   private StreamGobbler inputGobbler;

   /**
    * Create default process handler with process output timeout 10s. This handler will omit empty lines of process
    * output.
    *
    * @param command Command for the process.
    */
   private ProcessHandler(String command)
   {
      Assert.assertTrue("This ProcessHandler only works on Windows",
            String.valueOf(System.getProperty("os.name").toLowerCase()).contains("windows"));
      log.info("Executing " + command);
      ProcessBuilder pb;
      pb = new ProcessBuilder("cmd", "/c", command);
      pb.redirectErrorStream(true);
      try
      {
         process = pb.start();
      }
      catch (IOException e)
      {
         log.log(Level.WARNING, e.getLocalizedMessage(), e);
         throw new RuntimeException("Unable to start process with command: " + command);
      }
      inputGobbler = new StreamGobbler(process.getInputStream());
      inputGobbler.start();
   }

   private void destroy()
   {
      if (inputGobbler.isAlive())
      {
         // close streams and destroy the process
         inputGobbler.close();
      }
      try
      {
         // wait until the stream is closed
         inputGobbler.join(10000L);
      }
      catch (InterruptedException e)
      {
         log.warning("Execution caught during while waiting for StreamGobbler to stop.\n" + e.toString());
         log.log(Level.WARNING, e.getLocalizedMessage(), e);
      }
      process.destroy();
   }

   private String waitForOutput()
   {
      try
      {
         inputGobbler.join(10000L);
      }
      catch (InterruptedException e)
      {
         log.log(Level.WARNING, e.getLocalizedMessage(), e);
      }
      return inputGobbler.getOutput();
   }

   /**
    * Create default process handler with process output timeout 10s. This handler will omit empty lines of process
    * output.
    *
    * @param command Command for the process
    * @return The output of the {@code command}
    */
   public static String getCommandOutput(String command)
   {
      ProcessHandler processHandler = new ProcessHandler(command);
      String res = processHandler.waitForOutput();
      processHandler.destroy();
      return res;
   }

}
