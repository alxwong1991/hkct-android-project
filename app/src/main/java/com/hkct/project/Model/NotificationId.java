package com.hkct.project.Model;

import com.google.firebase.firestore.Exclude;

import org.checkerframework.checker.nullness.qual.NonNull;

public class NotificationId {
    @Exclude
        public String NotificationId;

    public <T extends NotificationId> T withId (@NonNull final String id) {
        this.NotificationId = id;
        return (T) this;
    }
}
