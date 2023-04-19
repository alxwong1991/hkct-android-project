package com.hkct.project.Model;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ProductId {
    private String ProductId;

    public <T extends ProductId> T withId (@NonNull final String id) {
        this.ProductId = id;
        return (T) this;
    }
}
