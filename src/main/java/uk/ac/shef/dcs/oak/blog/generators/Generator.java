package uk.ac.shef.dcs.oak.blog.generators;

import java.io.File;
import java.util.Map;

public interface Generator
{
   /**
    * Generates a string array on the basis of the given parameters
    * 
    * @param parameters
    * @return
    */
   String generate(File sourceFile, String[] parameters);

   /**
    * Generates a string array on the basis of the given parameters
    * 
    * @param parameters
    * @return
    */
   String generate(File sourceFile, String[] parameters, Map<File, String> pageMap);

   /**
    * Defines if this generator operates post hoc
    * 
    * @return
    */
   boolean postProcess();
}
