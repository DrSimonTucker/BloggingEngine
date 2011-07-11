package uk.ac.shef.dcs.oak.blog.generators;

import java.io.File;

public interface Generator
{
   /**
    * Generates a string array on the basis of the given parameters
    * 
    * @param parameters
    * @return
    */
   String generate(File sourceFile);
}
