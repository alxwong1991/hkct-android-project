package com.hkct.project.Model;

import com.google.firebase.firestore.Exclude;

import org.checkerframework.checker.nullness.qual.NonNull;

public class EventId {
    @Exclude
        public String EventId;

    public <T extends EventId> T withId (@NonNull final String id) {
        this.EventId = id;
        return (T) this;
    }
}
