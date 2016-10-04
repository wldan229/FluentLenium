package org.fluentlenium.core.wait;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.conditions.Conditions;
import org.fluentlenium.core.conditions.message.MessageProxy;

/**
 * Common class for all WaitMatcher instances.
 */
public class BaseWaitMatcher {
    /**
     * Build the final message from default message.
     *
     * @return final message
     */
    protected Function<String, String> messageCustomizer() {
        return Functions.identity();
    }

    /**
     * Perform the wait.
     *
     * @param wait    fluent wait object.
     * @param present predicate to wait for.
     * @param message message to use.
     */
    protected void until(final FluentWait wait, final Predicate<FluentControl> present, String message) {
        if (wait.useCustomMessage()) {
            wait.untilPredicate(present);
        } else {
            message = messageCustomizer().apply(message);
            wait.withMessage(message).untilPredicate(present);
        }
    }

    /**
     * Perform the wait.
     *
     * @param wait            fluent wait object.
     * @param present         predicate to wait for.
     * @param messageSupplier default message to use.
     */
    protected void until(final FluentWait wait, final Predicate<FluentControl> present, final Supplier<String> messageSupplier) {
        if (wait.useCustomMessage()) {
            wait.untilPredicate(present);
        } else {
            final Supplier<String> customMessageSupplier = new Supplier<String>() {
                @Override
                public String get() {
                    return messageCustomizer().apply(messageSupplier.get());
                }
            };
            wait.withMessage(customMessageSupplier).untilPredicate(present);
        }
    }

    /**
     * Perform the wait.
     *
     * @param wait              fluent wait object
     * @param condition         condition object to wait for
     * @param messageBuilder    message builder matching the condition object
     * @param conditionFunction condition fonction
     * @param <T>               type of the condition.
     */
    protected <T extends Conditions<?>> void until(final FluentWait wait, final T condition, final T messageBuilder,
            final Function<T, Boolean> conditionFunction) {
        final Predicate<FluentControl> predicate = new Predicate<FluentControl>() {
            @Override
            public boolean apply(final FluentControl input) {
                return conditionFunction.apply(condition);
            }
        };
        final Supplier<String> messageSupplier = new Supplier<String>() {
            @Override
            public String get() {
                conditionFunction.apply(messageBuilder);
                return MessageProxy.message(messageBuilder);
            }
        };

        until(wait, predicate, messageSupplier);
    }
}
