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

package edu.umn.biomedicus.measures

import edu.umn.biomedicus.framework.TagEx
import edu.umn.biomedicus.framework.TagExFactory
import edu.umn.biomedicus.framework.get
import edu.umn.biomedicus.numbers.NumberType
import edu.umn.nlpengine.Document
import edu.umn.nlpengine.DocumentsProcessor
import edu.umn.nlpengine.Label
import edu.umn.nlpengine.LabelMetadata
import java.math.BigDecimal
import javax.inject.Inject

/**
 * A number in text.
 * @property numerator the BigDecimal representation
 * @property denominator the BigDecimal representation
 * @property numberType
 */
@LabelMetadata(classpath = "biomedicus.v2", distinct = true)
data class Number(
        override val startIndex: Int,
        override val endIndex: Int,
        val numerator: String,
        val denominator: String,
        val numberType: NumberType
) : Label() {
    fun value(): BigDecimal {
        return BigDecimal(numerator).divide(BigDecimal(denominator), BigDecimal.ROUND_HALF_UP)
    }
}

/**
 * A number range from one number to another in text.
 */
@LabelMetadata(classpath = "biomedicus.v2", distinct = true)
data class NumberRange(
        override val startIndex: Int,
        override val endIndex: Int,
        val lower: BigDecimal,
        val upper: BigDecimal
) : Label()

/**
 * The document processor which is responsible for detecting number ranges in text.
 */
class NumberRangesLabeler(val expr: TagEx) : DocumentsProcessor {
    @Inject internal constructor(tagExFactory: TagExFactory) : this(
            tagExFactory.parse(
                    "(?<range> [?lower:Number] ParseToken<getText=\"-\"|i\"to\"> -> upper:Number | [?ParseToken<getText=i\"between\">] -> lower:Number ParseToken<getText=\"and\"> -> upper:Number)"
            )
    )

    override fun process(document: Document) {
        val labeler = document.labeler(NumberRange::class.java)

        for (tagExResult in expr.findAll(document)) {
            val lower: Number = tagExResult.namedLabels["lower"]
                    ?: error("lower should always have value")

            val upper: Number = tagExResult.namedLabels["upper"]
                    ?: error("upper should always have value")

            val lowerValue = lower.value()
            val upperValue = upper.value()
            if (upperValue > lowerValue) {
                val (startIndex, endIndex) = tagExResult.namedSpans["range"]
                        ?: error("range should always have value")
                labeler.add(NumberRange(startIndex, endIndex, lowerValue, upperValue))
            }
        }
    }
}
