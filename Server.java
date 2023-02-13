//Written by Ewan Guscott//

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import java.io.*;
import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.io.FileWriter;
import java.util.Base64;

public class Server implements Auction, Serializable{

    //Sets up starting items
    AuctionItem item1 = new AuctionItem();
    AuctionItem item2 = new AuctionItem();
    AuctionItem item3 = new AuctionItem();
    AuctionItem item4 = new AuctionItem();
    AuctionItem[] items = new AuctionItem[4];

    KeyGenerator keyGenerator;
    SecretKey key;
    

    public Server() {
        //Defines items
        super();
        item1.itemID = 1;
        item1.name = "Steel Samurai Limited Edition Gold Figurine";
        item1.description = "Released as a set of 20 at the 10 year anniversary of the first episode of Steel Samurai. Gold plated.";
        item1.highestBid = 5400;

        item2.itemID = 2;
        item2.name = "Ace Attorney Investigations: Miles Edgeworth (DS)";
        item2.description = "Working copy for DS. Used";
        item2.highestBid = 70;

        item3.itemID = 3;
        item3.name = "Klavier Gavin Autograph";
        item3.description = "Klavier Gavin autograph on poster released with single 'Guilty Love'";
        item3.highestBid = 700;

        item4.itemID = 4;
        item4.name = "Reaper of the Bailey's Autobiography";
        item4.description = "A biography written by Barok van Zieks. Brand new, never opened";
        item4.highestBid = 10;

        items[0] = item1;
        items[1] = item2;
        items[2] = item3;
        items[3] = item4;

        //Gets instance of key generator
        try{
            keyGenerator = KeyGenerator.getInstance("AES");
        }
        catch(NoSuchAlgorithmException e)
        {
            System.out.println("Error");
        };
            
        //Generates key
        keyGenerator.init(256);
        key = keyGenerator.generateKey();

        //Encodes key and writes to file
        try{
            byte[] byteKey = key.getEncoded();
            File keyfile = new File("keys/testKey.aes");
            try (FileOutputStream outputStream = new FileOutputStream(keyfile)) {
                outputStream.write(byteKey);
            }
        }
        catch(Exception e)
        {
        };


        

    }

    //Returns encrypted auction item
    public SealedObject getSpec(int itemID)
    {
        for(int i = 0; i < items.length; i++)
        {
            if(items[i].itemID == itemID)
            {
                try{
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.ENCRYPT_MODE, key);
                    SealedObject returnObject = new SealedObject(items[i], cipher);
                    return returnObject;
                }
                catch(Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    //Sets up server
    public static void main(String[] args) {
        try{
            Server s = new Server();
            String name = "Auction";
            Auction stub = (Auction) UnicastRemoteObject.exportObject(s, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
}
}
