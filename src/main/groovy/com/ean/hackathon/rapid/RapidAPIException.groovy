package com.ean.hackathon.rapid

/**
 * Created by phari on 6/30/16.
 */
class RapidAPIException extends Exception {

    int httpErrorCode

    public RapidAPIException(int httpErrorCode, String message) {
        super(message);
        this.httpErrorCode = httpErrorCode
    }

    public RapidAPIException(String message) {
        super(message);
    }

}
