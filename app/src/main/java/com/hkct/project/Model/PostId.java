package com.hkct.project.Model;

import com.google.firebase.firestore.Exclude;

import org.checkerframework.checker.nullness.qual.NonNull;

public class PostId {
    @Exclude
        public String PostId;

    public <T extends PostId> T withId (@NonNull final String id) {
        this.PostId = id;
        return (T) this;
    }
}
