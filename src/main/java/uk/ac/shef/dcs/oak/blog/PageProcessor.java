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

import uk.ac.shef.dcs.oak.blog.generators.Generator;

import com.petebevin.markdown.MarkdownProcessor;

public class PageProcessor
{
   MarkdownProcessor proc = new MarkdownProcessor();

   Pattern procPattern = Pattern.compile("(\\[\\[.*?\\]\\])");

   public static boolean doingHoldout = false;

   /**
    * Method to build the output file on the basis of the source and the
    * template filese
    * 
    * @param sourceFile
    * @param templateFile
    * @param outputFile
    */
   public Map<String, String> process(File sourceFile, File templateFile, File outputFile,
         Map<String, String> filler, Map<File, String> sourceMap) throws IOException,
         HoldOutException
   {
      String source = markdown(process(sourceFile, filler, sourceFile));
      sourceMap.put(sourceFile, source);

      filler.put("SOURCE", source);
      String output = process(templateFile, filler, sourceFile);
      PrintStream ps = new PrintStream(outputFile);
      ps.print(output);
      ps.close();

      return filler;
   }

   public String process(File sourceFile, Map<String, String> fillers, File inputFile)
         throws IOException, HoldOutException
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
            myBuffer.append(process(inputFile, line, fillers) + "\n");
      reader.close();

      return myBuffer.toString();
   }

   private String process(File sourceFile, String line, Map<String, String> fillers)
         throws HoldOutException
   {
      Map<String, String> replaceMap = new TreeMap<String, String>();
      Matcher m = procPattern.matcher(line);
      while (m.find())
      {
         String rep = build(sourceFile, m.group(1).substring(2, m.group(1).length() - 2), fillers);

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

   private String build(File sourceFile, String in, Map<String, String> filler)
         throws HoldOutException
   {
      if (SiteBuilder.debug)
         System.err.println("Replacing: " + in);

      if (filler.containsKey(in))
         return filler.get(in);

      try
      {
         String[] elems = in.split(":");
         String[] params = new String[0];
         String name = elems[0];
         if (elems.length == 2)
            params = elems[1].split(",");

         @SuppressWarnings("unchecked")
         Class<Generator> c = (Class<Generator>) Class
               .forName("uk.ac.shef.dcs.oak.blog.generators." + name.substring(0, 1).toUpperCase()
                     + name.substring(1).toLowerCase());
         Generator g = c.getConstructor(new Class[0]).newInstance(new Object[0]);
         if (g.postProcess())
            throw new HoldOutException(filler);
         return g.generate(sourceFile, params);
      }
      catch (HoldOutException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      return null;
   }

   public String markdown(String in)
   {
      return proc.markdown(in);
   }

}
