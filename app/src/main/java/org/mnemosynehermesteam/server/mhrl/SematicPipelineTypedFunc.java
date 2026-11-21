package org.mnemosynehermesteam.server.mhrl;

import java.util.function.BiFunction;

public class SematicPipelineTypedFunc <T> {
    
    public final Class<T> clazz;
    public final BiFunction<ManipObject<T>, MHRLRequestBody, MHRLOperationResult> lambda;

    public SematicPipelineTypedFunc(Class<T> clazz, BiFunction<ManipObject<T>, MHRLRequestBody, MHRLOperationResult> lambda) {
        this.clazz = clazz;
        this.lambda = lambda;
    }
}
