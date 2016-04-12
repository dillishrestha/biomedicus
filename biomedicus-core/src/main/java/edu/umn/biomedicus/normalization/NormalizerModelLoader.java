package edu.umn.biomedicus.normalization;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import edu.umn.biomedicus.application.DataLoader;
import edu.umn.biomedicus.common.tuples.WordPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 *
 */
@Singleton
public class NormalizerModelLoader extends DataLoader<NormalizerModel> {
    private final Logger LOGGER = LogManager.getLogger();

    private final Path lexiconFile;

    private final Path fallbackLexiconFile;

    @Inject
    public NormalizerModelLoader(@Named("normalization.lexicon.path") Path lexiconFile,
                                 @Named("normalization.fallback.path") Path fallbackLexiconFile) {
        this.lexiconFile = lexiconFile;
        this.fallbackLexiconFile = fallbackLexiconFile;
    }

    @Override
    protected NormalizerModel loadModel() {
        Yaml yaml = new Yaml();
        try {
            LOGGER.info("Loading normalization lexicon file: {}", lexiconFile);
            @SuppressWarnings("unchecked")
            Map<WordPos, String> lexicon = (Map<WordPos, String>) yaml.load(Files.newInputStream(lexiconFile));

            LOGGER.info("Loading normalization fallback lexicon file: {}", fallbackLexiconFile);
            @SuppressWarnings("unchecked")
            Map<WordPos, String> fallbackLexicon = (Map<WordPos, String>) yaml.load(Files.newInputStream(fallbackLexiconFile));

            return new NormalizerModel(lexicon, fallbackLexicon);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load Normalizer model", e);
        }
    }
}
