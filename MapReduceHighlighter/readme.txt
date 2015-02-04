Der MapReduce Highlighter ist ein Maven Projekt!
Zum Bauen des Projektes benötigt man maven 3.0 or later.

Um das Projekt mittels Maven erstellen zu können muss vorher die
Simmetrics-lib lokals ins Maven Repository insstalliert werden!
Dazu navigiert man in den ordner der Simmetrics-lib und führt 
folgenden befehl aus:

mvn install:install-file -Dfile=<simmetrics-name>.jar -DgroupId=simmetrics -DartifactId=simmetrics-lib -Dversion=1.0 -Dpackaging=jar


mit:
	mvn clean package

erzeugt man eine fertige Highlight.jar welche alle abhängigkeiten
wie hadoop-libs oder simmetrics beinhaltet.

Informationen über verwendete hadoop versionen

<!-- hadoop -->
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-hdfs</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-auth</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-common</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-core</artifactId>
            <version>1.2.1</version>
        </dependency>
