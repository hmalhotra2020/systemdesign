package com.example.store;

import com.example.models.Models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface Store {}

class InMemoryStoreImpl implements Store {
    // We can keep a singleton instance or just static fields. Let's look at InMemoryStore in Scala which is an object.
    // In Java, an object is represented by static fields, or a singleton. Let's use a class with public static fields
    // which mirrors Scala's object variables.
}
