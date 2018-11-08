package testing;

import java.io.*;
import java.nio.file.Paths;

import org.ansj.lucene7.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
/**
 *
 *TODO  索引文件
 * @author Snaiclimb
 * @date 2018年3月30日
 * @version 1.8
 */
public class Indexer {
    // 写索引实例
    private IndexWriter writer;

    /**
     * 构造方法 实例化IndexWriter
     *
     * @param indexDir
     * @throws IOException
     */
    public Indexer(String indexDir, String dic) throws IOException {
        //得到索引所在目录的路径  
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        // 标准分词器
        Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.dic_ansj, dic);
        //保存用于创建IndexWriter的所有配置。
        IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
        //实例化IndexWriter  
        writer = new IndexWriter(directory, iwConfig);
    }

    /**
     * 关闭写索引
     *
     * @throws Exception
     * @return 索引了多少个文件
     */
    public void close() throws IOException {
        writer.close();
    }

//    public int index(String dataDir) throws Exception {
//        File[] files = new File(dataDir).listFiles();
//        for (File file : files) {
//            //索引指定文件
//            indexFile(file);
//        }
//        //返回索引了多少个文件
//        return writer.numDocs();
//
//    }

    /**
     * 索引指定文件
     *
     * @param
     */
//    private int indexFile(String filePath) throws Exception {
//        //输出索引文件的路径
////        System.out.println("索引文件：" + f.getCanonicalPath());
//        //获取文档，文档里再设置每个字段
//        Document doc = getDocument(filePath);
//        //开始写入,就是把文档写进了索引文件里去了；
//        writer.addDocument(doc);
//        return writer.numDocs();
//    }

    /**
     * 获取文档，文档里再设置每个字段
     *
     * @param
     * @return document
     */
    private int getDocument(String filePath) throws Exception {


        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(filePath), "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineTxt = null;

        //把设置好的索引加到Document里，以便在确定被索引文档
        while ((lineTxt = bufferedReader.readLine()) != null){

            Document doc = new Document();
            doc.add(new TextField("content", lineTxt, Field.Store.YES));
            writer.addDocument(doc);
        }
        return writer.numDocs();
    }

    public static void main(String[] args) {
        //索引指定的文档路径
        String indexDir = "D:\\test\\index";
        String filePath = "D:\\test\\data\\resource.txt";
        String dic = "dic";
        Indexer indexer = null;
        int numIndexed = 0;
        //索引开始时间
        long start = System.currentTimeMillis();
        try {
            indexer = new Indexer(indexDir, dic);
            numIndexed = indexer.getDocument(filePath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                indexer.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //索引结束时间
        long end = System.currentTimeMillis();
        System.out.println("索引：" + numIndexed + " 个文件 花费了" + (end - start) + " 毫秒");
    }

}
