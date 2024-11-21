# FairDataPointTransferData

This tool copies rdf data between two rdf stores. It uses rdf4j to read and write the data between stores.
it can read/write from rdf4j Native stores and Remote stores (like GraphDB or BlazeGraph)

## Installation

This tools need java runtime to run (at least ver 17). To build the tool run:

```text
mvn clean install
```

## Usage

java -jar FairDataPo

```text
Usage: TransferData [-hV] (-d=<dataDirectory> | -e=<url>) [-d=<dataDirectory> |
                    -e=<url>]
Transfer data between RDF4J (supported) triplestores. You must first specify
the source database this can be a local data store (native store) or url
endpoint (triple store), for an endpoint you can optionally give a
username/password if needed for the datastore. Then specify the second (target)
database that can be native or remote datastore. If you supply just one
triplestore, it will just show the statements
  -h, --help             Show this help message and exit.
  -V, --version          Print version information and exit.
source  -d, --dir=<dataDirectory>
                         Data directory of Native store
  -e, --endpoint=<url>   URL endpoint, if needed you can supply password: https:
                           //username:password@example.com/fdp ,check the
                           triple store documentation for the exact url,
                           example 'fdp' repo in the GraphDb -> localhost:
                           7200/repositories/fdp

```

## Limitations

Currently only the RDF triples are copies, between the triple stores, not the additional data in the mongo database!.
You should make sure you copy this data as well. 

