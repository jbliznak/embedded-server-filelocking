package org.jboss.qa.tests.issues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

class StreamGobbler extends Thread
{
   private static final Logger log = Logger.getLogger(StreamGobbler.class.getSimpleName());

   private final StringBuilder sb = new StringBuilder();

   private final InputStream is;

   private boolean open = true;

   /**
    * Creates a new stream gobbler for reading the output of the process.
    *
    * @param is Stream comming from process.
    */
   StreamGobbler(InputStream is)
   {
      this.is = is;
   }

   @Override
   public void run()
   {
      try (InputStreamReader isr = new InputStreamReader(is);
           BufferedReader br = new BufferedReader(isr))
      {

         String line;
         while (open && (line = br.readLine()) != null)
         {
            if (!line.equals(""))
            {
               sb.append(line).append("\n");
            }
         }
      }
      catch (IOException ioe)
      {
         log.warning(ioe.toString());
         log.log(Level.WARNING, ioe.getLocalizedMessage(), ioe);
      }
      finally
      {
         try
         {
            is.close();
         }
         catch (IOException ioe)
         {
            log.warning(ioe.toString());
            log.log(Level.WARNING, ioe.getLocalizedMessage(), ioe);
         }
      }
   }

   public void close()
   {
      open = false;
   }

   public String getOutput()
   {
      return sb.toString();
   }
}
