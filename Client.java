//Written by Ewan Guscott//

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;

public class Client{
  
    //Sets up decode key 
     public static void main(String[] args) {
      SecretKey key;

      if (args.length < 1) {
      System.out.println("Usage: java Client n");
      return;
      }
      try{
      File file = new File("keys/testKey.aes");
      FileInputStream fl = new FileInputStream(file);
      byte[] decodedKey = new byte[(int)file.length()];
      fl.read(decodedKey);
      fl.close();
      key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
    catch(Exception e)
    {
      key = null;
    }
    
    //Gets input from command line
    int n = Integer.parseInt(args[0]);

    //Connects to server and gets selected auction item
    try {
          String name = "Auction";
          Registry registry = LocateRegistry.getRegistry("localhost");
          Auction server = (Auction) registry.lookup(name);

          SealedObject sealedResult = server.getSpec(n);
          Cipher cipher = Cipher.getInstance("AES");
          cipher.init(Cipher.DECRYPT_MODE, key);
          AuctionItem result = (AuctionItem)sealedResult.getObject(cipher);
          System.out.println("Item ID: " + result.itemID + "\nName = " + result.name + "\nDescription = " + result.description + "\nHighest bid = £" + result.highestBid);
    }
    catch (Exception e) {
      System.err.println("Exception:");
      e.printStackTrace();
      }
    }
    
}
