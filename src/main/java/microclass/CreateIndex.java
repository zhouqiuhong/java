package microclass;

import org.ansj.lucene7.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.nio.file.Paths;

public class CreateIndex {
    private IndexWriter indexWriter;

    public void close() throws Exception {
        indexWriter.close();
    }
    public void delete()throws Exception{
        indexWriter.deleteAll();
    }
    public int getDocument(String filePath, String fieldName) throws Exception {
        File xlsFile = new File(filePath);
        Workbook workbook = WorkbookFactory.create(xlsFile);
        Sheet sheet = workbook.getSheetAt(0);
        int rowCount = sheet.getLastRowNum() + 1;
        for (int row = 1; row < rowCount; row++) {
            Row currentRow = sheet.getRow(row);
            Document document = new Document();
            String content_name = currentRow.getCell(1).getStringCellValue();
            String semester = currentRow.getCell(3).getStringCellValue();
            String subject = currentRow.getCell(4).getStringCellValue();
            String grade = currentRow.getCell(5).getStringCellValue();
            grade = grade.replaceAll("(小学|初中|高中)", "");
            document.add(new TextField(fieldName, content_name, Field.Store.YES));
            document.add(new StringField("semesters", semester, Field.Store.YES));
            document.add(new StringField("subjects", subject, Field.Store.YES));
            document.add(new StringField("grades", grade, Field.Store.YES));
            indexWriter.addDocument(document);
            System.out.println(content_name + " " + semester + " " + subject + " " + grade);
        }
        return indexWriter.numDocs();
    }

    public CreateIndex(String indexDir) throws Exception {
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.dic_ansj, "dic");
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriter = new IndexWriter(directory, indexWriterConfig);
        indexWriter.deleteAll(); // 删除所有索引
    }

    public static void main(String[] args) throws Exception {
        String fieldName = "content";
        String indexDir = "D:\\test\\index";
        String xlsPath = "C:\\Users\\Administrator\\Desktop\\语音调用关键字 180914 知识点微课-课时目录表.xlsx";
        long start = System.currentTimeMillis();
        int numIndexed ;
        CreateIndex createIndex = new CreateIndex(indexDir);
        numIndexed = createIndex.getDocument(xlsPath, fieldName);
        createIndex.close();
        long end = System.currentTimeMillis();
        System.out.println("索引：" + numIndexed + " 个文件 花费了" + (end - start) + " 毫秒");
    }

}
