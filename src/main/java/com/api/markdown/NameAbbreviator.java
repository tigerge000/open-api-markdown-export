package com.api.markdown;

public class NameAbbreviator {
    private static final int targetLength = 5;
    private static final int MAX_DOTS = 16;
    private static final char DOT = '.';


    public static String abbreviate(String name) {
        if (name.endsWith(">")) {
            int left = name.indexOf("<");
            StringBuilder buf = new StringBuilder();
            buf.append(gene(name.substring(0, left)));
            name = name.substring(left + 1, name.length() - 1);
            String[] split = name.split(",");
            if (split.length > 0) {
                buf.append("<");
                for (String s : split) {
                    buf.append(abbreviate(s));
                    buf.append(",");
                }
                buf.deleteCharAt(buf.length() - 1);
                buf.append(">");
            }
            return buf.toString();
        }
        return gene(name);
    }

    private static int computeDotIndexes(final String className, int[] dotArray) {
        int dotCount = 0;
        int k = 0;
        while (true) {
            // ignore the $ separator in our computations. This is both convenient
            // and sensible.
            k = className.indexOf(DOT, k);
            if (k != -1 && dotCount < MAX_DOTS) {
                dotArray[dotCount] = k;
                dotCount++;
                k++;
            } else {
                break;
            }
        }
        return dotCount;
    }

    private static void computeLengthArray(final String className, int[] dotArray, int[] lengthArray, int dotCount) {
        int toTrim = className.length() - targetLength;
        // System.out.println("toTrim=" + toTrim);

        // int toTrimAvarage = 0;

        int len;
        for (int i = 0; i < dotCount; i++) {
            int previousDotPosition = -1;
            if (i > 0) {
                previousDotPosition = dotArray[i - 1];
            }
            int available = dotArray[i] - previousDotPosition - 1;
            if (toTrim > 0) {
                len = (available < 1) ? available : 1;
            } else {
                len = available;
            }
            toTrim -= (available - len);
            lengthArray[i] = len + 1;
        }

        int lastDotIndex = dotCount - 1;
        lengthArray[dotCount] = className.length() - dotArray[lastDotIndex];
    }

    private static String gene(String fqClassName) {
        StringBuilder buf = new StringBuilder(targetLength);
        int inLen = fqClassName.length();
        if (inLen < targetLength) {
            return fqClassName;
        }

        int[] dotIndexesArray = new int[MAX_DOTS];
        // a.b.c contains 2 dots but 2+1 parts.
        // see also http://jira.qos.ch/browse/LBCLASSIC-110
        int[] lengthArray = new int[MAX_DOTS + 1];

        int dotCount = computeDotIndexes(fqClassName, dotIndexesArray);

        // System.out.println();
        // System.out.println("Dot count for [" + className + "] is " + dotCount);
        // if there are not dots than abbreviation is not possible
        if (dotCount == 0) {
            return fqClassName;
        }
        computeLengthArray(fqClassName, dotIndexesArray, lengthArray, dotCount);
        for (int i = 0; i <= dotCount; i++) {
            if (i == 0) {
                buf.append(fqClassName, 0, lengthArray[i] - 1);
            } else {
                buf.append(fqClassName, dotIndexesArray[i - 1], dotIndexesArray[i - 1] + lengthArray[i]);
            }
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        String name = "java.util.List<com.ggj.open.platform.export.sample.pojo.GeneObj<com.ggj.open.platform.export.sample.pojo.Name>>";
        String gene = abbreviate(name);
        System.out.println(gene);
    }
}
