import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class Salsa20 {
//    private static long MOD = 4294967296L;
    private Byte[] secretKey;
    private Byte[] encryptedMessage;

    public Salsa20() {
        secretKey = new Byte[64];
    }

    public String encryptedMessageAsString() {
        byte[] encryptedMessageBytes = new byte[encryptedMessage.length];
        BigInteger iAsBigInteger = new BigInteger(String.valueOf(0));             // as stated before the use of BigInteger is for the purposes of providing
        for (Byte B:encryptedMessage) {                                             // a theoretical message length of 2^70 - 1. But because of the nature of arrays
            encryptedMessageBytes[Integer.parseInt(iAsBigInteger.toString())] = B;  // and Java as a language, this is almost certainly impossible in practice
            iAsBigInteger = iAsBigInteger.add(new BigInteger("1"));
        }

        try {
            String encryptedMessageString = new String(encryptedMessageBytes, "ISO-8859-1");
            return encryptedMessageString;
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }
    public Byte[] getEncryptedMessage() { //should really be private but i need it for demonstration purposes
        return encryptedMessage;
    }

    public void salsa20EncryptionFunction (Byte[] k, Byte[] v, Byte[] originalMessage) {
        if(v.length == 8 && (k.length == 16 || k.length == 32) && originalMessage.length > 0) {
            encryptedMessage = new Byte[originalMessage.length];
            boolean flag = false;
            int i = 0;
            Byte[] nonceSecondPart = {0, 0, 0, 0, 0, 0, 0, 0};
            BigInteger iAsBigInteger = new BigInteger("0"); // use of BigInteger was necessary because no other class or primitive type could hold values as high as 2^70
            BigInteger maxLength = new BigInteger("1180591620717411303424"); // maxLength == 2^70 the max length of a message that can be processed
            String length = new String(String.valueOf(originalMessage.length));
            BigInteger originalMessageLength = new BigInteger(length);

            while(true) {
                if(iAsBigInteger.compareTo(maxLength) >= 0) {
                    break;
                } else if (iAsBigInteger.compareTo(originalMessageLength) >= 0){
                    break;
                }
                Byte[] nonce = new Byte[16];
                for(int j = 0; j < 16; j++) {
                    if(j < 8) {
                        nonce[j] = v[j];
                    } else {
                        nonce[j] = nonceSecondPart[j - 8];
                    }
                }
                nonceSecondPart[7]++; // this complicated if else statement is meant to gradually increase each value of the second part of the nonce
                if(nonceSecondPart[7].equals(0)) {
                    nonceSecondPart[6]++;
                    if(nonceSecondPart[6].equals(0)) {
                        nonceSecondPart[5]++;
                        if (nonceSecondPart[5].equals(0)) {
                            nonceSecondPart[4]++;
                            if (nonceSecondPart[4].equals(0)) {
                                nonceSecondPart[3]++;
                                if (nonceSecondPart[3].equals(0)) {
                                    nonceSecondPart[2]++;
                                    if (nonceSecondPart[2].equals(0)) {
                                        nonceSecondPart[1]++;
                                        if (nonceSecondPart[1].equals(0)) {
                                            nonceSecondPart[0]++;
                                            if (nonceSecondPart[0].equals(0)) { // once the last 8 bytes have become 0 again that means
                                                break;                         // we've exhausted all the possible nonces, so the encryption
                                            }                                  // should stop to avoid repetition. that's why I added a break in the last if statement
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                salsa20ExpansionFunction(k,nonce);

                for(int j = 0; j < 64; j++) {
                    BigInteger sumOfIandJ = new BigInteger("0");
                    sumOfIandJ = sumOfIandJ.add(iAsBigInteger);
                    String jAsString = String.valueOf(j);
                    sumOfIandJ = sumOfIandJ.add(new BigInteger(jAsString));
                    if(sumOfIandJ.compareTo(originalMessageLength) < 0) {
                        encryptedMessage[i+j] = xor(secretKey[j], originalMessage[i+j]);
                    } else {
                        flag = true;
                        break;
                    }
                }

                if(flag) {
                    break;
                }
                i+=64;
                iAsBigInteger = iAsBigInteger.add(new BigInteger("64"));
            }
        } else {
            return;
        }
    }

    private void salsa20ExpansionFunction (Byte[] k, Byte[] n) {
        if(k.length == 16) {
            Byte[] tau_0 = {101, 120, 112, 97};
            Byte[] tau_1 = {110, 100, 32, 49};
            Byte[] tau_2 = {54, 45, 98, 121};
            Byte[] tau_3 = {116, 101, 32, 107};

            for(int i = 0; i < 64; i++) { // i'm sure there's an easier way. but this is what i came up with
                if(i < 4) {
                    secretKey[i] = tau_0[i];
                } else if (i < 20) {
                    secretKey[i] = k[i-4];
                } else if (i < 24) {
                    secretKey[i] = tau_1[i-20];
                } else if (i < 40) {
                    secretKey[i] = n[i-24];
                } else if (i < 44) {
                    secretKey[i] = tau_2[i-40];
                } else if (i < 60) {
                    secretKey[i] = k[i-44];
                } else {
                    secretKey[i] = tau_3[i-60];
                }
            }
        } else if (k.length == 32) {
            Byte[] sigma_0 = {101, 120, 112, 97};
            Byte[] sigma_1 = {110, 100, 32, 51};
            Byte[] sigma_2 = {50, 45, 98, 121};
            Byte[] sigma_3 = {116, 101, 32, 107};

            for(int i = 0; i < 64; i++) { // i'm sure there's an easier way. but this is what i came up with
                if(i < 4) {
                    secretKey[i] = sigma_0[i];
                } else if (i < 20) {
                    secretKey[i] = k[i-4];
                } else if (i < 24) {
                    secretKey[i] = sigma_1[i-20];
                } else if (i < 40) {
                    secretKey[i] = n[i-24];
                } else if (i < 44) {
                    secretKey[i] = sigma_2[i-40];
                } else if (i < 60) {
                    secretKey[i] = k[i-28];
                } else {
                    secretKey[i] = sigma_3[i-60];
                }
            }
        } else {
            secretKey = null;
            return;
        }

        salsa20HashFunction(secretKey);
    }

    private void salsa20HashFunction(Byte[] x) {
        if(x.length != 64) {
            return;
        }
        Integer[] y = new Integer[16];
        Integer[] z = new Integer[16];
        for(int i = 0; i < 16; i++) {
            Byte[] subSequence = {x[0+4*i],x[1+4*i],x[2+4*i],x[3+4*i]};
            y[i] = littleEndian(subSequence);
            z[i] = y[i];
        }
        for (int i = 0; i < 10; i++) {
            doubleRound(z);
        }

        for(int i = 0; i < 16; i++) {
            z[i] = sum(z[i], y[i]);
        }

        for(int i = 0; i < 16; i++) {
            Byte[] subSequence = inverseLittleEndian(z[i]);
            for(int j = 0; j < 4; j++) {
                x[j+4*i] = subSequence[j];
            }
        }
    }

    private Byte[] inverseLittleEndian(Integer y) {
        Byte[] result = new Byte[4];
        result[0] = (byte) (y % Math.pow(2,8));
        result[1] = (byte) ((y / Math.pow(2,8)) % Math.pow(2,8));
        result[2] = (byte) ((y / Math.pow(2,16)) % Math.pow(2,8));
        result[3] = (byte) (y / Math.pow(2,24));
        return result;
    }
    private Integer littleEndian(Byte[] b) {
        //Integer[] bAsInts = {Byte.toUnsignedInt(b[0]),Byte.toUnsignedInt(b[1]),Byte.toUnsignedInt(b[2]),Byte.toUnsignedInt(b[3])};
        Integer result = 0;
        result += b[0]; //bAsInts[0]
        result += (int) Math.pow(2,8)*b[1];
        result += (int) Math.pow(2,16)*b[2];
        result += (int) Math.pow(2,24)*b[3];
        return result;
    }
    public void doubleRound(Integer[] x) {
        columnRound(x);
        rowRound(x);
    }
    private void columnRound(Integer[] x) {
        Integer[] y = {x[0], x[4], x[8], x[12], x[1], x[5], x[9], x[13], x[2], x[6], x[10], x[14], x[3], x[7], x[11], x[15]};
        rowRound(y);
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                x[i+4*j] = y[j+4*i];
            }
        }
    }

    private void rowRound(Integer[] y) {
        Integer[] tmp_0 = {y[0], y[1], y[2], y[3]};
        Integer[] tmp_1 = {y[5], y[6], y[7], y[4]};
        Integer[] tmp_2 = {y[10], y[11], y[8], y[9]};
        Integer[] tmp_3 = {y[15], y[12], y[13], y[14]};

        quarterRound(tmp_0);
        quarterRound(tmp_1);
        quarterRound(tmp_2);
        quarterRound(tmp_3);

        /* zeroth row */
        y[0] = tmp_0[0];
        y[1] = tmp_0[1];
        y[2] = tmp_0[2];
        y[3] = tmp_0[3];
        /* first row */
        y[4] = tmp_1[3];
        y[5] = tmp_1[0];
        y[6] = tmp_1[1];
        y[7] = tmp_1[2];
        /* second row */
        y[8] = tmp_2[2];
        y[9] = tmp_2[3];
        y[10] = tmp_2[0];
        y[11] = tmp_2[1];
        /* third row */
        y[12] = tmp_3[1];
        y[13] = tmp_3[2];
        y[14] = tmp_3[3];
        y[15] = tmp_3[0];
    }
    private void quarterRound(Integer[] y) {
        y[1] = xor(y[1],Integer.rotateLeft(new Integer(sum(y[0],y[3])),7));
        y[2] = xor(y[2],Integer.rotateLeft(new Integer(sum(y[1],y[0])),9));
        y[3] = xor(y[3],Integer.rotateLeft(new Integer(sum(y[2],y[1])),13));
        y[0] = xor(y[0],Integer.rotateLeft(new Integer(sum(y[3],y[2])),18));
    }

    private Integer sum(Integer x, Integer y) {
        return new Integer((x + y)); // no need for mod even though i originally implemented it, in computers the addition operation of 2 32 bit integers is by definition addition in GF(2^32)
    }                                // this means that overflow will naturally insure that the largest value we get is 2^32 - 1.
                                     // (because we don't care about the sign, otherwise it would be between -2^31 and 2^31 - 1 )
    private Integer xor(Integer x, Integer y) {
        return new Integer(x ^ y); // no need for mod, because using bitwise xor will never create a number bigger than the numbers entered
    }

    private Byte xor(Byte x, Byte y) {
        byte result = (byte) ((x.byteValue())^(y.byteValue()));
        return new Byte(result); // no need for mod, because using bitwise xor will never create a number bigger than the numbers entered
    }
}
