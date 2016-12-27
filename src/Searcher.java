import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jsh3571 on 27/12/2016.
 */

public class Searcher {

    public static void main(String[] args) throws Exception {
        String indexPath = "./test/idx";    // where the index is
        String field = "contents";          // find qstring from field
        String queryString = "raincoat";         // thing that were looking 4
        int hitPerPage = 10;

        // IndexReader 생성(IndexSearcher에 넘겨주기 위해)
        Path p = Paths.get(indexPath);
        Directory d = FSDirectory.open(p);
        IndexReader reader = DirectoryReader.open(d);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Analyser 생성
        Analyzer analyzer = new StandardAnalyzer();

        // Query 생성 from QueryParser
        QueryParser queryParser = new QueryParser(field, analyzer);
        Query query = queryParser.parse(queryString);

        // search performed here
        TopDocs results = searcher.search(query, 5*hitPerPage);
        ScoreDoc[] hits = results.scoreDocs;

        int numOfHits = results.totalHits;
        System.out.println(numOfHits+" total matching documents");

        for (int i=0; i<numOfHits; i++) {
            Document doc = searcher.doc(hits[i].doc);
            String path = doc.get(/* field */ "path");
            System.out.println(""+path);
        }
        reader.close();
    }
}
