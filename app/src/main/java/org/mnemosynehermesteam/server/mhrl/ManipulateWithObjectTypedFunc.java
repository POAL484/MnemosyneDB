package org.mnemosynehermesteam.server.mhrl;

import java.util.function.BiFunction;

public class ManipulateWithObjectTypedFunc <T> {
    
    public final Class<T> clazz;
    public final BiFunction<ManipObject<T>, MHRLRequestBody, MHRLOperationResult> lambda;

    public ManipulateWithObjectTypedFunc(Class<T> clazz, BiFunction<ManipObject<T>, MHRLRequestBody, MHRLOperationResult> lambda) {
        this.clazz = clazz;
        this.lambda = lambda;
    }
}
