package nl.healthri.fdp.transferdata;

import org.eclipse.rdf4j.repository.RepositoryConnection;
import picocli.CommandLine;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

@CommandLine.Command(name = "TransferData",
        description = "Transfer data between RDF4J (supported) triplestores. You must first specify the source database " +
                "this can be a local data store (native store) or url endpoint (triple store), for an endpoint you can optionally " +
                "give a username/password if needed for the datastore. Then specify the second (target) database that can " +
                "be native or remote datastore. " +
                "If you supply just one triplestore, it will just show the statements",
        mixinStandardHelpOptions = true, version = "TransferData v1.0")
public class TransferData implements Runnable {

    // First group: for the first parameter
    @CommandLine.ArgGroup(multiplicity = "1..2", order = 1, heading = "source")
    RepositoryGroup[] repos;

    public static void main(String... args) {
        var cmd = new CommandLine(new TransferData());
        if (args.length == 0) {
            cmd.usage(System.out);
        } else {
            System.exit(cmd.execute(args));
        }
    }

    @Override
    public void run() {
        try {
            if (repos.length == 1) {
                //just print..
                var source = repos[0].getTripleStore();
                System.out.printf("Print statements for: %s", source.getRepository().toString());
                RdfUtils.printStats(source);
            } else {
                var source = repos[0].getTripleStore();
                var target = repos[1].getTripleStore();
                System.out.printf("Transfering data from: %s to: %s", source.getRepository().toString(), target.getRepository().toString());
                RdfUtils.copy(source, target);
            }


        } catch (MalformedURLException me) {
            throw new RuntimeException(me);
        }
    }


    //--dir="C:\Users\PatrickDekker(Health\OneDrive - Health-RI\Bureaublad\fdp\" --endpoint="http://localhost:7200/repositories/fdp"

    public static class RepositoryGroup {
        @CommandLine.Option(names = {"-d", "--dir"},
                description = "Data directory of Native store")
        public Optional<Path> dataDirectory;

        @CommandLine.Option(names = {"-e", "--endpoint"},
                description = "URL endpoint, if needed you can supply password: https://username:password@example.com/fdp ,check the triple store documentation for the exact url, example 'fdp' repo in the GraphDb -> localhost:7200/repositories/fdp")
        public Optional<URL> url;

        private RepositoryConnection getTripleStore() throws MalformedURLException {
            return dataDirectory.isPresent() ? RdfUtils.tripleStore(dataDirectory.get()) : RdfUtils.tripleStore(url.get());
        }
    }
};