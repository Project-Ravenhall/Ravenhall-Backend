package dev.ravenhall.book.domain.models;

import java.util.List;
import java.util.regex.Pattern;

public final class TextNormalizer {

    private TextNormalizer() {}

    private static final List<String> BLACKLISTED_TAGS = List.of(
            "z-library", "z-lib", "1lib", "libgen", "annas-archive",
            "retail", "digital", "ocr", "scan", "hq", "ebook"
    );
    private static final String TAGS_JOINED = String.join("|", BLACKLISTED_TAGS);
    private static final String REGEX_SYNTAX = String.format(
            "(?i)\\((?=[^)]*\\b(?:%1$s)\\b)[^)]*\\)|\\[(?=[^\\]]*\\b(?:%1$s)\\b)[^\\]]*\\]",
            TAGS_JOINED
    );
    private static final Pattern TAG_PATTERN = Pattern.compile(REGEX_SYNTAX);

    public static String normalize(String raw) {
        if (raw == null || raw.isBlank()) return "";
        String noExtension = raw.replaceFirst("\\.[^.]+$", "");
        String noTags = TAG_PATTERN.matcher(noExtension).replaceAll("");
        return collapseWhitespace(noTags);
    }

    public static String collapseWhitespace(String raw) {
        if (raw == null || raw.isBlank()) return "";
        return raw.replaceAll("[-_]+", " ")
                  .replaceAll("\\s+", " ")
                  .trim();
    }
}
