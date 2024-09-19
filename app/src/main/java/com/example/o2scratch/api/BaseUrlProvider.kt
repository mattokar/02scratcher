package com.example.o2scratch.api

interface BaseUrlProvider {
    var baseUrl: String
}

class BaseUrlProviderImpl(initialBaseUrl: String) : BaseUrlProvider {
    override var baseUrl: String = initialBaseUrl
}