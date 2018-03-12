package org.registryviewer.service;

import com.thoughtworks.xstream.XStream;
import org.registryviewer.gui.HomeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KnownExceptionMessagesTranslatorService {

    private static final String ERROR_VOCABULARY = "/error_vocabulary.xml";

    private List<TranslationItem> translationItems;

    private static final Logger logger = LoggerFactory.getLogger(KnownExceptionMessagesTranslatorService.class);

    @PostConstruct
    public void init() {
        XStream xstream = new XStream();
        xstream.alias("item", TranslationItem.class);
        try (InputStream stream = KnownExceptionMessagesTranslatorService.class.getResourceAsStream(ERROR_VOCABULARY)) {
            translationItems = (List<TranslationItem>) xstream.fromXML(stream);
        } catch (IOException e) {
            logger.error("Error loading error dictionary");
            throw new RuntimeException("Error loading error dictionary", e);
        }
    }

    public List<TranslationItem> getTranslationItems() {
        return translationItems;
    }

    public String translate(Throwable e) {
        Optional<String> translation = findException(e.getMessage());
        if (translation.isPresent()) {
            return translation.get();
        }

        if (e.getCause() == null) {
            return e.toString();
        } else {
            return translate(e.getCause());
        }
    }

    private Optional<String> findException(String text) {

        if (text == null) {
            return Optional.empty();
        }

        for (TranslationItem translationItem : translationItems) {
            if (text.matches(translationItem.getFrom())) {
                return Optional.of(text.replaceFirst(translationItem.getFrom(), translationItem.getTo()));
            }
        }

        return Optional.empty();
    }

    public static class TranslationItem {
        private String from;
        private String to;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }
}
