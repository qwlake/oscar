package com.carrotins.poc.kotlindl.enumeration

enum class Animal(val code: String) {
    DOG("dog"),
    CAT("cat"),
    ;

    companion object {
        fun fromCode(code: String?): Animal? {
            return code?.let { values().firstOrNull { it.code == code } }
        }
    }
}