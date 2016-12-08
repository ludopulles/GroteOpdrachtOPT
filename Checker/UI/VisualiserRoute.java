/*    */ package UI;
/*    */ 
/*    */ import Validator.Order;
/*    */ import Validator.ProblemData;
/*    */ import Validator.Solution;
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VisualiserRoute
/*    */ {
/*    */   final int truck;
/*    */   final int day;
/*    */   final int route;
/*    */   final int routeStartId;
/*    */   final Color color;
/* 22 */   public boolean visible = true;
/*    */   
/*    */ 
/*    */   public VisualiserRoute(int truck, int day, int route, int routeStartId, Color color)
/*    */   {
/* 27 */     this.truck = truck;
/* 28 */     this.day = day;
/* 29 */     this.route = route;
/* 30 */     this.routeStartId = routeStartId;
/* 31 */     this.color = color;
/*    */     
/* 33 */     this.visible = true;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void DrawRoute(Solution s, Graphics g, long xOff, long yOff, double xF, double yF)
/*    */   {
/* 48 */     Order[] route = s.sollution[this.truck][this.day];
/*    */     
/* 50 */     int i = this.routeStartId;
/*    */     
/* 52 */     Order dispose = s.problem.getOrder(0);
/* 53 */     Order oPrev = dispose;
/*    */     
/* 55 */     Order oNext = route[i];
/*    */     
/* 57 */     g.setColor(this.color);
/* 58 */     while ((oNext != null) && (oNext.id != 0))
/*    */     {
/*    */ 
/* 61 */       int x1 = (int)(xF * (oPrev.xCoord + xOff));
/* 62 */       int y1 = (int)(yF * (oPrev.yCoord + yOff));
/* 63 */       int x2 = (int)(xF * (oNext.xCoord + xOff));
/* 64 */       int y2 = (int)(yF * (oNext.yCoord + yOff));
/*    */       
/* 66 */       g.drawLine(x1, y1, x2, y2);
/*    */       
/*    */ 
/* 69 */       oPrev = oNext;
/* 70 */       i++;
/* 71 */       oNext = route[i];
/*    */     }
/*    */     
/*    */ 
/* 75 */     int x1 = (int)(xF * (oPrev.xCoord + xOff));
/* 76 */     int y1 = (int)(yF * (oPrev.yCoord + yOff));
/* 77 */     int x2 = (int)(xF * (dispose.xCoord + xOff));
/* 78 */     int y2 = (int)(yF * (dispose.yCoord + yOff));
/*    */     
/* 80 */     g.drawLine(x1, y1, x2, y2);
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 86 */     return Order.getDayShort(this.day) + "-Truck " + (this.truck + 1) + "-Route " + this.route;
/*    */   }
/*    */ }


/* Location:              D:\Downloads\Checker.jar!\UI\VisualiserRoute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */