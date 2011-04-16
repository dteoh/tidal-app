package com.dteoh.tidal.guice;

import java.lang.reflect.Field;

import org.slf4j.Logger;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Listens to types annotated with the {@link InjectLogger} annotation and
 * registers a logger injector with it.
 * 
 * @author Douglas Teoh
 * 
 */
public class LoggerListener implements TypeListener {

    @Override
    public <T> void hear(final TypeLiteral<T> literal,
            final TypeEncounter<T> encounter) {
        for (Field field : literal.getRawType().getDeclaredFields()) {
            if (field.getType() == Logger.class
                    && field.isAnnotationPresent(InjectLogger.class)) {
                encounter.register(new LoggerInjector<T>(field));
            }
        }
    }

}