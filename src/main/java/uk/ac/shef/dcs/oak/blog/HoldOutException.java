package uk.ac.shef.dcs.oak.blog;

import java.util.Map;

public class HoldOutException extends Exception
{
   Map<String, String> filler = null;

   public HoldOutException(Map<String, String> tFiller)
   {
      super();
      filler = tFiller;
   }

   public Map<String, String> getFiller()
   {
      return filler;
   }
}
