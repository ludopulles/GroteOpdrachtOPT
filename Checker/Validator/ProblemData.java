 package Validator;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.util.Collection;
 import java.util.TreeMap;
 
 
 
 public class ProblemData
 {
   TreeMap<Integer, Order> orders = new TreeMap();
   
 
 
   int[][] matrix;
   
 
 
 
   public ProblemData(String orderFile, String distanceFile)
     throws IOException
   {
     this.orders = ProblemReader.reader.readOrders(new BufferedReader(new FileReader(new File(orderFile))));
     this.matrix = ProblemReader.reader.readDistances(new BufferedReader(new FileReader(new File(orderFile))));
   }
   
   public ProblemData(TreeMap<Integer, Order> orders, int[][] matrix)
   {
     this.orders = orders;
     this.matrix = matrix;
   }
   
 
 
 
 
 
 
   public Order getOrder(int o)
   {
     return (Order)this.orders.get(Integer.valueOf(o));
   }
   
 
 
 
 
   public Collection<Order> getOrders()
   {
     return this.orders.values();
   }
   
 
 
 
 
 
 
 
   public int getDistance(int loc1, int loc2)
   {
     return this.matrix[loc1][loc2];
   }
   
 
 
 
   public void initChecking()
   {
     for (Order o : this.orders.values())
     {
       o.initChecking();
     }
   }
 }


