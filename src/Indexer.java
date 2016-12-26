import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Created by jsh3571 on 26/12/2016.
 */

public class Indexer {

    // concerns creation only.
    public static void main(String[] args) {
        String indexPath = "C:\\lucene_test\\index";    // toSaved
        String docsPath = "C:\\lucene_test\\data";      // toGetData

        final Path docDir = Paths.get(docsPath);        // getPath of dataDir
        if (!Files.isReadable(docDir))                  // error handling
            System.exit(1);

        Date start = new Date();    // need this to get time taken.
        try {
            System.out.println("Indexing to directory '"+indexPath+"'...");
            // param 1
            Directory dir = FSDirectory.open(Paths.get(indexPath));

            // param 2
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            // set up the writer which actually writes index using from docDir(::Path).
            IndexWriter writer = new IndexWriter(dir, config);

            // main algo
            // indexDocs(writer, docDir);

            System.out.println((new Date().getTime()-start.getTime()) +
                    " total milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
