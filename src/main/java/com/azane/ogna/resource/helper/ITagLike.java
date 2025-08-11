package com.azane.ogna.resource.helper;

import java.util.Set;

/**
 * @author azaneNH37 (2025-07-13)
 */
public interface ITagLike<T>
{
    Class<T> getTagType();

    Set<T> getContentList();

    @SuppressWarnings("unchecked")
    default <U> ITagLike<U> castToType(Class<U> type)
    {
        if(getTagType().equals(type))
        {
            return (ITagLike<U>) this;
        }
        throw new ClassCastException("Cannot cast to " + type.getName());
    }

    void absorb(ITagLike<T> other);
}
