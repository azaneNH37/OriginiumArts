package com.azane.ogna.util;

import com.azane.ogna.genable.data.FxData;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public final class OgnaFxHelper
{
    public static Optional<FxData.FxUnit> extractFxUnit(@Nullable FxData fxData, Function<FxData, FxData.FxUnit> mapper)
    {
        return Optional.ofNullable(fxData).map(mapper);
    }
}
