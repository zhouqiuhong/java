package microclass;

import org.ansj.domain.Result;
import org.ansj.recognition.impl.UserDicNatureRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Search {
    public void getResult(String indexDir, String queryField) throws Exception {
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Result result = DicAnalysis.parse(queryField);
        new UserDicNatureRecognition().recognition(result);
        System.out.println(result.getTerms());
        List<String> list = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        List<String> semesterList = new ArrayList<>();
        List<String> subjectList = new ArrayList<>();
        List<String> gradeList = new ArrayList<>();
        for (org.ansj.domain.Term term : result) {
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
                    System.out.println("default");
                    break;

            }
        }
        System.out.println(list);
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

        TopDocs searchDocs = indexSearcher.search(query, 20);
        System.out.println(searchDocs.totalHits);
        for (ScoreDoc scoDoc : searchDocs.scoreDocs) {
            Document document = indexSearcher.doc(scoDoc.doc);
            System.out.println(document.getField("content"));
        }
        indexReader.close();
        directory.close();
    }

    public static void main(String[] args) throws Exception {
        Search search = new Search();
        String indexDir = "D:\\test\\index";
        search.getResult(indexDir, "我想听圆柱的全面积");
    }
}
