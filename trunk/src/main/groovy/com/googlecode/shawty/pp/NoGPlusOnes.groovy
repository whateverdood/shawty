package com.googlecode.shawty.pp

import com.googlecode.shawty.Preprocessor

class NoGPlusOnes extends Preprocessor {

    @Override
    public String process(String input) {
        return input.replaceAll("g:plusone", "g-plusone");
    }

}
