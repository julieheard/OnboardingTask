package io.jenkins.plugins;

import java.util.UUID;

public class CategoryItem {

    private UUID uuid;
    private String CategoryName;

    public CategoryItem() {
    }

    public CategoryItem(String categoryName) {
        this.CategoryName = categoryName;
        this.uuid = java.util.UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCategoryName() {
        return CategoryName;
    }

}
