package com.udacity.udacitybooklistingapp;

/**
 * Created by temp on 15/08/2016.
 */
public class Model {

    private int totalItems;
    private String id;
    private String authors;
    private String title;

    public Model(int totalItems) {
        this.totalItems = totalItems;
    }

    public Model(int totalItems, String id) {
        this.totalItems = totalItems;
        this.id = id;
    }

    public Model(int totalItems, String id, String authors) {
        this.totalItems = totalItems;
        this.id = id;
        this.authors = authors;
    }

    public Model(String authors, String title) {
        this.authors = authors;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public String getAuthors() {
        return authors;
    }
}
