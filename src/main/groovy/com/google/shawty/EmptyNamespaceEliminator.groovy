package com.google.shawty;

/**
 * Removes empty xml namespaces if they exists b/c XPath can be dumb.
 */
class EmptyNamespaceEliminator extends Preprocessor {

    String process(String input) {
        return input.replaceAll(/xmlns *= *["']["']/, "")
    }

}
