package com.azane.ogna.lib;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;

public final class GsonExtra
{
    public static final ExclusionStrategy EXPOSE_FILTER_serialize = new ExclusionStrategy()
    {
        @Override
        public boolean shouldSkipField(FieldAttributes f)
        {
            Expose expose = f.getAnnotation(Expose.class);
            return expose != null && !expose.serialize();
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz)
        {
            return false;
        }
    };
    public static final ExclusionStrategy EXPOSE_FILTER_deserialize = new ExclusionStrategy()
    {
        @Override
        public boolean shouldSkipField(FieldAttributes f)
        {
            Expose expose = f.getAnnotation(Expose.class);
            return expose != null && !expose.deserialize();
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz)
        {
            return false;
        }
    };
}
