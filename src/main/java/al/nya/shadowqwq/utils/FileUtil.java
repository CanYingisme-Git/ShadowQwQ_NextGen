package al.nya.shadowqwq.utils;

import java.io.*;

public class FileUtil {
    public static void writeFile(File file,byte[] bytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
    }
    public static byte[] readFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[fis.available()];
        fis.read(bytes);
        return bytes;
    }
}
