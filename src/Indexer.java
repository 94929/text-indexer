import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

/**
 * Created by jsh3571 on 26/12/2016.
 */

public class Indexer {

    // concerns creation only.
    public static void main(String[] args) {
        String indexPath = "./test/idx";        // toSaved
        String docsPath = "./test/doc";         // toGetData

        final Path docDir = Paths.get(docsPath);        // getPath of dataDir
        if (!Files.isReadable(docDir))                  // error handling
            System.exit(1);

        Date start = new Date();    // need this to get time taken.
        try {
            System.out.println("Indexing to directory '" + indexPath + "'...");
            // param 1
            Directory dir = FSDirectory.open(Paths.get(indexPath));

            // param 2
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            // set up the writer which actually writes index using from docDir(::Path).
            IndexWriter writer = new IndexWriter(dir, config);

            // main algo
            indexDocs(writer, docDir);

            System.out.println((new Date().getTime() - start.getTime()) +
                    " total milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Iterate through the paths(ie. the files) */
    public static void indexDocs(final IndexWriter writer, Path path)
            throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(
                        Path file,
                        BasicFileAttributes attrs) throws IOException {
                    indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public static void indexDoc(
            IndexWriter writer,
            Path file,
            long lastModified) throws IOException {

        try (InputStream stream = Files.newInputStream(file)) {
            Document doc = new Document();

            doc.add(new StringField("path", file.toString(), Field.Store.YES));
            doc.add(new LongPoint("modified", lastModified));
            doc.add(new TextField("contents",
                    new BufferedReader(
                            new InputStreamReader(
                                    stream, StandardCharsets.UTF_8))));

            System.out.println("adding"+file);
            writer.addDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
