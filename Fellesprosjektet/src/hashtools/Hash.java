package hashtools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	private static final int rounds = 70000;
	
	public static String SHA512(String rawtext){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Your stupid! Select a valid hash algorithm!");
		}
		byte[] bytes = rawtext.getBytes();
		int localrounds = rounds;
		while (localrounds > 0){
			md.reset();
			md.update(bytes);
			bytes = md.digest();
			localrounds--;
		}
		return convertToHex(bytes);
	}
	
	private static String convertToHex (byte[] data){
		StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)){
                	buf.append((char) ('0' + halfbyte));
                } else { 
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }
	
	public static String createHash(String password, String salt){
		return SHA512(password + salt);
	}
}