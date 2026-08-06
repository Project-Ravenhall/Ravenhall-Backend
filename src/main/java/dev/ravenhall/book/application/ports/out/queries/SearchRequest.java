package dev.ravenhall.book.application.ports.out.queries;

import java.util.List;

public record SearchRequest(String title, List<String> authors, String series) {}