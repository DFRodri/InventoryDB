package com.example.android.bookdb.custom_class;

public class Book {

    private String bookTitle;
    private String bookPrice;
    private String bookQuantity;


    public Book(String bookTitle, String bookPrice, String bookQuantity) {
        this.bookTitle = bookTitle;
        this.bookPrice = bookPrice;
        this.bookQuantity = bookQuantity;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public String getBookQuantity() {
        return bookQuantity;
    }

    public void setBookQuantity(String bookQuantity) {
        this.bookQuantity = bookQuantity;
    }
}
