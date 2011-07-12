package uk.ac.shef.dcs.oak.blog.generators;

import java.io.File;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Menulist implements Generator
{
   @Override
   public String generate(File sourceFile, String[] parameters, Map<File, String> pageMap)
   {
      int depthCount = sourceFile.getAbsolutePath().split(File.separator).length;

      List<File> entryFiles = new LinkedList<File>();
      for (File f : pageMap.keySet())
         if (f.getAbsolutePath().split(File.separator).length == depthCount)
            entryFiles.add(f);

      Collections.sort(entryFiles, new Comparator<File>()
      {
         @Override
         public int compare(File arg0, File arg1)
         {
            return (int) (arg0.lastModified() - arg1.lastModified());
         }
      });

      StringBuffer outString = new StringBuffer();
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      for (File f : entryFiles)
      {
         outString.append("###" + df.format(f.lastModified()) + "###\n" + summ(pageMap.get(f)));
      }

      return outString.toString();
   }

   private String summ(String in)
   {
      return in.substring(0, Math.min(in.length(), 100)) + "...";
   }

   @Override
   public boolean postProcess()
   {
      return false;
   }

   @Override
   public String generate(File sourceFile, String[] parameters)
   {
      // Get the directories in the source file folder
      List<String> dirs = new LinkedList<String>();
      for (File f : sourceFile.getParentFile().listFiles())
         if (f.isDirectory())
            dirs.add(f.getName());

      StringBuffer buffer = new StringBuffer("<UL>");
      for (String dir : dirs)
         buffer.append("<LI><A HREF=\"" + dir + "\">" + dir + "</a></LI>\n");
      buffer.append("</UL>");
      return buffer.toString();
   }
}
