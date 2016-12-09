import java.security.*;
import org.spongycastle.jce.provider.*;
import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.zip.*;
import java.util.*;


/**
 * WhatsApp decryption tool - http://jameelnabbo.com/breaking-whatsapp-encryption-exploit/
 */
public class crypt12 {
    public static void main(String[] args) {
        String cryptedFile    = "msgstore.db.crypt12";      // The input file
        String cryptedFileEnc = "msgstore.db.crypt12.enc";  // encrypted file with header and footer stripped
        String decryptedZlibFile = "msgstore.db.zlib";      // zlib output file
        String decryptedDbFile = "msgstore.db";             // sqlite3 db output file
        String keyFile = "key";
        
        byte[] keyData = new byte[158];
        byte[] aesK = new byte[32];
        byte[] aesIV = new byte[16];
        byte[] encryptedHeader = new byte[67];
        byte[] buffer = new byte[8192];
        int read;
        
        BufferedInputStream is;
        RandomAccessFile raf;
        FileOutputStream os;
        CipherInputStream isCipher;
        Cipher cipher;        
        
        try {        
            Security.insertProviderAt((Provider)new BouncyCastleProvider(), 1);
        
            System.out.format("%n");
            System.out.format("whatsapp crypt12 decryption tool - http://jameelnabbo.com/breaking-whatsapp-encryption-exploit");
            
            // get Key
            is = new BufferedInputStream(new FileInputStream(keyFile));
            is.read(keyData);
            System.arraycopy(keyData, 126, aesK, 0, 32);
            is.close();
            
            // get IV
            is = new BufferedInputStream(new FileInputStream(cryptedFile));
            is.read(encryptedHeader);
            System.arraycopy(encryptedHeader, 51, aesIV, 0, 16); 
            is.close();
            
            //System.out.println("K:" +Arrays.toString(aesK));
            //System.out.println("IV:"+Arrays.toString(aesIV));
            System.out.println("K:" +ListToHex(aesK));
            System.out.println("IV:"+ListToHex(aesIV));
            
            // create enc file by stripping header and footer
            System.out.format("creating encrypted file with header/footer stripped: %s%n",cryptedFileEnc);
            is = new BufferedInputStream(new FileInputStream(cryptedFile));  // read msgstore.db.crypt12
            is.skip(67);    // 67 byte header
            int available = is.available();                
            raf = new RandomAccessFile(new File(cryptedFileEnc), "rw");
            
            while((read=is.read(buffer))!=-1) {
                raf.write(buffer, 0, read);                
            }
            raf.setLength(available - 20);  // 20 byte footer
            raf.close();
                 
        
            // create zlib output file                    
            System.out.format("creating zlib output file: %s%n",decryptedZlibFile);
            
            is = new BufferedInputStream(new FileInputStream(cryptedFileEnc));                                                                                 
            cipher = Cipher.getInstance("AES/CBC/NoPadding", "SC");            
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesK, "AES"), new IvParameterSpec(aesIV));            
            isCipher = new CipherInputStream(is, cipher);
            os = new FileOutputStream(decryptedZlibFile);
            
            while((read=isCipher.read(buffer))!=-1) {            
		        os.write(buffer, 0, read);             
		    }
            
            os.close();
            is.close(); 
                    
        
            // create sqlite3 db output file                                
            System.out.format("creating sqlite3 output file: %s%n",decryptedDbFile);
            
            is = new BufferedInputStream(new FileInputStream(cryptedFileEnc));            
            cipher = Cipher.getInstance("AES/CBC/NoPadding", "SC");            
            cipher.init(2, new SecretKeySpec(aesK, "AES"), new IvParameterSpec(aesIV));
            isCipher = new CipherInputStream(is, cipher);
            InflaterInputStream isInflater = new InflaterInputStream(isCipher, new Inflater(false));
            os = new FileOutputStream(decryptedDbFile);
            
            while((read=isInflater.read(buffer))!=-1) {
                os.write(buffer, 0, read);
            }
            
            os.close();
            is.close();          
        }
        catch (Exception e) {
            System.err.println("exception:" + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.format("%n");
    }
    
    
    // from iglogger - https://github.com/intrepidusgroup/IGLogger/blob/master/iglogger.java
    // to dump K and IV to screen as hex strings
    public static String ListToHex(byte[] data){
		String string = "";
        for (int i=0;i<data.length;i++) {
			byte b = data[i];
			StringBuffer s = new StringBuffer(Integer.toHexString((b >= 0) ? b : 256 + b));
			if(s.length() == 1) s.insert(0,'0');
			string = string + s;
        }
        return string;
    }
}