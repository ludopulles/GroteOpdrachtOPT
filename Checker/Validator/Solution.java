/*     */ package Validator;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.util.Collection;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ public class Solution
/*     */ {
/*     */   public static final int TruckMaxCapacity = 100000;
/*     */   public static final int TruckMaxTime = 43200;
/*     */   public static final double disposingTime = 1800.0D;
/*     */   public final ProblemData problem;
/*     */   public Order[][][] sollution;
/*     */   
/*     */   public Solution(File file, ProblemData problemdata, WarningCollector warnings)
/*     */     throws IOException
/*     */   {
/*  24 */     BufferedReader br = new BufferedReader(new FileReader(file));
/*     */     
/*  26 */     this.problem = problemdata;
/*     */     
/*  28 */     parseFile(br, warnings);
/*     */   }
/*     */   
/*     */   public Solution(String text, ProblemData problemdata, WarningCollector warnings)
/*     */   {
/*  33 */     BufferedReader br = new BufferedReader(new StringReader(text));
/*  34 */     this.problem = problemdata;
/*     */     
/*     */     try
/*     */     {
/*  38 */       parseFile(br, warnings);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  42 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void parseFile(BufferedReader br, WarningCollector warnings)
/*     */     throws IOException
/*     */   {
/*  56 */     this.problem.initChecking();
/*     */     
/*  58 */     TreeMap[][] result = new TreeMap[2][5];
/*  59 */     this.sollution = new Order[2][5][];
/*     */     
/*  61 */     for (int t = 0; t < 2; t++) {
/*  62 */       for (int d = 0; d < 5; d++)
/*     */       {
/*  64 */         result[t][d] = new TreeMap();
/*     */       }
/*     */     }
/*  67 */     int lineNr = 1;
/*     */     
/*     */ 
/*     */ 
/*  71 */     String line = br.readLine();
/*     */     
/*  73 */     while (line != null)
/*     */     {
/*  75 */       String[] data = line.split(";");
/*     */       
/*  77 */       if (data.length != 4)
/*     */       {
/*  79 */         if ((line.equals("")) && (br.readLine() == null)) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*  85 */         warnings.addWarning("Parsing error in sollution line " + lineNr + ": '" + line + "'.\n" + 
/*  86 */           "  expected format: 'Vehicle; Day; Sequence number; Order'");
/*  87 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/*  95 */         int vehicle = Integer.parseInt(data[0].trim());
/*  96 */         int day = Integer.parseInt(data[1].trim());
/*  97 */         int sequence = Integer.parseInt(data[2].trim());
/*  98 */         int orderId = Integer.parseInt(data[3].trim());
/*     */         
/* 100 */         Order order = this.problem.getOrder(orderId);
/*     */         
/* 102 */         if ((1 > vehicle) || (vehicle > 2))
/*     */         {
/* 104 */           warnings.addWarning("Parsing error in sollution line " + lineNr + ": '" + line + "'.\n" + 
/* 105 */             "  Vehicle index " + vehicle + " not in range of {1,2} \n" + 
/* 106 */             "  Expected format: 'Vehicle; Day; Sequence number; Order'");
/* 107 */           return;
/*     */         }
/*     */         
/* 110 */         if ((1 > day) || (day > 5))
/*     */         {
/* 112 */           warnings.addWarning("Parsing error in sollution line " + lineNr + ": '" + line + "'.\n" + 
/* 113 */             "  Day index " + day + " not in range of {1,2,3,4,5} \n" + 
/* 114 */             "  Expected format: 'Vehicle; Day; Sequence number; Order'");
/* 115 */           return;
/*     */         }
/*     */         
/* 118 */         if (sequence < 1)
/*     */         {
/* 120 */           warnings.addWarning("Parsing error in sollution line " + lineNr + ": '" + line + "'.\n" + 
/* 121 */             "  Sequence index " + sequence + " not in range of {1,2,3,...} \n" + 
/* 122 */             "  Expected format: 'Vehicle; Day; Sequence number; Order'");
/* 123 */           return;
/*     */         }
/*     */         
/* 126 */         if (order == null)
/*     */         {
/* 128 */           warnings.addWarning("Parsing error in sollution line " + lineNr + ": '" + line + "'.\n" + 
/* 129 */             "  Order index " + orderId + " doesn't exist in the orderfile.\n" + 
/* 130 */             "  Expected format: 'Vehicle; Day; Sequence number; Order'");
/* 131 */           return;
/*     */         }
/*     */         
/* 134 */         if (result[(vehicle - 1)][(day - 1)].put(Integer.valueOf(sequence), order) != null)
/*     */         {
/* 136 */           warnings.addWarning("Parsing error in sollution line " + lineNr + ": '" + line + "'.\n" + 
/* 137 */             "  duplicate sequence number " + sequence + " in file.");
/* 138 */           return;
/*     */         }
/* 140 */         if (orderId != 0)
/*     */         {
/* 142 */           order.orderCountDay[(day - 1)] += 1;
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException nfe)
/*     */       {
/* 147 */         if (lineNr > 1)
/*     */         {
/* 149 */           warnings.addWarning("Parsing error in sollution line " + lineNr + ": '" + line + "'.\n" + 
/* 150 */             "  All fields should contain integers.\n" + 
/* 151 */             "  Expected format: 'Vehicle; Day; Sequence number; Order'");
/* 152 */           return;
/*     */         }
/*     */       }
/*     */       
/* 156 */       lineNr++;
/* 157 */       line = br.readLine();
/*     */     }
/*     */     
/*     */ 
/* 161 */     for (int t = 0; t < 2; t++)
/*     */     {
/* 163 */       TreeMap[] truckResult = result[t];
/* 164 */       for (int d = 0; d < 5; d++)
/*     */       {
/* 166 */         TreeMap<Integer, Order> routeResult = truckResult[d];
/* 167 */         int size = routeResult.size();
/* 168 */         Order[] route = new Order[size];
/* 169 */         this.sollution[t][d] = route;
/*     */         
/* 171 */         int expectedIndex = 1;
/*     */         
/* 173 */         for (Map.Entry<Integer, Order> entry : routeResult.entrySet())
/*     */         {
/* 175 */           int seq = ((Integer)entry.getKey()).intValue();
/* 176 */           Order order = (Order)entry.getValue();
/*     */           
/* 178 */           if (order == null)
/*     */           {
/* 180 */             throw new Error("Sequence number: " + seq + " is null! " + order);
/*     */           }
/*     */           
/* 183 */           if (seq != expectedIndex)
/*     */           {
/* 185 */             warnings.addWarning("Error found in sollution: missing sequence index " + expectedIndex + " of Truck " + (t + 1) + " on " + Order.getDay(d) + ".\n" + 
/* 186 */               "  Next found index: " + seq + "\n" + 
/* 187 */               "  Route must contain upfollowing sequence values starting from 1.");
/* 188 */             return;
/*     */           }
/*     */           
/*     */ 
/* 192 */           route[(seq - 1)] = order;
/*     */           
/*     */ 
/* 195 */           expectedIndex++;
/*     */         }
/*     */         
/* 198 */         if (route == null)
/*     */         {
/* 200 */           throw new Error("How can route be null?");
/*     */         }
/* 202 */         if ((route.length > 0) && (route[(route.length - 1)].id != 0))
/*     */         {
/* 204 */           warnings.addWarning("Error found in sollution: Route of Truck " + (t + 1) + " on " + Order.getDay(d) + " doesn't end at the base.\n" + 
/* 205 */             "  Last order done by any Truck on any Day should be 0, disposing the waste at the base.");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean checkScore(WarningCollector warnings)
/*     */   {
/* 220 */     int locationWasteDisposal = this.problem.getOrder(0).loc;
/*     */     
/* 222 */     boolean feasible = true;
/* 223 */     double travelTime = 0.0D;
/* 224 */     double declinePenalty = 0.0D;
/*     */     
/*     */ 
/* 227 */     warnings.addMessage("> Checking Frequenty:");
/* 228 */     Collection<Order> orders = this.problem.getOrders();
/*     */     
/* 230 */     int accepted = orders.size() - 1;
/* 231 */     int totalOrders = accepted;
/* 232 */     for (Order o : orders)
/*     */     {
/* 234 */       if (o.id != 0)
/*     */       {
/* 236 */         if (!o.isValid(warnings))
/*     */         {
/* 238 */           feasible = false;
/*     */         }
/*     */         
/* 241 */         if (o.declined)
/*     */         {
/* 243 */           declinePenalty += o.freq * o.legingTijd * 3.0D;
/* 244 */           accepted--;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 250 */     warnings.addMessage("\n> Checking Routes:");
/* 251 */     for (int d = 0; d < 5; d++) {
/* 252 */       for (int t = 0; t < 2; t++)
/*     */       {
/* 254 */         double cumTime = 0.0D;
/* 255 */         int cumWaste = 0;
/*     */         
/* 257 */         Order[] route = this.sollution[t][d];
/*     */         
/* 259 */         int lastLocation = locationWasteDisposal;
/*     */         
/* 261 */         int trips = 0;
/*     */         
/* 263 */         warnings.addMessage("[" + (t + 1) + "][" + (d + 1) + "] Route of Truck " + (t + 1) + " on " + Order.getDay(d) + ":");
/*     */         
/*     */ 
/* 266 */         for (int s = 0; s < route.length; s++)
/*     */         {
/* 268 */           Order order = route[s];
/*     */           
/*     */ 
/* 271 */           cumTime += this.problem.getDistance(lastLocation, order.loc);
/* 272 */           lastLocation = order.loc;
/*     */           
/* 274 */           if (order.id == 0)
/*     */           {
/* 276 */             if (cumWaste == 0)
/*     */             {
/* 278 */               if (s == 0)
/*     */               {
/* 280 */                 warnings.addWarning("!!! You don't have to dispose the garbage at the start of the schedule.");
/*     */               }
/*     */               else
/*     */               {
/* 284 */                 warnings.addWarning("!!! Visiting the waste disposal location without carring garbage.");
/*     */               }
/*     */               
/*     */             }
/*     */             else {
/* 289 */               cumTime += 1800.0D;
/*     */             }
/*     */             
/*     */ 
/* 293 */             if (cumWaste > 100000)
/*     */             {
/* 295 */               feasible = false;
/* 296 */               warnings.addWarning("!!! The capacity of Truck " + (t + 1) + " on " + Order.getDay(d) + " is exceeded.");
/*     */             }
/*     */             
/* 299 */             double cumTimeRounded = (int)(cumTime * 10.0D) / 10.0D;
/* 300 */             trips++;
/* 301 */             warnings.addMessage("  Trip " + trips + " - load: " + cumWaste + "/" + 100000 + " l - time: " + cumTimeRounded + "/" + 43200 + " s (" + cumTime / 60.0D + " min)");
/*     */             
/* 303 */             cumWaste = 0;
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 308 */             cumTime += order.legingTijd;
/* 309 */             cumWaste += order.volume;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 314 */         travelTime += cumTime;
/* 315 */         if (cumTime > 43200.0D)
/*     */         {
/* 317 */           feasible = false;
/* 318 */           warnings.addWarning("!!! The maximum service time of Truck " + (t + 1) + " on " + Order.getDay(d) + " is exceeded.");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 323 */     warnings.addMessage("\n> Accepted orders:\t" + accepted + "/" + totalOrders + " (" + (totalOrders - accepted) + " declined)");
/* 324 */     warnings.addMessage("> Traveling time:\t" + travelTime + " s (" + travelTime / 60.0D + " min)");
/* 325 */     warnings.addMessage("> Decline penalty:\t" + declinePenalty + " s (" + declinePenalty / 60.0D + " min)");
/* 326 */     double totalTime = travelTime + declinePenalty;
/* 327 */     warnings.addMessage("> Total score:\t\t" + totalTime + " s (" + totalTime / 60.0D + " min)");
/*     */     
/* 329 */     return feasible;
/*     */   }
/*     */ }


/* Location:              D:\Downloads\Checker.jar!\Validator\Solution.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */