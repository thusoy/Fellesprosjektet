package rmi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class PolicyLocator {

    public static final String POLICY_FILE_NAME = "/allow_all.policy";

    public static String getLocationOfPolicyFile() {
        try {
            File tempFile = File.createTempFile("rmi-base", ".policy");
            InputStream is = PolicyLocator.class.getResourceAsStream(POLICY_FILE_NAME);
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            int read = 0;
            while((read = is.read()) != -1) {
                writer.write(read);
            }
            writer.close();
            tempFile.deleteOnExit();
            return tempFile.getAbsolutePath();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}