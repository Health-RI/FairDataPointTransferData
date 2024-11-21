package nl.healthri.fdp.transferdata;

import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class RdfUtilsTest {

    private final String localFile = "C:\\Users\\PatrickDekker(Health\\OneDrive - Health-RI\\Bureaublad\\fdp\\";
    private final String remoteUrl = "http://localhost:7200/repositories/fdp";

    private static void purgeDirectory(File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                purgeDirectory(f);
            } else {
                f.delete();
            }
        }
    }

    @Test
    @Disabled
    void printNative() {
        Path dir = Path.of(localFile);
        var store = RdfUtils.tripleStore(dir);
        RdfUtils.printStats(store);
    }

    @Test
    void nativeStore() throws IOException {
        Path tmpDir = Files.createTempDirectory("nativeStore");
        try (var store = RdfUtils.tripleStore(tmpDir)) {
            assertThat(store.size(), equalTo(0L));
        }
    }

    @Test
    void copy() throws IOException {
        Path tmpDirSource = Files.createTempDirectory("source");
        Path tmpDirTarget = Files.createTempDirectory("target");

        try (
                var source = RdfUtils.tripleStore(tmpDirSource);
                var target = RdfUtils.tripleStore(tmpDirTarget)) {

            final long numberOfTriples = 1000L;

            assertThat(source.size(), equalTo(0L));
            assertThat(target.size(), equalTo(0L));

            source.begin();
            for (int i = 1; i <= numberOfTriples; i++) {
                var iri = source.getValueFactory().createIRI("http://www.example.com//#" + i);
                source.add(iri, DCTERMS.TITLE, source.getValueFactory().createLiteral("Test " + i));
            }
            source.commit();
            RdfUtils.copy(source, target);

            assertThat(source.size(), equalTo(numberOfTriples));
            assertThat(target.size(), equalTo(numberOfTriples));
        }
        purgeDirectory(tmpDirSource.toFile());
        purgeDirectory(tmpDirTarget.toFile());
    }

    @Test
    void clear() throws IOException {
        Path tmpDir = Files.createTempDirectory("nativeStore");
        try (var connection = RdfUtils.tripleStore(tmpDir)) {
            var iri = connection.getValueFactory().createIRI("http://www.example.com");
            connection.add(iri, iri, iri);
            assertThat("add statement", connection.size(), equalTo(1L));
            connection.clear();
            assertThat("delete all statements", connection.size(), equalTo(0L));
        }
        //delete temp directory and files.
        purgeDirectory(tmpDir.toFile());
    }

    @Test
    void testUrl() throws MalformedURLException {
        var url = RdfUtils.parseUrl(new URL("http://user:password@localhost:7200/repositories/fdp"));
        assertThat("username", url.username(), equalTo("user"));
        assertThat("password", url.password(), equalTo("password"));
        assertThat("correct url", url.url(), equalTo("http://localhost:7200/repositories/fdp"));
        assertThat("repo", url.repo(), equalTo("fdp"));
    }

}