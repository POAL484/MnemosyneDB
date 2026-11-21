package org.mnemosynehermesteam.server.mhrl;

import java.util.function.Predicate;

public class ManipulateWithObjectTypedPredicate <T> {
    
    public final Class<T> clazz;
    public final Predicate<ManipObject<T>> lambda;

    public ManipulateWithObjectTypedPredicate(Class<T> clazz, Predicate<ManipObject<T>> lambda) {
        this.clazz = clazz;
        this.lambda = lambda;
    }
}
