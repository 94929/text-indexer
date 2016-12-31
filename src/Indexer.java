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
import java.util.concurrent.TimeUnit;

/**
 * Created by jsh3571 on 26/12/2016.
 */

public class Indexer {
    private final Path idxPath;           // index folder path
    private final Path docPath;           // docs folder path
    private Date start;                   // need this to get time.

    private IndexWriter writer;

    public Indexer(String idxPath, String docPath) {
        this.idxPath = Paths.get(idxPath);
        this.docPath = Paths.get(docPath);
        if (!Files.isReadable(this.docPath)) {
            System.out.println("'docPath' is not readable.");
            System.exit(1);
        }

        start = new Date();

        try {
            System.out.println("Indexing to directory '" +idxPath+ "'...");

            initWriter();
            indexDocs(writer, this.docPath);

            // Printing total time taken for indexing.
            printTimeTaken();

            // Closing writer before reader reads from searcher.
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initWriter() throws IOException {
        // PARAM 1 of writer, Open idxPath as Directory
        Directory directory = FSDirectory.open(idxPath);

        // param 2 of writer,
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        // IndexWriter writer = new (PARAM1,2)
        writer = new IndexWriter(directory, config);
        // saying that this writer will write into the directory with the config
    }

    /* Iterate through the paths(ie. the files) */
    private void indexDocs(final IndexWriter writer, final Path path)
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

    private void indexDoc(IndexWriter writer, Path file, long lastModified)
            throws IOException {

        try (InputStream stream = Files.newInputStream(file)) {
            Document doc = new Document();

            doc.add(new StringField("path", file.toString(), Field.Store.YES));
            doc.add(new LongPoint("modified", lastModified));
            doc.add(new TextField("contents",
                    new BufferedReader(
                            new InputStreamReader(
                                    stream, StandardCharsets.UTF_8))));

            System.out.println("adding '"+file+"'");
            writer.addDocument(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printTimeTaken() {
        System.out.println("Took total "+ TimeUnit.MILLISECONDS.toSeconds(
                new Date().getTime()-start.getTime())+
                " seconds.");
    }
}
