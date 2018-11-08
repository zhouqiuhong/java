package testing;

import org.ansj.lucene7.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;


public class CreateIndexer {
    /**
     *
     * @param indexPosition 索引存放的位置
     * @param dics 词典加载的位置
     * @param text 被索引的文件， 索引数据存放位置
     * @param file
     * @throws Exception
     */
    public IndexWriter createDoc(String indexPosition, String dics, String filePath) throws Exception
    {
        Document document = new Document();

        Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.dic_ansj, dics);

        IndexWriterConfig  indexWriterConfig = new IndexWriterConfig(analyzer);
        Directory directory = FSDirectory.open(Paths.get(indexPosition));
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(filePath), "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;
        lineTxt = bufferedReader.readLine();
        while (lineTxt != null){
            document.add(new TextField("contents", lineTxt, Field.Store.YES));
            indexWriter.addDocument(document);
        }

        return indexWriter;
    }

    /**
     * @param searchField field
     * @param keyWord 用户搜索关键词
     * @param indexDir 索引文档地址
     * @param dic 词典
     */
    public void queryIndex(String searchField, String keyWord, String indexDir, String dic) throws Exception
    {
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.dic_ansj, dic);
        QueryParser queryParser = new QueryParser(searchField, analyzer);
        Query query = queryParser.parse(keyWord);

        TopDocs hits = indexSearcher.search(query, 10);

        for(ScoreDoc scoreDoc:hits.scoreDocs){
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(doc.get("fullPath"));
        }

    }
    public static void main(String[] args){
        CreateIndexer indexer = new CreateIndexer();
        String indexDir = "D:\\test\\index";
        String dataDir = "D:\\test\\data";
        String filePath = "D:\\test\\data\\调用关键字2.txt";
        String searchField = "contents";
        String text = "this is a dog";
        File[]  files = new File(dataDir).listFiles();
        String dic="dic";
        try{
                indexer.createDoc(indexDir, dic, filePath);

//                indexer.queryIndex(searchField, "dog", indexDir, dic);
//                indexer.createDoc(indexDir, text, dic).close();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                indexer.createDoc(indexDir, dic, filePath).close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

//        String string = "我和你一起";
//        System.out.println(IndexAnalysis.parse(string));
    }




}
