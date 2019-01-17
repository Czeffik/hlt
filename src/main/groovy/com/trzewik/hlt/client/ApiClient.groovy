package com.trzewik.hlt.client

import io.restassured.RestAssured
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification

class ApiClient {
    private RequestSpecification request
    String baseUrl
    String basePath = ''

    ApiClient build() {
        if (baseUrl == null){
            throw new IllegalArgumentException('Base url is null. Are you forgot set it?')
        }
        RestAssured.baseURI = baseUrl
        this.request = RestAssured
                .given()
                .basePath(basePath)
        return this
    }

    Response post(Object body, String url = '') {
        return request
                .body(body)
                .log()
                .all()
                .post(url)
                .andReturn()
    }

    Response put(Object body, String url) {
        return request
                .body(body)
                .log()
                .all()
                .put(url)
                .andReturn()
    }

    Response patch(Object body, String url) {
        return request
                .body(body)
                .log()
                .all()
                .patch(url)
                .andReturn()
    }

    Response delete(String url) {
        return request
                .log()
                .all()
                .delete(url)
                .andReturn()
    }

    Response get(String url = '', Map<String, ?> queryParams = [:]) {
        return request
                .queryParams(queryParams)
                .log()
                .all()
                .get(url)
                .andReturn()
    }
}
