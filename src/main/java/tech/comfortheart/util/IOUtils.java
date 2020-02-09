package tech.comfortheart.util;

import java.io.*;
import java.nio.file.Files;

public class IOUtils {
    public static final void removeFileOrDir(final String path) throws IOException {
        removeFileOrDir(new File(path));
    }

    public static final void removeFileOrDir(final File path) throws IOException {
        if (path.isDirectory()) {
            for (File file: path.listFiles()) {
                removeFileOrDir(file);
            }
        }
        Files.delete(path.toPath());
    }

    /**
     * Read String from STDIN.
     * @param prompt
     * @return
     * @throws IOException
     */
    public static String readStandardInput(String prompt) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.print(prompt);
        System.out.flush();
        return in.readLine();
    }
}
