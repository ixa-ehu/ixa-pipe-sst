/*
 *  Copyright 2016 Rodrigo Agerri

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package eus.ixa.ixa.pipe.sst;

import ixa.kaflib.ExternalRef;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.Mark;
import ixa.kaflib.Term;
import ixa.kaflib.WF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.google.common.io.Files;

import eus.ixa.ixa.pipe.ml.StatisticalSequenceLabeler;
import eus.ixa.ixa.pipe.ml.sequence.Sequence;
import eus.ixa.ixa.pipe.ml.sequence.SequenceFactory;

/**
 * Annotation class for SuperSenses in ixa-pipe-sst. This class is useful to see
 * how to use ixa-pipe-ml API for SuperSense Tagging.
 * @author ragerri
 * @version 2016-04-25
 */
public class Annotate {

  /**
   * The sequence factory.
   */
  private SequenceFactory seqFactory;
  /**
   * The SST to do the annotation.
   */
  private StatisticalSequenceLabeler sstTagger;
  /**
   * Clear features after every sentence or when a -DOCSTART- mark appears.
   */
  private String clearFeatures;
  
  private String model;

  public Annotate(final Properties properties) throws IOException {
    this.model = properties.getProperty("model");
    this.clearFeatures = properties.getProperty("clearFeatures");
    seqFactory = new SequenceFactory();
    sstTagger = new StatisticalSequenceLabeler(properties, seqFactory);
  }

  /**
   * Classify Named Entities creating the entities layer in the
   * {@link KAFDocument} using statistical models, post-processing and/or
   * dictionaries only.
   * 
   * @param kaf
   *          the kaf document to be used for annotation
   * @throws IOException
   *           throws exception if problems with the kaf document
   */
  public final void annotateToKAF(final KAFDocument kaf) throws IOException {

    List<List<WF>> sentences = kaf.getSentences();
    for (List<WF> sentence : sentences) {
      // process each sentence
      String[] tokens = new String[sentence.size()];
      String[] tokenIds = new String[sentence.size()];
      for (int i = 0; i < sentence.size(); i++) {
        tokens[i] = sentence.get(i).getForm();
        tokenIds[i] = sentence.get(i).getId();
      }
      if (clearFeatures.equalsIgnoreCase("docstart")
          && tokens[0].startsWith("-DOCSTART-")) {
        sstTagger.clearAdaptiveData();
      }
      List<Sequence> seqSpans = sstTagger.getSequences(tokens);
      for (Sequence name : seqSpans) {
        Integer startIndex = name.getSpan().getStart();
        Integer endIndex = name.getSpan().getEnd();
        List<Term> nameTerms = kaf.getTermsFromWFs(Arrays.asList(Arrays
            .copyOfRange(tokenIds, startIndex, endIndex)));
        List<WF> targets = new ArrayList<>();
        for (Term term : nameTerms) {
          List<WF> wfs = term.getWFs();
          for (WF wf : wfs) {
            targets.add(wf);
          }
        }
        Mark markable = kaf.newMark(KAFDocument.newWFSpan(targets), "Ancora");
        markable.setLemma(name.getString());
        ExternalRef externalRef = kaf.createExternalRef("MRC-3.0-SST", name.getType());
        markable.addExternalRef(externalRef);
      }
      if (clearFeatures.equalsIgnoreCase("yes")) {
        sstTagger.clearAdaptiveData();
      }
    }
    sstTagger.clearAdaptiveData();
  }

  public final String annotateToCoNLL02(final KAFDocument kaf) {
    StringBuilder sb = new StringBuilder();
    List<List<WF>> sentences = kaf.getSentences();
    for (List<WF> sentence : sentences) {
      // process each sentence
      String[] tokens = new String[sentence.size()];
      String[] tokenIds = new String[sentence.size()];
      for (int i = 0; i < sentence.size(); i++) {
        tokens[i] = sentence.get(i).getForm();
        tokenIds[i] = sentence.get(i).getId();
      }
      if (clearFeatures.equalsIgnoreCase("docstart")
          && tokens[0].startsWith("-DOCSTART-")) {
        sstTagger.clearAdaptiveData();
      }
      String[] seqStrings = sstTagger.seqToStrings(tokens);
      for (int i = 0; i < seqStrings.length; i++) {
        sb.append(tokens[i]).append("\t").append(seqStrings[i]).append("\n");
      }
      if (clearFeatures.equalsIgnoreCase("yes")) {
        sstTagger.clearAdaptiveData();
      }
      sb.append("\n");
    }
    sstTagger.clearAdaptiveData();
    return sb.toString();
  }

}
