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

    public Searcher(String indexPath, String queryString) throws Exception {
        // Create IndexSearcher
        Path p = Paths.get(indexPath);
        Directory d = FSDirectory.open(p);
        IndexReader reader = DirectoryReader.open(d);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Create Analyser
        Analyzer analyzer = new StandardAnalyzer();

        // Create Query from QueryParser
        QueryParser queryParser = new QueryParser("contents", analyzer);
        Query query = queryParser.parse(queryString);

        int hitPerPage = 10;

        // Actual search is performed here
        TopDocs results = searcher.search(query, 5*hitPerPage);
        ScoreDoc[] hits = results.scoreDocs;

        int numOfHits = results.totalHits;
        System.out.println(numOfHits+" total matching documents");

        for (int i=0; i<numOfHits; i++) {
            Document doc = searcher.doc(hits[i].doc);
            String path = doc.get("path");
            System.out.println(""+path);
        }

        reader.close();
    }
}
