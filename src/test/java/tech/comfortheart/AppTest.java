package tech.comfortheart;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.LinkedList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException {
        String[] headers = {"Hey", "You"};
        List<String[]> data = new LinkedList<>();

        data.add(new String[]{"are you okay", "no, i don't know"});
        data.add(new String[]{"not sure about this", "could you help?"});

        String filePath = "/tmp/test.csv";
        CSVFormat formatter = CSVFormat.DEFAULT;
        FileWriter fileWriter=new FileWriter(filePath);
        try (CSVPrinter printer = new CSVPrinter(fileWriter, formatter)) {
            //写入列头数据
            printer.printRecord(headers);
            if (null != data) {
                //循环写入数据
                int i=0;
                for (String[] lineData : data) {
                    printer.printRecord(lineData);
                    i++;
                    if (i >= 100) {
                        i=0;
                        printer.flush();
                    }
                }

                printer.flush();
                printer.close();
            }
            fileWriter.close();
        }catch (Exception e){

        }

        System.out.println("CSV文件创建成功,文件路径:"+filePath);

    }

}
