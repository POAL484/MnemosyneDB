package org.mnemosynehermesteam.server.mhrl;

import java.util.function.Predicate;

public class SematicPipelineTypedPredicate <T> {
    
    public final Class<T> clazz;
    public final Predicate<ManipObject<T>> lambda;

    public SematicPipelineTypedPredicate(Class<T> clazz, Predicate<ManipObject<T>> lambda) {
        this.clazz = clazz;
        this.lambda = lambda;
    }
}
