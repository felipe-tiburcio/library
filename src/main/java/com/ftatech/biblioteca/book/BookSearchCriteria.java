package com.ftatech.biblioteca.book;

public record BookSearchCriteria(String title, String author, String isbn, Boolean available) {
}
