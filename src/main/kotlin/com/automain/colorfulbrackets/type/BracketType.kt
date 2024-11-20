package com.automain.colorfulbrackets.type

import com.intellij.lang.BracePair

interface BracketType {
    fun pairs(): List<BracePair> = emptyList()
}