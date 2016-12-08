/*    */ package Validator;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class DefaultProblem
/*    */ {
/*    */   private static TreeMap<Integer, Order> defaultOrders;
/*    */   private static int[][] defaultMatrix;
/*    */   private static ProblemData defaultProblem;
/*    */   
/*    */   public static ProblemData getDefaultProblem()
/*    */   {
/* 23 */     if (defaultProblem == null)
/*    */     {
/*    */       try
/*    */       {
/* 27 */         defaultProblem = new ProblemData(getDefaultOrders(), getDefaultMatrix());
/*    */       }
/*    */       catch (IOException e)
/*    */       {
/* 31 */         throw new Error(e);
/*    */       }
/*    */     }
/*    */     
/* 35 */     return defaultProblem;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static int[][] getDefaultMatrix()
/*    */     throws IOException
/*    */   {
/* 46 */     if (defaultMatrix == null)
/*    */     {
/* 48 */       InputStream ip = ClassLoader.getSystemResourceAsStream("resources/AfstandenMatrix.txt");
/* 49 */       defaultMatrix = ProblemReader.reader.readDistances(new BufferedReader(new InputStreamReader(ip)));
/*    */     }
/*    */     
/*    */ 
/* 53 */     return defaultMatrix;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static TreeMap<Integer, Order> getDefaultOrders()
/*    */     throws IOException
/*    */   {
/* 64 */     if (defaultOrders == null)
/*    */     {
/* 66 */       InputStream ip = ClassLoader.getSystemResourceAsStream("resources/Orderbestand.txt");
/* 67 */       defaultOrders = ProblemReader.reader.readOrders(new BufferedReader(new InputStreamReader(ip)));
/*    */     }
/*    */     
/*    */ 
/* 71 */     return defaultOrders;
/*    */   }
/*    */ }


/* Location:              D:\Downloads\Checker.jar!\Validator\DefaultProblem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */