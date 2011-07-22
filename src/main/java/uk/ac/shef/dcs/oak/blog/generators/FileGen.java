package uk.ac.shef.dcs.oak.blog.generators;

import java.io.File;
import java.util.Map;

public class Filegen implements Generator
{

   @Override
   public boolean postProcess()
   {
      return false;
   }

   @Override
   public String generate(File sourceFile, String[] parameters, Map<File, String> pageMap)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String generate(File sourceFile, String[] parameters)
   {
      System.err.println("Searching: " + sourceFile + " for " + parameters[0]);
      StringBuffer retString = new StringBuffer();
      String filename = parameters[0];

      java.io.File searchFile = sourceFile.getParentFile();
      while (searchFile != null)
      {
         for (java.io.File f : searchFile.listFiles())
            if (f.getName().equals(filename))
            {
               System.err.println("Found: " + retString + filename);
               return retString + filename;
            }
         searchFile = searchFile.getParentFile();
         retString.append("../");
      }

      return null;
   }
}
