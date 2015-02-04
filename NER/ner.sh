#!/bin/sh
scriptdir=`dirname $0`

java -Xmx1000m -cp "$scriptdir/stanford-ner.jar:" edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier $scriptdir/dewac_175m_600.crf.ser.gz -textFile $1
