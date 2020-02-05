package tech.comfortheart.util;

import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class CsvWriterTests
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
        CsvWriter csvWriter = new CsvWriter(filePath, headers);
        try  {
            csvWriter.writeRecords(data);

            System.out.println("CSV文件创建成功,文件路径:"+filePath);
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            csvWriter.close();
        }


    }

}
