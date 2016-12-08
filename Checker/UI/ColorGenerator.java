/*    */ package UI;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ColorGenerator
/*    */ {
/* 15 */   private static final ArrayList<Color> colors = new ArrayList();
/*    */   
/* 17 */   private static double factor = 1.0D;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Color getColor(int i)
/*    */   {
/* 26 */     while (colors.size() <= i)
/*    */     {
/* 28 */       createNextBatch();
/*    */     }
/*    */     
/* 31 */     return (Color)colors.get(i);
/*    */   }
/*    */   
/*    */ 
/* 35 */   static final int[][] colorLoop = { { 255 }, 
/* 36 */     { 0, 255 }, 
/* 37 */     { 0, 0, 255 }, 
/* 38 */     { 255, 255 }, 
/* 39 */     { 0, 255, 255 }, 
/* 40 */     { 255, 0, 255 }, 
/* 41 */     { 255, 200 }, 
/* 42 */     { 200, 255 }, 
/* 43 */     { 0, 255, 200 }, 
/* 44 */     { 0, 200, 255 }, 
/* 45 */     { 255, 0, 200 }, 
/* 46 */     { 200, 0, 255 }, 
/* 47 */     { 255, 122, 122 }, 
/* 48 */     { 122, 255, 122 }, 
/* 49 */     { 122, 122, 255 }, 
/* 50 */     { 255, 255, 122 }, 
/* 51 */     { 122, 255, 255 }, 
/* 52 */     { 255, 122, 255 } };
/*    */   
/*    */ 
/*    */   public static void createNextBatch()
/*    */   {
/*    */     int[][] arrayOfInt;
/*    */     
/* 59 */     int j = (arrayOfInt = colorLoop).length; for (int i = 0; i < j; i++) { int[] c = arrayOfInt[i];
/*    */       
/* 61 */       colors.add(new Color((int)(factor * c[0]), 
/* 62 */         (int)(factor * c[1]), 
/* 63 */         (int)(factor * c[2])));
/*    */     }
/* 65 */     factor *= 0.8D;
/*    */   }
/*    */ }


/* Location:              D:\Downloads\Checker.jar!\UI\ColorGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */