package com.gymcrm.storage;

import java.util.HashMap;

public abstract class TypedStorage<T> extends HashMap<Long, T> {
    public abstract Class<T> getType();
}
