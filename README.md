# Whatsapp Messages Decryption 
Tool to break down Whatsapp crypt12 encryption
# Requirements:
Linux Or Mac OSX or Windows runs java. 
Tested on Ubuntu Linux 16.04.
OpenJDK version of Java.


# How it works
Clone the repo. 

$ git clone https://github.com/JameelNabbo/WhatsappDecryption.git

$ cd WhatsappDecryption

Compile WTDecrypt.java.  

$ javac -classpath "lib/whatsapp_spongycastle.jar:." WTDecrypt.java

Copy key and msgstore.db.WTDecrypt files to the same repo directory 

$ cp /path/to/file/key .

$ cp /path/to/file/msgstore.db.crypt12 .

Command execution:  

java -cp "lib/whatsapp_spongycastle.jar:." WTDecrypt

For more information visit: http://jameelnabbo.com/breaking-whatsapp-encryption-exploit/
