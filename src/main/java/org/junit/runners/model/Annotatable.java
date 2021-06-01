package org.junit.runners.model;

import java.lang.annotation.Annotation;

/**
 * A model element that may have annotations.
 * 
 * @since 4.12
 */
/*
有注解的模型元素
 */
public interface Annotatable {
    /**
     * Returns the model elements' annotations.
     */
    Annotation[] getAnnotations();

    /**
     * Returns the annotation on the model element of the given type, or @code{null}
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationType);
}
