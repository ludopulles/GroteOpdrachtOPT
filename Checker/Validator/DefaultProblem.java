 package Validator;
 
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.util.TreeMap;
 
 
 
 
 
 
 
 public final class DefaultProblem
 {
   private static TreeMap<Integer, Order> defaultOrders;
   private static int[][] defaultMatrix;
   private static ProblemData defaultProblem;
   
   public static ProblemData getDefaultProblem()
   {
     if (defaultProblem == null)
     {
       try
       {
         defaultProblem = new ProblemData(getDefaultOrders(), getDefaultMatrix());
       }
       catch (IOException e)
       {
         throw new Error(e);
       }
     }
     
     return defaultProblem;
   }
   
 
 
 
 
 
   public static int[][] getDefaultMatrix()
     throws IOException
   {
     if (defaultMatrix == null)
     {
       InputStream ip = ClassLoader.getSystemResourceAsStream("resources/AfstandenMatrix.txt");
       defaultMatrix = ProblemReader.reader.readDistances(new BufferedReader(new InputStreamReader(ip)));
     }
     
 
     return defaultMatrix;
   }
   
 
 
 
 
 
   public static TreeMap<Integer, Order> getDefaultOrders()
     throws IOException
   {
     if (defaultOrders == null)
     {
       InputStream ip = ClassLoader.getSystemResourceAsStream("resources/Orderbestand.txt");
       defaultOrders = ProblemReader.reader.readOrders(new BufferedReader(new InputStreamReader(ip)));
     }
     
 
     return defaultOrders;
   }
 }


