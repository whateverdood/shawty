package com.googlecode.shawty.pp

import com.googlecode.shawty.Preprocessor

class Lowerer extends Preprocessor {

    @Override
    public String process(String input) {
        return input.toLowerCase();
    }

}
