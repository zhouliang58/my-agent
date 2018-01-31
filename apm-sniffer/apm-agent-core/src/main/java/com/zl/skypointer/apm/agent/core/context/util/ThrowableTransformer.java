package com.zl.skypointer.apm.agent.core.context.util;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 11:34
 * @since JDK1.8
 */
public enum ThrowableTransformer {
    INSTANCE;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public String convert2String(Throwable throwable, final int maxLength) {
        final StringBuilder stackMessage = new StringBuilder();
        Throwable causeException = throwable;
        while (causeException != null) {
            stackMessage.append(printExceptionInfo(causeException));

            boolean overMaxLength = printStackElement(throwable.getStackTrace(), new AppendListener() {
                public void append(String value) {
                    stackMessage.append(value);
                }

                public boolean overMaxLength() {
                    return stackMessage.length() > maxLength;
                }
            });

            if (overMaxLength) {
                break;
            }

            causeException = throwable.getCause();
        }

        return stackMessage.toString();
    }

    private String printExceptionInfo(Throwable causeException) {
        return causeException.toString() + LINE_SEPARATOR;
    }

    private boolean printStackElement(StackTraceElement[] stackTrace, AppendListener printListener) {
        for (StackTraceElement traceElement : stackTrace) {
            printListener.append("at " + traceElement + LINE_SEPARATOR);
            if (printListener.overMaxLength()) {
                return true;
            }
        }
        return false;
    }

    private interface AppendListener {
        void append(String value);

        boolean overMaxLength();
    }
}
