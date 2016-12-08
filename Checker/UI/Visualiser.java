/*     */ package UI;
/*     */ 
/*     */ import Validator.Order;
/*     */ import Validator.ProblemData;
/*     */ import Validator.Solution;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.ComponentListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.ArrayList;
/*     */ import javax.swing.BoundedRangeModel;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollBar;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Visualiser
/*     */   extends JPanel
/*     */   implements ComponentListener, MouseListener, ChangeListener
/*     */ {
/*     */   private static final long serialVersionUID = 8589186749519499054L;
/*     */   App main;
/*     */   Solution solution;
/*     */   BufferedImage image;
/*     */   JScrollPane scrollPane;
/*     */   JScrollBar sp_h;
/*     */   JScrollBar sp_v;
/*  41 */   int viewPortWidth = 0;
/*  42 */   int viewPortHeight = 0;
/*     */   
/*  44 */   int zoom = 1;
/*     */   
/*     */ 
/*     */   ArrayList<VisualiserRoute> routes;
/*     */   
/*  49 */   long minX = Long.MAX_VALUE;
/*  50 */   long maxX = Long.MIN_VALUE;
/*  51 */   long minY = Long.MAX_VALUE;
/*  52 */   long maxY = Long.MIN_VALUE;
/*     */   
/*     */ 
/*  55 */   boolean repos = false;
/*     */   
/*     */   int center_x;
/*     */   
/*     */   int center_y;
/*     */   int oldMaxV;
/*     */   int oldMaxH;
/*     */   
/*     */   public Visualiser(App main)
/*     */   {
/*  65 */     this.main = main;
/*     */     
/*  67 */     this.routes = new ArrayList();
/*     */     
/*  69 */     addMouseListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void paintComponent(Graphics g)
/*     */   {
/*  76 */     g.drawImage(this.image, 0, 0, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateSize(JViewport port)
/*     */   {
/*  89 */     this.viewPortHeight = port.getHeight();
/*  90 */     this.viewPortWidth = port.getWidth();
/*     */     
/*  92 */     setPreferredSize(new Dimension(this.viewPortWidth * this.zoom, 
/*  93 */       this.viewPortHeight * this.zoom));
/*     */     
/*  95 */     repaintBufferedImage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setJScrollPane(JScrollPane sp)
/*     */   {
/* 106 */     if (this.scrollPane != null)
/*     */     {
/* 108 */       throw new Error("ScrollPane Already initiated!");
/*     */     }
/*     */     
/* 111 */     this.scrollPane = sp;
/* 112 */     this.sp_h = sp.getHorizontalScrollBar();
/* 113 */     this.sp_v = sp.getVerticalScrollBar();
/*     */     
/* 115 */     updateSize(sp.getViewport());
/* 116 */     sp.addComponentListener(this);
/*     */     
/* 118 */     this.sp_h.getModel().addChangeListener(this);
/* 119 */     this.sp_v.getModel().addChangeListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void repaintBufferedImage()
/*     */   {
/* 127 */     if ((this.solution != null) && (this.viewPortHeight != 0) && (this.viewPortWidth != 0))
/*     */     {
/*     */ 
/* 130 */       if (this.image == null)
/*     */       {
/* 132 */         this.image = new BufferedImage(this.viewPortWidth * this.zoom, this.viewPortHeight * this.zoom, 
/* 133 */           1);
/*     */       }
/* 135 */       else if ((this.image.getHeight() != this.viewPortHeight * this.zoom) || (this.image.getWidth() != this.viewPortWidth * this.zoom))
/*     */       {
/* 137 */         this.image = new BufferedImage(this.viewPortWidth * this.zoom, this.viewPortHeight * this.zoom, 
/* 138 */           1);
/*     */         
/* 140 */         System.gc();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 145 */         this.image.flush();
/*     */       }
/*     */       
/* 148 */       Graphics g = this.image.createGraphics();
/*     */       
/*     */ 
/* 151 */       g.setColor(Color.lightGray);
/* 152 */       g.fillRect(0, 0, this.image.getWidth(), this.image.getHeight());
/*     */       
/*     */ 
/* 155 */       double xF = this.viewPortWidth * this.zoom / (1.0D * this.maxX - this.minX);
/* 156 */       double yF = this.viewPortHeight * this.zoom / (1.0D * this.maxY - this.minY);
/* 157 */       long xOff = -this.minX;
/* 158 */       long yOff = -this.minY;
/*     */       
/*     */ 
/* 161 */       for (VisualiserRoute r : this.routes)
/*     */       {
/* 163 */         if (r.visible)
/*     */         {
/* 165 */           r.DrawRoute(this.solution, g, xOff, yOff, xF, yF);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 170 */       g.setColor(Color.black);
/* 171 */       for (Order o : this.solution.problem.getOrders())
/*     */       {
/* 173 */         int x = (int)(xF * (o.xCoord + xOff));
/* 174 */         int y = (int)(yF * (o.yCoord + yOff));
/*     */         
/* 176 */         g.drawRect(x - 1, y - 1, 2, 2);
/*     */       }
/*     */       
/*     */ 
/* 180 */       Order disposal = this.solution.problem.getOrder(0);
/*     */       
/* 182 */       g.setColor(Color.red);
/*     */       
/* 184 */       int x = (int)(xF * (disposal.xCoord + xOff));
/* 185 */       int y = (int)(yF * (disposal.yCoord + yOff));
/*     */       
/* 187 */       g.fillRect(x - 2, y - 2, 4, 4);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 192 */     repaint();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSollution(Solution sol)
/*     */   {
/* 202 */     this.solution = sol;
/* 203 */     if (sol == null)
/*     */     {
/* 205 */       this.image = null;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 210 */       this.minX = Long.MAX_VALUE;
/* 211 */       this.maxX = Long.MIN_VALUE;
/* 212 */       this.minY = Long.MAX_VALUE;
/* 213 */       this.maxY = Long.MIN_VALUE;
/*     */       
/* 215 */       for (Order o : this.solution.problem.getOrders())
/*     */       {
/* 217 */         this.minX = Math.min(this.minX, o.xCoord);
/* 218 */         this.maxX = Math.max(this.maxX, o.xCoord);
/* 219 */         this.minY = Math.min(this.minY, o.yCoord);
/* 220 */         this.maxY = Math.max(this.maxY, o.yCoord);
/*     */       }
/*     */       
/*     */ 
/* 224 */       this.minY = ((this.minY - 0.05D * (this.maxY - this.minY)));
/* 225 */       this.maxY = ((this.maxY + 0.05D * (this.maxY - this.minY)));
/* 226 */       this.minX = ((this.minX - 0.05D * (this.maxX - this.minX)));
/* 227 */       this.maxX = ((this.maxX + 0.05D * (this.maxX - this.minX)));
/*     */       
/*     */ 
/* 230 */       int routeId = 0;
/* 231 */       this.routes.clear();
/*     */       
/* 233 */       for (int d = 0; d < 5; d++) {
/* 234 */         for (int t = 0; t < 2; t++)
/*     */         {
/* 236 */           Order[] route = sol.sollution[t][d];
/* 237 */           int r = 0;
/*     */           
/*     */ 
/* 240 */           if (route.length > 1)
/*     */           {
/* 242 */             r = 1;
/* 243 */             Color c = ColorGenerator.getColor(routeId);
/* 244 */             routeId++;
/* 245 */             this.routes.add(new VisualiserRoute(t, d, 1, 0, c));
/*     */           }
/*     */           
/* 248 */           for (int i = 0; i < route.length; i++)
/*     */           {
/* 250 */             Order o = route[i];
/* 251 */             if (o.id == 0)
/*     */             {
/* 253 */               if (i + 1 < route.length)
/*     */               {
/* 255 */                 r++;
/* 256 */                 Color c = ColorGenerator.getColor(routeId);
/* 257 */                 routeId++;
/* 258 */                 this.routes.add(new VisualiserRoute(t, d, r, i + 1, c));
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 264 */       repaintBufferedImage();
/*     */       
/* 266 */       this.main.visualiser.setRoutes(this.routes);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void componentHidden(ComponentEvent arg0) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void componentMoved(ComponentEvent arg0) {}
/*     */   
/*     */ 
/*     */   public void componentShown(ComponentEvent arg0) {}
/*     */   
/*     */ 
/*     */   public void componentResized(ComponentEvent e)
/*     */   {
/* 284 */     updateSize(this.scrollPane.getViewport());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseClicked(MouseEvent me)
/*     */   {
/* 292 */     int x = me.getX();
/* 293 */     int y = me.getY();
/*     */     
/* 295 */     if (me.getButton() == 1)
/*     */     {
/*     */ 
/* 298 */       if (this.zoom != 8)
/*     */       {
/*     */ 
/* 301 */         this.zoom *= 2;
/* 302 */         setPreferredSize(new Dimension(this.viewPortWidth * this.zoom, 
/* 303 */           this.viewPortHeight * this.zoom));
/* 304 */         repaintBufferedImage();
/* 305 */         this.scrollPane.revalidate();
/*     */         
/* 307 */         this.center_x = (x * 2);
/* 308 */         this.center_y = (y * 2);
/* 309 */         this.oldMaxV = this.sp_v.getMaximum();
/* 310 */         this.oldMaxH = this.sp_h.getMaximum();
/* 311 */         this.repos = true;
/*     */         
/*     */ 
/* 314 */         this.sp_v.setValue(this.sp_v.getValue() + 1);
/* 315 */         this.sp_h.setValue(this.sp_h.getValue() + 1);
/*     */       }
/*     */     }
/* 318 */     else if (me.getButton() == 3)
/*     */     {
/* 320 */       if (this.zoom != 1)
/*     */       {
/*     */ 
/* 323 */         this.zoom /= 2;
/* 324 */         setPreferredSize(new Dimension(this.viewPortWidth * this.zoom, 
/* 325 */           this.viewPortHeight * this.zoom));
/* 326 */         repaintBufferedImage();
/* 327 */         this.scrollPane.revalidate();
/*     */         
/* 329 */         this.center_x = (x / 2);
/* 330 */         this.center_y = (y / 2);
/* 331 */         this.oldMaxV = this.sp_v.getMaximum();
/* 332 */         this.oldMaxH = this.sp_h.getMaximum();
/* 333 */         this.repos = true;
/*     */         
/*     */ 
/* 336 */         this.sp_v.setValue(this.sp_v.getValue() + 1);
/* 337 */         this.sp_h.setValue(this.sp_h.getValue() + 1);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 342 */     this.main.visualiser.location.setText("zoom: " + this.zoom + "x");
/* 343 */     repaint();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void mouseEntered(MouseEvent arg0) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void mouseExited(MouseEvent arg0) {}
/*     */   
/*     */ 
/*     */   public void mousePressed(MouseEvent arg0) {}
/*     */   
/*     */ 
/*     */   public void mouseReleased(MouseEvent arg0) {}
/*     */   
/*     */ 
/*     */   public void stateChanged(ChangeEvent arg0)
/*     */   {
/* 363 */     int w = this.sp_h.getMaximum();
/* 364 */     int h = this.sp_v.getMaximum();
/*     */     
/* 366 */     if ((this.repos) && (this.oldMaxH != w) && (this.oldMaxV != this.sp_v.getMaximum()))
/*     */     {
/*     */ 
/* 369 */       this.repos = false;
/* 370 */       int x = Math.max(0, Math.min(this.center_x - this.viewPortWidth / 2, w));
/* 371 */       int y = Math.max(0, Math.min(this.center_y - this.viewPortHeight / 2, h));
/*     */       
/* 373 */       this.sp_h.setValue(x);
/* 374 */       this.sp_v.setValue(y);
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\Downloads\Checker.jar!\UI\Visualiser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */