package com.google.shawty;

class DoctypeEliminator extends Preprocessor {

    String process(String input) {
        return input.replaceAll(/<!DOCTYPE.*?>/, "")
    }

}
