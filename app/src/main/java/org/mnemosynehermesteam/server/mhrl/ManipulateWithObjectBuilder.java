package org.mnemosynehermesteam.server.mhrl;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.mnemosynehermesteam.core.Core;

public class ManipulateWithObjectBuilder {
    
    private final ManipulateWithObject manip;

    public ManipulateWithObjectBuilder(Core core, UUID userId, String key) {
        manip = new ManipulateWithObject(core, userId, key);
    }
    
    public <T> ManipulateWithObjectBuilder forClass(Class<T> clazz, BiFunction<ManipObject<T>, MHRLRequestBody, MHRLOperationResult> lambda) {
        manip.attachType(clazz, lambda);
        return this;
    }

    public ManipulateWithObjectBuilder forAnyClass(BiFunction<ManipObject<Object>, MHRLRequestBody, MHRLOperationResult> lambda) {
        manip.attachAnyType(lambda);
        return this;
    }

    public <T> ManipulateWithObjectBuilder forClassAndCheck(Class<T> clazz, Predicate<ManipObject<T>> predicate, BiFunction<ManipObject<T>, MHRLRequestBody, MHRLOperationResult> lambda) {
        manip.attachTypeAndPredict(clazz, predicate, lambda);
        return this;
    }

    public ManipulateWithObjectBuilder addDefault(Object defaultVal) {
        manip.addDefault(defaultVal);
        return this;
    }

    public ManipulateWithObjectBuilder onNull(Function<ManipObject<Object>, MHRLOperationResult> lambda) {
        manip.onNull(lambda);
        return this;
    }

    public ManipulateWithObject build() {
        return manip;
    }
}
