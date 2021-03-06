/*
 * Copyright (c) 2018 Regents of the University of Minnesota.
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

package edu.umn.biomedicus.vocabulary;


import edu.umn.biomedicus.common.dictionary.BidirectionalDictionary;

/**
 * Retrieves the system-wide term indexes used by BioMedICUS.
 *
 * @since 1.6.0
 */
public interface Vocabulary {

  /**
   * The words that would appear in parse tokens.
   */
  BidirectionalDictionary getWordsIndex();

  /**
   * The words that would occur in term tokens.
   */
  BidirectionalDictionary getTermsIndex();

  /**
   * The words that would occur as normalized forms of term tokens.
   */
  BidirectionalDictionary getNormsIndex();
}
