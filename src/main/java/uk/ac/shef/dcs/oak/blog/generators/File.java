package uk.ac.shef.dcs.oak.blog.generators;

public class File implements Generator
{

   @Override
   public String generate(java.io.File sourceFile, String[] parameters)
   {
      System.err.println("Searching: " + sourceFile);
      StringBuffer retString = new StringBuffer();
      String filename = parameters[0];

      java.io.File searchFile = sourceFile.getParentFile();
      while (searchFile != null)
      {
         for (java.io.File f : searchFile.listFiles())
            if (f.getName().equals(filename))
               return retString + filename;
         searchFile = searchFile.getParentFile();
         retString.append("../");
      }

      return null;
   }
}
