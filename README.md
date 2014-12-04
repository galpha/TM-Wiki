# Textmining Praktikum

## Allgemeines

Im Rahmen des Text Mining Praktikums wird dieses Repository von der Gruppe "Named Entity Recognition mit Wikipedia (Orte)" genutzt.

## Links & Infos

Neueste [Wikipedia Dumps](https://dumps.wikimedia.org/enwiki/latest/)  
Alle Orte mit Eintrag (Geografikum) in Normdaten: [Deutsche National Bibliothek](https://portal.dnb.de/)  
Nützliche Bibliothek: [simmetrics](https://sourceforge.net/projects/simmetrics/)

# Compile & Run

install maven 3.0.5

## Title Parser

cd ../TM-Wiki/

mvn compile

mvn exec:java -Dexec.mainClass="title_parser.Main"