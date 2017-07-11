/*
 * Copyright (c) 2017 Regents of the University of Minnesota.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.umn.biomedicus.spelling;

import com.google.inject.Inject;
import edu.umn.biomedicus.common.StandardViews;
import edu.umn.biomedicus.common.terms.TermIndex;
import edu.umn.biomedicus.common.types.semantics.Misspelling;
import edu.umn.biomedicus.common.types.text.ParseToken;
import edu.umn.biomedicus.common.utilities.Patterns;
import edu.umn.biomedicus.exc.BiomedicusException;
import edu.umn.biomedicus.framework.DocumentProcessor;
import edu.umn.biomedicus.framework.store.Document;
import edu.umn.biomedicus.framework.store.Label;
import edu.umn.biomedicus.framework.store.LabelIndex;
import edu.umn.biomedicus.framework.store.TextView;
import edu.umn.biomedicus.framework.store.ValueLabeler;
import edu.umn.biomedicus.vocabulary.Vocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class MisspellingDetector implements DocumentProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MisspellingDetector.class);

  private final TermIndex wordIndex;

  @Inject
  public MisspellingDetector(Vocabulary vocabulary) {
    wordIndex = vocabulary.getWordsIndex();
  }

  @Override
  public void process(Document document) throws BiomedicusException {
    LOGGER.info("Finding any misspelled words in a document.");

    TextView systemView = StandardViews.getSystemView(document);

    LabelIndex<ParseToken> parseTokenLabelIndex = systemView.getLabelIndex(ParseToken.class);
    ValueLabeler mispellingLabeler = systemView.getLabeler(Misspelling.class)
        .value(new Misspelling());

    for (Label<ParseToken> parseTokenLabel : parseTokenLabelIndex) {
      ParseToken parseToken = parseTokenLabel.value();
      String text = parseToken.text();
      if (Patterns.ALPHABETIC_WORD.matcher(text).matches() && !wordIndex.contains(text)) {
        mispellingLabeler.label(parseTokenLabel);
      }
    }
  }
}
