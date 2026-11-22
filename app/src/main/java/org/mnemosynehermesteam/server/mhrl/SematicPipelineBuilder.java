package org.mnemosynehermesteam.server.mhrl;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.mnemosynehermesteam.core.Core;

public class SematicPipelineBuilder {
    
    private final SematicPipeline manip;

    public SematicPipelineBuilder(Core core, UUID userId, String key) {
        manip = new SematicPipeline(core, userId, key);
    }
    
    public <T> SematicPipelineBuilder forClass(Class<T> clazz, BiFunction<ManipObject<T>, MHRLRequestBody, MHRLOperationResult> lambda) {
        manip.attachType(clazz, lambda);
        return this;
    }

    public SematicPipelineBuilder forAnyClass(BiFunction<ManipObject<Object>, MHRLRequestBody, MHRLOperationResult> lambda) {
        manip.attachAnyType(lambda);
        return this;
    }

    public <T> SematicPipelineBuilder forClassAndCheck(Class<T> clazz, Predicate<ManipObject<T>> predicate, BiFunction<ManipObject<T>, MHRLRequestBody, MHRLOperationResult> lambda) {
        manip.attachTypeAndPredict(clazz, predicate, lambda);
        return this;
    }

    public SematicPipelineBuilder addDefault(Object defaultVal) {
        manip.addDefault(defaultVal);
        return this;
    }

    public SematicPipelineBuilder onNull(Function<ManipObject<Object>, MHRLOperationResult> lambda) {
        manip.onNull(lambda);
        return this;
    }

    public SematicPipeline build() {
        return manip;
    }
}
