package org.mnemosynehermesteam.server.mhrl;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
// import java.util.stream.Collectors;

import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.core.Core;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class SematicPipeline {

    private final Core core;
    private final UUID userId;
    private final String key;

    private boolean isAnyType = false;
    private BiFunction<ManipObject<Object>, MHRLRequestBody, MHRLOperationResult> anyTypeFunction = null;

    private final List<SematicPipelineTypedFunc<? extends Object>> funcs = new LinkedList<>();

    private final Map<String, SematicPipelineTypedPredicate<? extends Object>> predicates = new HashMap<>();

    private Function<ManipObject<Object>, MHRLOperationResult> onNull = null;

    private Object defaultVal = null;

    public SematicPipeline(Core core, UUID userId, String key) {
        this.core = core;
        this.userId = userId;
        this.key = key;
    }

    public <T> void attachType(Class<T> clazz, BiFunction<ManipObject<T>, MHRLRequestBody, MHRLOperationResult> lambda) {
        funcs.add(new SematicPipelineTypedFunc<>(clazz, lambda));
    }

    public void attachAnyType(BiFunction<ManipObject<Object>, MHRLRequestBody, MHRLOperationResult> lambda) {
        isAnyType = true;
        anyTypeFunction = lambda;
    }

    public <T> void attachTypeAndPredict(Class<T> clazz, Predicate<ManipObject<T>> pred, BiFunction<ManipObject<T>, MHRLRequestBody, MHRLOperationResult> lambda) {
        predicates.put(clazz.getName(), new SematicPipelineTypedPredicate<>(clazz, pred));
        attachType(clazz, lambda);
    }

    public <T> void addDefault(T val) {
        defaultVal = val;
    }

    public void onNull(Function<ManipObject<Object>, MHRLOperationResult> lambda) {
        onNull = lambda;
    }

    public MHRLOperationResult start(MHRLOperation op, MHRLRequestBody mhrlRequestBody) {
        if ( isAnyType ) {
            log.debug("Starting any type for {} with {} key", op.operationName(), key);
            return anyTypeFunction.apply(new ManipObject<>(core, this.core.readCache(userId, key), key, userId), mhrlRequestBody);
        }
        CacheDTO cacheDTO = this.core.readCache(userId, key);
        if ( cacheDTO == null ) {
            if ( onNull != null ) return onNull.apply(new ManipObject<>(core, cacheDTO, key, userId));
            if ( defaultVal == null ) return MHRLOperationResult.generateNPE(op.operationName(), key);
            cacheDTO = new CacheDTO(UUID.randomUUID(), userId, new Date().getTime(), defaultVal);
            core.saveCache(cacheDTO, key);
        }
        log.debug("starting for {} cache with {} type and {} operation", key, cacheDTO.cacheObject().getClass().getName(), op.operationName());
        // log.debug("Operation {} has assignables: {}", op.operationName(), funcs.stream().map(f -> f.clazz.getName()).collect(Collectors.toList()));
        for ( SematicPipelineTypedFunc<? extends Object> f : funcs ) {
            if ( f.clazz.isAssignableFrom(cacheDTO.cacheObject().getClass()) ) {
                if ( predicates.containsKey(f.clazz.getName()) ) {
                    if ( !predicates.get(f.clazz.getName()).lambda.test(new ManipObject<>(core, cacheDTO, key, userId)) ) {
                        log.debug("Predicate failed for {}", op.operationName());
                        return MHRLOperationResult.generateTypesMismatch(op.operationName(), key);
                    }
                }
                log.debug("Found assignable for {} - {}", key, f.clazz.getName());
                return f.lambda.apply(new ManipObject<>(core, cacheDTO, key, userId), mhrlRequestBody);
            }
        }
        log.debug("Not found assignable type for {} cache, generate 1", key);
        return MHRLOperationResult.generateTypesMismatch(op.operationName(), key);
    }
}
