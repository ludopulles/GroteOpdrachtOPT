/*     */ package Validator;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Order
/*     */ {
/*     */   public final int id;
/*     */   
/*     */   public final String location;
/*     */   
/*     */   public final int freq;
/*     */   
/*     */   public final int containers;
/*     */   
/*     */   public final int volumePerContainer;
/*     */   
/*     */   public final double legingTijd;
/*     */   
/*     */   public final int loc;
/*     */   
/*     */   public final long xCoord;
/*     */   
/*     */   public final long yCoord;
/*     */   
/*     */   final int volume;
/*     */   
/*  27 */   int[] orderCountDay = new int[5];
/*  28 */   boolean declined = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Order(int id, String location, int freq, int containers, int volumePerContainer, double legingTijd, int loc, long xCoord, long yCoord)
/*     */   {
/*  35 */     this.id = id;
/*  36 */     this.location = location;
/*  37 */     this.freq = freq;
/*  38 */     this.containers = containers;
/*  39 */     this.volumePerContainer = volumePerContainer;
/*  40 */     this.legingTijd = legingTijd;
/*  41 */     this.loc = loc;
/*  42 */     this.xCoord = xCoord;
/*  43 */     this.yCoord = yCoord;
/*     */     
/*  45 */     this.volume = (this.containers * this.volumePerContainer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isValid(WarningCollector warnings)
/*     */   {
/*  55 */     int count = 0;
/*  56 */     for (int i = 0; i < 5; i++)
/*     */     {
/*  58 */       switch (this.orderCountDay[i]) {
/*     */       case 0: 
/*     */         break;
/*  61 */       case 1:  count++;
/*  62 */         break;
/*  63 */       default:  warnings.addWarning("!!! Order " + this.id + " is planned " + this.orderCountDay[i] + " times on " + getDay(i));
/*  64 */         return false;
/*     */       }
/*     */       
/*     */     }
/*  68 */     if (count == 0)
/*     */     {
/*  70 */       this.declined = true;
/*     */     }
/*     */     else
/*     */     {
/*  74 */       this.declined = false;
/*  75 */       if (count != this.freq)
/*     */       {
/*  77 */         warnings.addWarning("!!! Order " + this.id + " is planned " + count + " times in the week.\n" + 
/*  78 */           "       expected 0 or " + this.freq + " times.");
/*  79 */         return false;
/*     */       }
/*     */       
/*  82 */       switch (this.freq) {
/*     */       case 2: 
/*  84 */         if ((this.orderCountDay[0] + this.orderCountDay[3] != 2) && 
/*  85 */           (this.orderCountDay[1] + this.orderCountDay[4] != 2))
/*     */         {
/*  87 */           warnings.addWarning("!!! Order " + this.id + " isn't planned on the correct days.\n" + 
/*  88 */             "       planned on: " + getDaysPlanned() + "\n" + 
/*  89 */             "       supposed to be planned on either Mo_Th or Tu_Fr");
/*  90 */           return false;
/*     */         }
/*     */         break;
/*  93 */       case 3:  if (this.orderCountDay[0] + this.orderCountDay[2] + this.orderCountDay[4] != 3)
/*     */         {
/*  95 */           warnings.addWarning("!!! Order " + this.id + " isn't planned on the correct days.\n" + 
/*  96 */             "       planned on: " + getDaysPlanned() + "\n" + 
/*  97 */             "       supposed to be planned on Mo_We_Fr");
/*  98 */           return false;
/*     */         }
/*     */         break;
/*     */       }
/*     */       
/*     */     }
/* 104 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDaysPlanned()
/*     */   {
/* 114 */     String s = null;
/* 115 */     for (int i = 0; i < 5; i++)
/*     */     {
/* 117 */       if (this.orderCountDay[i] > 0)
/*     */       {
/* 119 */         s = (s == null ? "" : new StringBuilder(String.valueOf(s)).append("_").toString()) + getDayShort(i);
/*     */       }
/*     */     }
/*     */     
/* 123 */     return s == null ? "Declined" : s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void initChecking()
/*     */   {
/* 131 */     for (int i = 0; i < 5; i++)
/*     */     {
/* 133 */       this.orderCountDay[i] = 0;
/*     */     }
/*     */     
/* 136 */     this.declined = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getDay(int day)
/*     */   {
/* 147 */     switch (day) {
/*     */     case 0: 
/* 149 */       return "Monday";
/* 150 */     case 1:  return "Tuesday";
/* 151 */     case 2:  return "Wednessday";
/* 152 */     case 3:  return "Thursday";
/* 153 */     case 4:  return "Friday"; }
/* 154 */     return "NoDay";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getDayShort(int day)
/*     */   {
/* 166 */     switch (day) {
/*     */     case 0: 
/* 168 */       return "Mo";
/* 169 */     case 1:  return "Tu";
/* 170 */     case 2:  return "We";
/* 171 */     case 3:  return "Th";
/* 172 */     case 4:  return "Fr"; }
/* 173 */     return "??";
/*     */   }
/*     */ }


/* Location:              D:\Downloads\Checker.jar!\Validator\Order.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */