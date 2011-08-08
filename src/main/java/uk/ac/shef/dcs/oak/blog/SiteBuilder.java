package uk.ac.shef.dcs.oak.blog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Hello world!
 * 
 */
public class SiteBuilder
{
   public static boolean debug = true;

   public static void main(String[] args)
   {
      if (args.length == 0)
      {
         SiteBuilder builder = new SiteBuilder("input");
         builder.processInput(new File("output"));
      }
      else if (args.length == 2)
      {
         SiteBuilder builder = new SiteBuilder(args[0]);
         builder.processInput(new File(args[1]));
      }
   }

   Map<File, File> inputFileToTemplate = new TreeMap<File, File>(new Comparator<File>()
   {
      @Override
      public int compare(File arg0, File arg1)
      {
         String fsc = File.separator;
         if (fsc.equals("\\"))
            fsc = "\\\\";

         // Get the depth of each file
         Integer depth0 = arg0.getAbsolutePath().split(fsc).length;
         Integer depth1 = arg1.getAbsolutePath().split(fsc).length;

         if (depth0.compareTo(depth1) == 0)
            if (arg0.getName().equals("index.markdown"))
               return -1;
            else if (arg0.getName().equals("index.markdown"))
               return 1;
            else
               return arg0.compareTo(arg1);
         else
            return depth0.compareTo(depth1);
      }
   });

   Map<File, String> outputMap = new TreeMap<File, String>();

   File baseFile = null;
   PageProcessor proc = new PageProcessor();
   List<HoldOutPage> heldPages = new LinkedList<HoldOutPage>();

   public SiteBuilder(String inputPath)
   {
      File f = new File(inputPath);
      buildMap(f);
   }

   private boolean clear(File outputPath)
   {
      for (File f : outputPath.listFiles())
         if (f.isDirectory())
            clear(f);

      for (File f : outputPath.listFiles())
      {
         if (debug)
            System.out.println("Deleting: " + f);
         if (!f.delete())
         {
            System.out.println("Unable to delete: " + f);
            return false;
         }
      }
      return true;
   }

   public void processInput(File outputPath)
   {
      Map<String, String> filler = new TreeMap<String, String>();

      // Clear the outputDirectory
      if (clear(outputPath))
         for (Entry<File, File> entry : inputFileToTemplate.entrySet())
            try
            {
               filler = buildOutput(entry.getKey(), entry.getValue(), outputPath, filler);
            }
            catch (IOException e)
            {
               System.out.println("Unable to build " + entry.getKey() + ": "
                     + e.getLocalizedMessage());
            }
            catch (HoldOutException e)
            {
               HoldOutPage page = new HoldOutPage(filler, entry.getKey(), entry.getValue(),
                     outputPath);
               heldPages.add(page);
            }
      else
         System.err.println("Output directory could not be cleared!");

      // Process all the holdouts
      for (HoldOutPage page : heldPages)
         try
         {
            buildHoldOutOutput(page.inputSource, page.templateFile, page.outputFile, page.filler);
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }

   }

   private Map<String, String> buildOutput(File inputFile, File templateFile, File outputPath,
         Map<String, String> filler) throws IOException, HoldOutException
   {
      if (debug)
         System.out.println("Building " + inputFile);

      if (!inputFile.getName().equals("template.html"))
         if (inputFile.getName().endsWith(".markdown"))
         {
            // Build the output file
            String outputFilePath = inputFile.getAbsolutePath().substring(
                  baseFile.getAbsolutePath().length());
            outputFilePath = outputFilePath.substring(0, outputFilePath.lastIndexOf(".")) + ".html";
            File outputFile = new File(outputPath, outputFilePath);

            if (outputFile.getParentFile().mkdirs())
               System.err.println("Created directories");

            filler = proc.process(inputFile, templateFile, outputFile, filler, outputMap);

            if (debug)
               System.out.println("BUILD: " + inputFile + " => " + outputFile + " (" + templateFile
                     + ")");

         }
         else
            copy(inputFile,
                  new File(outputPath, inputFile.getAbsolutePath().substring(
                        baseFile.getAbsolutePath().length())));

      return filler;
   }

   private Map<String, String> buildHoldOutOutput(File inputFile, File templateFile,
         File outputPath, Map<String, String> filler) throws IOException
   {
      if (debug)
         System.out.println("Building " + inputFile);

      if (!inputFile.getName().equals("template.html"))
         if (inputFile.getName().endsWith(".markdown"))
         {
            // Build the output file
            String outputFilePath = inputFile.getAbsolutePath().substring(
                  baseFile.getAbsolutePath().length());
            outputFilePath = outputFilePath.substring(0, outputFilePath.lastIndexOf(".")) + ".html";
            File outputFile = new File(outputPath, outputFilePath);

            if (outputFile.getParentFile().mkdirs())
               System.err.println("Created directories");

            if (debug)
               System.out.println("BUILD: " + inputFile + " => " + outputFile + " (" + templateFile
                     + ")");

            filler = proc.processHeld(inputFile, templateFile, outputFile, filler, outputMap);
         }
         else
            copy(inputFile,
                  new File(outputPath, inputFile.getAbsolutePath().substring(
                        baseFile.getAbsolutePath().length())));

      return filler;
   }

   private void copy(File inFile, File outFile) throws IOException
   {
      if (debug)
         System.out.println("Copying: " + inFile + " => " + outFile);

      FileInputStream fis = new FileInputStream(inFile);
      FileOutputStream fos = new FileOutputStream(outFile);
      byte[] buffer = new byte[1024];
      int read = fis.read(buffer);
      while (read > 0)
      {
         fos.write(buffer, 0, read);
         read = fis.read(buffer);
      }
      fos.close();
      fis.close();
   }

   private void buildMap(File f)
   {
      baseFile = f;

      // Look for the base template file
      File templateFile = new File(f, "template.html");

      if (templateFile.exists())
         buildMap(f, templateFile);
      else
         System.err.println("No template file!");
   }

   private void buildMap(File proc, File template)
   {
      if (debug)
         System.out.println("Processing: " + proc);

      if (proc.isDirectory())
      {
         // First search the directory for a template file
         for (File f : proc.listFiles())
            if (f.getName().equals("template.html"))
               template = f;

         // Now process each of the files in the directory
         for (File f : proc.listFiles())
            buildMap(f, template);
      }
      else
         // Add this to the map if it's a file
         inputFileToTemplate.put(proc, template);
   }
}

class HoldOutPage
{
   Map<String, String> filler = new TreeMap<String, String>();
   File inputSource;
   File templateFile;
   File outputFile;

   public HoldOutPage(Map<String, String> fillerIn, File input, File template, File output)
   {
      inputSource = input;
      templateFile = template;
      filler.putAll(fillerIn);
      outputFile = output;
   }

}
