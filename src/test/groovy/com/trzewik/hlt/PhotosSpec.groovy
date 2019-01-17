package com.trzewik.hlt

import com.trzewik.hlt.client.ApiClient
import com.trzewik.hlt.model.Photo
import io.restassured.response.Response
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Unroll

/*
All below tests are for fake server:
https://github.com/typicode/jsonplaceholder#how-to
'Note: the resource will not be really created on the server but it will be faked as if.'
 */

class PhotosSpec extends Specification {
    private String baseUrl = 'https://jsonplaceholder.typicode.com/'
    private String basePath = 'photos'
    private ApiClient apiClient

    def setup() {
        apiClient = new ApiClient(
                baseUrl: baseUrl,
                basePath: basePath)
                .build()
    }

    @Unroll
    def "Should be possible get all #photosNumber photos"() {
        when:
        Response response = apiClient.get()

        then:
        println response.statusCode()

        when:
        List<Photo> photos = response.body().as(Photo[].class)

        then:
        photos.size() == photosNumber

        where:
        photosNumber = 5000
    }

    @Unroll
    def "Should return single photo object with id #photoId"() {
        when:
        Response response = apiClient.get(photoId)

        then:
        response.statusCode() == 200

        when:
        Photo photo = response.body().as(Photo.class)

        then:
        photo.getId() == Integer.parseInt(photoId)

        where:
        photoId = '1'
    }

    @Unroll
    def "Should not be possible get photo with id: #photoId because resource not found"() {
        when:
        Response response = apiClient.get(photoId)

        then:
        response.statusCode() == 404

        where:
        photoId = '5001'
    }

    @Unroll
    def "Should return photo objects with album id: #queryParameters.albumId"() {
        when:
        Response response = apiClient.get('', queryParameters)

        then:
        response.statusCode() == 200

        when:
        List<Photo> photos = response.body().as(Photo[].class)

        then:
        photos.each {
            assert it.getAlbumId() == queryParameters.albumId
        }

        where:
        queryParameters = ['albumId': 100]
    }

    @Unroll
    def "Should return empty list because albumId #queryParameters.albumId does not exist."() {
        when:
        Response response = apiClient.get('', queryParameters)

        then:
        response.statusCode() == 200

        when:
        List<Photo> photos = response.body().as(Photo[].class)

        then:
        photos.size() == 0

        where:
        queryParameters = ['albumId': 101]
    }

    def "Should be possible upload new photo"() {
        when:
        Response response = apiClient.post(photo)

        then:
        response.statusCode() == 201

        where:
        photo = new Photo(
                albumId: 1,
                id: 5002,
                title: 'example title',
                url: 'https://example.photo.url',
                thumbnailUrl: 'https://example.photo.thumbnailUrl'
        )
    }

    def "Should not be possible upload new photo instead old one"() {
        when:
        Response response = apiClient.post(photo, photo.id.toString())

        then:
        response.statusCode() == 404

        where:
        photo = new Photo(
                albumId: 1,
                id: 1,
                title: 'example title',
                url: 'https://example.photo.url',
                thumbnailUrl: 'https://example.photo.thumbnailUrl'
        )
    }

    @Unroll
    def "Should be possible replace old photo with id: #photo.id with new one"() {
        when:
        Response response = apiClient.put(photo, photo.id.toString())

        then:
        response.statusCode() == 200

        where:
        photo = new Photo(
                albumId: 1,
                id: 2,
                title: 'example title',
                url: 'https://example.photo.url',
                thumbnailUrl: 'https://example.photo.thumbnailUrl'
        )
    }

    @Issue("Currently server returning 500")
    @Unroll
    def "Should not be possible replace old photo with id: #photo.id with new one"() {
        when:
        Response response = apiClient.put(photo, photo.id.toString())

        then:
        response.statusCode() == 404

        where:
        photo = new Photo(
                albumId: 1,
                id: 0,
                title: 'example title',
                url: 'https://example.photo.url',
                thumbnailUrl: 'https://example.photo.thumbnailUrl'
        )
    }

    @Unroll
    def "Should be possible change title in photo with id: #photo.id to #photo.title"() {
        when:
        Response response = apiClient.patch(photo, photo.id.toString())

        then:
        response.statusCode() == 200

        where:
        photo = ['id': 2, 'title': 'new photo title']
    }

    @Issue('Currently server returning 200')
    @Unroll
    def "Should not be possible change title in photo with id: #photo.id because photo does not exist"() {
        when:
        Response response = apiClient.patch(photo, photo.id.toString())

        then:
        response.statusCode() == 404

        where:
        photo = ['id': 0, 'title': 'new photo title']
    }

    @Unroll
    def "Should be possible delete photo with id: #photoId"() {
        when:
        Response response = apiClient.delete(photoId.toString())

        then:
        response.statusCode() == 200

        where:
        photoId = 1
    }
}