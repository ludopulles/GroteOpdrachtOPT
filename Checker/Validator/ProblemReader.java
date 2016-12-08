/*    */ package Validator;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ProblemReader
/*    */ {
/* 15 */   public static final ProblemReader reader = new ProblemReader();
/*    */   
/*    */   public int[][] readDistances(BufferedReader br) throws IOException
/*    */   {
/* 19 */     ArrayList<DistanceData> data = new ArrayList();
/*    */     
/* 21 */     int maxIndex = -1;
/*    */     
/*    */ 
/* 24 */     br.readLine();
/*    */     
/* 26 */     String line = br.readLine();
/* 27 */     int to; while (line != null)
/*    */     {
/* 29 */       String[] lineData = line.split(";");
/*    */       
/* 31 */       int from = Integer.parseInt(lineData[0].trim());
/* 32 */       to = Integer.parseInt(lineData[1].trim());
/* 33 */       int dist = Integer.parseInt(lineData[3].trim());
/*    */       
/* 35 */       data.add(new DistanceData(from, to, dist));
/*    */       
/* 37 */       maxIndex = Math.max(maxIndex, Math.max(from, to));
/*    */       
/* 39 */       line = br.readLine();
/*    */     }
/*    */     
/* 42 */     maxIndex++;
/* 43 */     int[][] matrix = new int[maxIndex][maxIndex];
/*    */     
/* 45 */     for (DistanceData d : data)
/*    */     {
/* 47 */       matrix[d.from][d.to] = d.dist;
/*    */     }
/*    */     
/* 50 */     return matrix;
/*    */   }
/*    */   
/*    */   public TreeMap<Integer, Order> readOrders(BufferedReader br) throws IOException
/*    */   {
/* 55 */     TreeMap<Integer, Order> orders = new TreeMap();
/*    */     
/* 57 */     br.readLine();
/*    */     
/* 59 */     String line = br.readLine();
/*    */     
/* 61 */     orders.put(Integer.valueOf(0), new Order(0, "Maarheeze", 0, 0, 0, 0.0D, 287, 56071576L, 513090749L));
/*    */     
/* 63 */     while (line != null)
/*    */     {
/* 65 */       String[] lineData = line.split(";");
/*    */       
/* 67 */       int orderId = Integer.parseInt(lineData[0].trim());
/* 68 */       String plaats = lineData[1].trim();
/* 69 */       int freq = Integer.parseInt(lineData[2].trim().substring(0, 1));
/* 70 */       int aantCont = Integer.parseInt(lineData[3].trim());
/* 71 */       int volumeCont = Integer.parseInt(lineData[4].trim());
/* 72 */       double legingTijd = Double.parseDouble(lineData[5].trim()) * 60.0D;
/* 73 */       int matrixId = Integer.parseInt(lineData[6].trim());
/* 74 */       long xCoord = Long.parseLong(lineData[7].trim());
/* 75 */       long yCoord = Long.parseLong(lineData[8].trim());
/*    */       
/* 77 */       Order order = new Order(orderId, plaats, freq, aantCont, volumeCont, legingTijd, matrixId, xCoord, yCoord);
/*    */       
/* 79 */       orders.put(Integer.valueOf(orderId), order);
/*    */       
/* 81 */       line = br.readLine();
/*    */     }
/*    */     
/* 84 */     return orders;
/*    */   }
/*    */   
/*    */   public final class DistanceData
/*    */   {
/*    */     public final int from;
/*    */     public final int to;
/*    */     public final int dist;
/*    */     
/*    */     public DistanceData(int from, int to, int dist)
/*    */     {
/* 95 */       this.from = from;
/* 96 */       this.to = to;
/* 97 */       this.dist = dist;
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\Downloads\Checker.jar!\Validator\ProblemReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */