package com.google.shawty;

/**
 * Removes the default xml namespace if one exists b/c XPath can be dumb.
 */
class DefaultNamespaceEliminator extends Preprocessor {

    String process(String input) {
        return input.replaceAll(/xmlns *= *["'].[^"'>]*["']/, "")
    }

}
