package uk.ac.shef.dcs.oak.blog.generators;

import java.io.File;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Localblog implements Generator
{

   @Override
   public String generate(File sourceFile, String[] parameters)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean postProcess()
   {
      // This runs after all the other pages have been built
      return true;
   }

   @Override
   public String generate(File sourceFile, String[] parameters, Map<File, String> pageMap)
   {
      // This deals with splitting problem on windows machines
      String fsc = File.separator;
      if (fsc.equals("\\"))
         fsc = "\\\\";

      String parentPath = sourceFile.getParentFile().getAbsolutePath();

      List<File> entryFiles = new LinkedList<File>();
      for (File f : pageMap.keySet())
         if (f.getAbsolutePath().startsWith(parentPath))
            entryFiles.add(f);

      Collections.sort(entryFiles, new Comparator<File>()
      {
         @Override
         public int compare(File arg0, File arg1)
         {
            return -(int) (arg0.lastModified() - arg1.lastModified());
         }
      });

      StringBuffer outString = new StringBuffer();
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      for (File f : entryFiles)
         outString.append("###" + df.format(f.lastModified()) + "###\n" + summ(pageMap.get(f)));

      return outString.toString();
   }

   private String summ(String in)
   {
      return in;
      // return in.substring(0, Math.min(in.length(), 100)) + "...";
   }
}
