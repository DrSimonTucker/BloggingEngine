package uk.ac.shef.dcs.oak.blog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.petebevin.markdown.MarkdownProcessor;

public class PageProcessor
{
   MarkdownProcessor proc = new MarkdownProcessor();

   Pattern procPattern = Pattern.compile("(\\[\\[.*?\\]\\])");

   /**
    * Method to build the output file on the basis of the source and the
    * template filese
    * 
    * @param sourceFile
    * @param templateFile
    * @param outputFile
    */
   public void process(File sourceFile, File templateFile, File outputFile) throws IOException
   {
      Map<String, String> filler = new TreeMap<String, String>();
      String source = markdown(process(sourceFile, filler));

      filler.put("SOURCE", source);
      String output = process(templateFile, filler);
      PrintStream ps = new PrintStream(outputFile);
      ps.print(output);
      ps.close();
   }

   public String process(File sourceFile, Map<String, String> fillers) throws IOException
   {
      StringBuffer myBuffer = new StringBuffer();

      BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
      for (String line = reader.readLine(); line != null; line = reader.readLine())
         if (line.startsWith("~"))
         {
            String[] elems = line.trim().split("~");
            System.err.println("Adding filler: " + elems[1] + " => " + elems[2]);
            fillers.put(elems[1], elems[2]);
            System.err.println(fillers.keySet());
         }
         else
            myBuffer.append(process(line, fillers) + "\n");
      reader.close();

      return myBuffer.toString();
   }

   private String process(String line, Map<String, String> fillers)
   {
      Map<String, String> replaceMap = new TreeMap<String, String>();
      Matcher m = procPattern.matcher(line);
      while (m.find())
      {
         String rep = build(m.group(1).substring(2, m.group(1).length() - 2), fillers);
         if (rep != null)
            replaceMap.put(m.group(1), rep);
         else
            System.err.println("Cannot process filler: " + m.group(1) + " given "
                  + fillers.keySet());
      }

      String repString = line;
      for (Entry<String, String> ent : replaceMap.entrySet())
         repString = repString.replace(ent.getKey(), ent.getValue());

      return repString;
   }

   private String build(String in, Map<String, String> filler)
   {
      if (SiteBuilder.debug)
         System.err.println("Replacing: " + in);

      if (filler.containsKey(in))
         return filler.get(in);

      return null;
   }

   public String markdown(String in)
   {
      return proc.markdown(in);
   }

}
