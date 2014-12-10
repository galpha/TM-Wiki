# Textmining Praktikum

## Allgemeines

Im Rahmen des Text Mining Praktikums wird dieses Repository von der Gruppe "Named Entity Recognition mit Wikipedia (Orte)" genutzt.

## Links & Infos

Neueste [Wikipedia Dumps](https://dumps.wikimedia.org/dewiki/latest/)  
Alle Orte mit Eintrag (Geografikum) in Normdaten: [Deutsche National Bibliothek](https://portal.dnb.de/)  
Nützliche Bibliothek: [simmetrics](https://sourceforge.net/projects/simmetrics/)

# Compile & Run

## Title Parser

cd ../TM-Wiki/

javac *.java

java Main

## Highlighter

cd TM-Wiki/src/main/java/text_highlighter/  

javac -cp '.:simmetrics.jar' ./text_highlighter.java 

java -cp '.:simmetrics.jar' text_highlighter

