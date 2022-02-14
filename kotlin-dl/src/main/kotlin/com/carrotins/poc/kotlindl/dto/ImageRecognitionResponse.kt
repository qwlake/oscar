package com.carrotins.poc.kotlindl.dto

import com.fasterxml.jackson.annotation.JsonProperty

class ImageRecognitionResponse(
    @JsonProperty("object_name")
    val objectName: String
)