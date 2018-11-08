package testing;

import java.nio.file.Paths;

import org.ansj.lucene7.AnsjAnalyzer;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    public static void search(String indexDir, String q, String dic) throws Exception{
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Result results = IndexAnalysis.parse(q);
        System.out.println(results.getTerms());
        Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.dic_ansj, dic);

        QueryParser parser = new QueryParser("content", analyzer);
//        BooleanQuery
        Query query = parser.parse(q);
        System.out.println(query.toString());

        long start = System.currentTimeMillis();

        TopDocs hits = indexSearcher.search(query, 1000);
        long end = System.currentTimeMillis();
        System.out.println("匹配 " + q + " ，总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");
        System.out.println(hits.getMaxScore());

        for(ScoreDoc scoreDoc: hits.scoreDocs){
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(doc.getFields());

//            System.out.println(doc.get("fullPath"));
            System.out.println(doc.getField("content"));
        }
        indexReader.close();
        dir.close();
    }
    public static void main(String[] args){
        String indexDir = "D:\\test\\index";
        String q = "我想学一元二次方程的概念";
        String dic = "dic";
//        try{
//            search(indexDir, q, dic);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }
}
