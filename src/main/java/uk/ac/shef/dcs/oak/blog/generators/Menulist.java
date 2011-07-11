package uk.ac.shef.dcs.oak.blog.generators;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Menulist implements Generator
{

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
