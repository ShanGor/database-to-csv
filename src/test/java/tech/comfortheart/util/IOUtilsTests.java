package tech.comfortheart.util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class IOUtilsTests {

    @Test
    public void deleteFolder() throws IOException {
        File file = new File("/tmp/sam");

        IOUtils.removeFileOrDir(file.getAbsolutePath());
    }

}
