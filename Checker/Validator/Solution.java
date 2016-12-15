 package Validator;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.StringReader;
 import java.util.Collection;
import java.util.Map;
 import java.util.Map.Entry;
 import java.util.TreeMap;
 
 
 public class Solution
 {
   public static final int TruckMaxCapacity = 100000;
   public static final int TruckMaxTime = 43200;
   public static final double disposingTime = 1800.0D;
   public final ProblemData problem;
   public Order[][][] solution;
   
   public Solution(File file, ProblemData problemdata, WarningCollector warnings)
     throws IOException
   {
     BufferedReader br = new BufferedReader(new FileReader(file));
     
     this.problem = problemdata;
     
     parseFile(br, warnings);
   }
   
   public Solution(String text, ProblemData problemdata, WarningCollector warnings)
   {
     BufferedReader br = new BufferedReader(new StringReader(text));
     this.problem = problemdata;
     
     try
     {
       parseFile(br, warnings);
     }
     catch (IOException e)
     {
       e.printStackTrace();
     }
   }
   
 
 
 
 
 
 
 
   private void parseFile(BufferedReader br, WarningCollector warnings)
     throws IOException
   {
     this.problem.initChecking();
     
     TreeMap[][] result = new TreeMap[2][5];
     this.solution = new Order[2][5][];
     
     for (int t = 0; t < 2; t++) {
       for (int d = 0; d < 5; d++)
       {
         result[t][d] = new TreeMap();
       }
     }
     int lineNr = 1;
     
 
 
     String line = br.readLine();
     
     while (line != null)
     {
       String[] data = line.split(";");
       
       if (data.length != 4)
       {
         if ((line.equals("")) && (br.readLine() == null)) {
           break;
         }
         
 
 
         warnings.addWarning("Parsing error in solution line " + lineNr + ": '" + line + "'.\n" + 
           "  expected format: 'Vehicle; Day; Sequence number; Order'");
         return;
       }
       
 
 
 
       try
       {
         int vehicle = Integer.parseInt(data[0].trim());
         int day = Integer.parseInt(data[1].trim());
         int sequence = Integer.parseInt(data[2].trim());
         int orderId = Integer.parseInt(data[3].trim());
         
         Order order = this.problem.getOrder(orderId);
         
         if ((1 > vehicle) || (vehicle > 2))
         {
           warnings.addWarning("Parsing error in solution line " + lineNr + ": '" + line + "'.\n" + 
             "  Vehicle index " + vehicle + " not in range of {1,2} \n" + 
             "  Expected format: 'Vehicle; Day; Sequence number; Order'");
           return;
         }
         
         if ((1 > day) || (day > 5))
         {
           warnings.addWarning("Parsing error in solution line " + lineNr + ": '" + line + "'.\n" + 
             "  Day index " + day + " not in range of {1,2,3,4,5} \n" + 
             "  Expected format: 'Vehicle; Day; Sequence number; Order'");
           return;
         }
         
         if (sequence < 1)
         {
           warnings.addWarning("Parsing error in solution line " + lineNr + ": '" + line + "'.\n" + 
             "  Sequence index " + sequence + " not in range of {1,2,3,...} \n" + 
             "  Expected format: 'Vehicle; Day; Sequence number; Order'");
           return;
         }
         
         if (order == null)
         {
           warnings.addWarning("Parsing error in solution line " + lineNr + ": '" + line + "'.\n" + 
             "  Order index " + orderId + " doesn't exist in the orderfile.\n" + 
             "  Expected format: 'Vehicle; Day; Sequence number; Order'");
           return;
         }
         
         if (result[(vehicle - 1)][(day - 1)].put(Integer.valueOf(sequence), order) != null)
         {
           warnings.addWarning("Parsing error in solution line " + lineNr + ": '" + line + "'.\n" + 
             "  duplicate sequence number " + sequence + " in file.");
           return;
         }
         if (orderId != 0)
         {
           order.orderCountDay[(day - 1)] += 1;
         }
       }
       catch (NumberFormatException nfe)
       {
         if (lineNr > 1)
         {
           warnings.addWarning("Parsing error in solution line " + lineNr + ": '" + line + "'.\n" + 
             "  All fields should contain integers.\n" + 
             "  Expected format: 'Vehicle; Day; Sequence number; Order'");
           return;
         }
       }
       
       lineNr++;
       line = br.readLine();
     }
     
 
     for (int t = 0; t < 2; t++)
     {
       TreeMap[] truckResult = result[t];
       for (int d = 0; d < 5; d++)
       {
         TreeMap<Integer, Order> routeResult = truckResult[d];
         int size = routeResult.size();
         Order[] route = new Order[size];
         this.solution[t][d] = route;
         
         int expectedIndex = 1;
         
         for (Map.Entry<Integer, Order> entry : routeResult.entrySet())
         {
           int seq = ((Integer)entry.getKey()).intValue();
           Order order = (Order)entry.getValue();
           
           if (order == null)
           {
             throw new Error("Sequence number: " + seq + " is null! " + order);
           }
           
           if (seq != expectedIndex)
           {
             warnings.addWarning("Error found in solution: missing sequence index " + expectedIndex + " of Truck " + (t + 1) + " on " + Order.getDay(d) + ".\n" + 
               "  Next found index: " + seq + "\n" + 
               "  Route must contain upfollowing sequence values starting from 1.");
             return;
           }
           
 
           route[(seq - 1)] = order;
           
 
           expectedIndex++;
         }
         
         if (route == null)
         {
           throw new Error("How can route be null?");
         }
         if ((route.length > 0) && (route[(route.length - 1)].id != 0))
         {
           warnings.addWarning("Error found in solution: Route of Truck " + (t + 1) + " on " + Order.getDay(d) + " doesn't end at the base.\n" + 
             "  Last order done by any Truck on any Day should be 0, disposing the waste at the base.");
         }
       }
     }
   }
   
 
 
 
 
 
 
 
   public boolean checkScore(WarningCollector warnings)
   {
     int locationWasteDisposal = this.problem.getOrder(0).loc;
     
     boolean feasible = true;
     double travelTime = 0.0D;
     double declinePenalty = 0.0D;
     
 
     warnings.addMessage("> Checking Frequency:");
     Collection<Order> orders = this.problem.getOrders();
     
     int accepted = orders.size() - 1;
     int totalOrders = accepted;
     for (Order o : orders)
     {
       if (o.id != 0)
       {
         if (!o.isValid(warnings))
         {
           feasible = false;
         }
         
         if (o.declined)
         {
           declinePenalty += o.freq * o.legingTijd * 3.0D;
           accepted--;
         }
       }
     }
     
 
     warnings.addMessage("\n> Checking Routes:");
     for (int d = 0; d < 5; d++) {
       for (int t = 0; t < 2; t++)
       {
         double cumTime = 0.0D;
         int cumWaste = 0;
         
         Order[] route = this.solution[t][d];
         
         int lastLocation = locationWasteDisposal;
         
         int trips = 0;
         
         warnings.addMessage("[" + (t + 1) + "][" + (d + 1) + "] Route of Truck " + (t + 1) + " on " + Order.getDay(d) + ":");
         
 
         for (int s = 0; s < route.length; s++)
         {
           Order order = route[s];
           
 
           cumTime += this.problem.getDistance(lastLocation, order.loc);
           lastLocation = order.loc;
           
           if (order.id == 0)
           {
             if (cumWaste == 0)
             {
               if (s == 0)
               {
                 warnings.addWarning("!!! You don't have to dispose the garbage at the start of the schedule.");
               }
               else
               {
                 warnings.addWarning("!!! Visiting the waste disposal location without carring garbage.");
               }
               
             }
             else {
               cumTime += 1800.0D;
             }
             
 
             if (cumWaste > 100000)
             {
               feasible = false;
               warnings.addWarning("!!! The capacity of Truck " + (t + 1) + " on " + Order.getDay(d) + " is exceeded.");
             }
             
             double cumTimeRounded = (int)(cumTime * 10.0D) / 10.0D;
             trips++;
             warnings.addMessage("  Trip " + trips + " - load: " + cumWaste + "/" + 100000 + " l - time: " + cumTimeRounded + "/" + 43200 + " s (" + cumTime / 60.0D + " min)");
             
             cumWaste = 0;
 
           }
           else
           {
             cumTime += order.legingTijd;
             cumWaste += order.volume;
           }
         }
         
 
         travelTime += cumTime;
         if (cumTime > 43200.0D)
         {
           feasible = false;
           warnings.addWarning("!!! The maximum service time of Truck " + (t + 1) + " on " + Order.getDay(d) + " is exceeded.");
         }
       }
     }
     
     warnings.addMessage("\n> Accepted orders:\t" + accepted + "/" + totalOrders + " (" + (totalOrders - accepted) + " declined)");
     warnings.addMessage("> Traveling time:\t" + travelTime + " s (" + travelTime / 60.0D + " min)");
     warnings.addMessage("> Decline penalty:\t" + declinePenalty + " s (" + declinePenalty / 60.0D + " min)");
     double totalTime = travelTime + declinePenalty;
     warnings.addMessage("> Total score:\t\t" + totalTime + " s (" + totalTime / 60.0D + " min)");
     
     return feasible;
   }
 }


