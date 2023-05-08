import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Salsa20 salsa20 = new Salsa20();
        Scanner scanner = new Scanner(System.in);

        int keyLength;
        System.out.print("Enter an original message m using only ascii characters to encode it in Salsa20: ");
        String m = scanner.nextLine();
//        System.out.println("Do you wish to encrypt with a 16 or 32 byte key? Please enter 16 for 16 byte key and 32 for 32 byte key");
//        while (true) {
//            keyLength = Integer.parseInt(scanner.nextLine());
//            if(keyLength == 16 || keyLength == 32) {
//                break;
//            } else {
//                System.out.println("Incorrect key length. Please try again.");
//                System.out.println("Please enter 16 for 16 byte key and 32 for 32 byte key");
//            }
//        }
//        Byte[] key = new Byte[keyLength];
//        Byte[] v = new Byte[8];
//        System.out.println("Please enter your key with length " + keyLength +
//                ". Please do so one value at a time, and remember since your key has byte values," +
//                " any individual position can have a value from -128 to 127 inclusively"); // values are internally treated as unsigned bytes 0 to 255. But that doesn't matter to the user
//        for(int i = 0; i < keyLength; i++) {
//            System.out.print("key[" + i + "] = ");
//            try {
//                key[i] = Byte.parseByte(scanner.nextLine());
//            } catch (NumberFormatException e) {
//                System.out.println("Number out of bounds. Please try again.");
//                i--;
//            }
//
//        }
//
//        System.out.println("Please enter an 8 byte nonce. Same rules as the key apply.");
//        for(int i = 0; i < 8; i++) {
//            System.out.print("nonce[" + i + "] = ");
//            try {
//                v[i] = Byte.parseByte(scanner.nextLine());
//            } catch (NumberFormatException e) {
//                System.out.println("Number out of bounds. Please try again.");
//                i--;
//            }
//        }
        Byte[] key = {11, 15, -4, 127, -104, 65, 78, 12, 7, 0, 95, -78, 12, -3, 46, 42, 21, 0, 15, -107, -10, -65, 74, 2, 7, 100, 9, 61, -12, 36, 6, 2}; // temp for testing. Should be removed and replaced with code above when done
        Byte[] v = {-12, 0, 7, 3, -73, -62, 17, 51}; // temp for testing. Should be removed and replaced with code above when done
        // converting m to Byte array for internal calculation
        Byte[] mByteArray = new Byte[m.length()];
        byte[] mArray = m.getBytes(StandardCharsets.US_ASCII);
        for(int i = 0; i < m.length(); i++) {
            mByteArray[i] = mArray[i];
        }
        salsa20.salsa20EncryptionFunction(key,v,mByteArray);
        String c = salsa20.encryptedMessageAsString();

        System.out.println("Original message: " + m);
        System.out.println("Encrypted message: " + c);
        System.out.println("Now to decrypt the message we simply send it through Salsa20 again," +
                " but this time the input is out cypher text.\nThe symmetrical nature of Salsa20, means that the output this time should be " +
                "our original message. We shall demonstrate this now.");
        // converting c to char array for easier handling and then converting that char array to a Byte array
        // reusing the same variables as before so as to not waste additional memory

        salsa20.salsa20EncryptionFunction(key,v,salsa20.getEncryptedMessage());
        String decryptedMessage = salsa20.encryptedMessageAsString();
        System.out.println("Encrypted message: " + c);
        System.out.println("Decrypted message: " + decryptedMessage);
    }
}
