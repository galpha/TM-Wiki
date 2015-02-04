#!/bin/sh
scriptdir=`dirname $0`

java -Xmx1000m -cp "$scriptdir/stanford-ner.jar:" edu.stanford.nlp.ie.crf.NERGUI
