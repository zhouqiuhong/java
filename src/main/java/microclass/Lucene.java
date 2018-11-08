package microclass;

import org.ansj.domain.Result;
import org.ansj.lucene7.AnsjAnalyzer;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.nio.file.Paths;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Lucene {
    private IndexWriter indexWriter;
    private Document document;
    private Directory directory;
    private String indexDir;

    private Lucene(String indexDir) throws Exception{
        this.indexDir = indexDir;
        document = new Document();
        directory = FSDirectory.open(Paths.get(this.indexDir));
        Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.dic_ansj, "dic");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        indexWriter = new IndexWriter(directory, config);
        //indexWriter.deleteAll();
    }

    public void close() throws Exception{
        indexWriter.close();
    }
    public void delete()throws Exception{
        indexWriter.deleteAll();
    }
    public void update(String keyWord)throws Exception{
        indexWriter.updateDocument(new Term("content", keyWord), document);
    }
    public int createIndexer(String filePath) throws Exception{
        File file = new File(filePath);
        Workbook book = WorkbookFactory.create(file);
        Sheet sheet = book.getSheetAt(0);
        int rowCount = sheet.getLastRowNum() + 1;
        for(int row=1; row<rowCount; row++){
            Row currentRow = sheet.getRow(row);
            Document document = new Document();
            String content_name = currentRow.getCell(1).getStringCellValue();
            String semester = currentRow.getCell(3).getStringCellValue();
            String subject = currentRow.getCell(4).getStringCellValue();
            String grade = currentRow.getCell(5).getStringCellValue();
            grade = grade.replaceAll("(小学|初中|高中)", "");
            document.add(new TextField("content", content_name, Field.Store.YES));
            document.add(new StringField("semesters", semester, Field.Store.YES));
            document.add(new StringField("subjects", subject, Field.Store.YES));
            document.add(new StringField("grades", grade, Field.Store.YES));
            indexWriter.addDocument(document);
            System.out.println(content_name + " " + semester + " " + subject + " " + grade);
        }
        return indexWriter.numDocs();
    }

    private void search(String queryField) throws Exception{
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Result result = DicAnalysis.parse(queryField);
        System.out.println(result.getTerms());
        List<String> list = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        List<String> semesterList = new ArrayList<>();
        List<String> subjectList = new ArrayList<>();
        List<String> gradeList = new ArrayList<>();
        for(org.ansj.domain.Term term:result){
            String natureStr = term.getNatureStr();
            String fieldValue = term.getName();
            switch (natureStr) {
                case "nlp":
                    list.add(fieldValue);
                    break;
                case "nvs":
                    stringList.add(fieldValue);
                    break;
                case "semester":
                    semesterList.add(fieldValue);
                    break;
                case "subject":
                    subjectList.add(fieldValue);
                    break;
                case "grade":
                    gradeList.add(fieldValue);
                    break;
                default:
                    break;
            }
        }
        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        for (String string : list) {
            builder.add(new TermQuery(new Term("content", string)), BooleanClause.Occur.MUST);
        }
        for (String stringTo : stringList) {
            builder.add(new TermQuery(new Term("content", stringTo)), BooleanClause.Occur.SHOULD);
        }
        for (String semStr : semesterList) {
            builder.add(new TermQuery(new Term("semesters", semStr)), BooleanClause.Occur.MUST);
        }
        for (String graStr : gradeList) {
            builder.add(new TermQuery(new Term("grades", graStr)), BooleanClause.Occur.MUST);
        }
        for (String subStr : subjectList) {
            builder.add(new TermQuery(new Term("subjects", subStr)), BooleanClause.Occur.MUST);
        }
        Query query = builder.build();
        TopDocs docs = searcher.search(query, 20);
        System.out.println(docs.totalHits);
        for(ScoreDoc scoreDoc:docs.scoreDocs){
            document = searcher.doc(scoreDoc.doc);
            System.out.println(document.getField("content"));
        }
        reader.close();
        directory.close();

    }
    public static void main(String[] args)throws Exception{
        String indexDir = "D:\\test\\index";
        Lucene lucene = new Lucene(indexDir);
        String xlsPath = "C:\\Users\\Administrator\\Desktop\\语音调用关键字 180914 知识点微课-课时目录表.xlsx";
        long start = System.currentTimeMillis();
        int numIndexed=0 ;
//        lucene.delete();
//        numIndexed = lucene.createIndexer(xlsPath);
//        lucene.close();
        long end = System.currentTimeMillis();
        System.out.println("索引：" + numIndexed + " 个文件 花费了" + (end - start) + " 毫秒");
        String queryField = "我想听三角形的面积";
        lucene.search(queryField);


    }
}
