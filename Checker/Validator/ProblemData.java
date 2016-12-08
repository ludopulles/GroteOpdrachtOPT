/*    */ package Validator;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.File;
/*    */ import java.io.FileReader;
/*    */ import java.io.IOException;
/*    */ import java.util.Collection;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ProblemData
/*    */ {
/* 14 */   TreeMap<Integer, Order> orders = new TreeMap();
/*    */   
/*    */ 
/*    */ 
/*    */   int[][] matrix;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ProblemData(String orderFile, String distanceFile)
/*    */     throws IOException
/*    */   {
/* 26 */     this.orders = ProblemReader.reader.readOrders(new BufferedReader(new FileReader(new File(orderFile))));
/* 27 */     this.matrix = ProblemReader.reader.readDistances(new BufferedReader(new FileReader(new File(orderFile))));
/*    */   }
/*    */   
/*    */   public ProblemData(TreeMap<Integer, Order> orders, int[][] matrix)
/*    */   {
/* 32 */     this.orders = orders;
/* 33 */     this.matrix = matrix;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Order getOrder(int o)
/*    */   {
/* 44 */     return (Order)this.orders.get(Integer.valueOf(o));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Collection<Order> getOrders()
/*    */   {
/* 53 */     return this.orders.values();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getDistance(int loc1, int loc2)
/*    */   {
/* 65 */     return this.matrix[loc1][loc2];
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void initChecking()
/*    */   {
/* 73 */     for (Order o : this.orders.values())
/*    */     {
/* 75 */       o.initChecking();
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\Downloads\Checker.jar!\Validator\ProblemData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */