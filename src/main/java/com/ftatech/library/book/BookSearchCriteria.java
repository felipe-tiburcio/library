package com.ftatech.library.book;

public record BookSearchCriteria(String title, String author, String isbn, Boolean available) {
}
