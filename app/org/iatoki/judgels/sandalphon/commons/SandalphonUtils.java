package org.iatoki.judgels.sandalphon.commons;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import org.apache.commons.codec.binary.Base64;
import org.iatoki.judgels.jophiel.User;
import play.Play;
import play.libs.Json;
import play.mvc.Http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public final class SandalphonUtils {

    public static boolean verifyProblemJid(String problemJid) {
        return true;
//        HTTPRequest httpRequest;
//        try {
//            httpRequest = new HTTPRequest(HTTPRequest.Method.GET, getEndpoint("verifyUser").toURL());
//            httpRequest.setAuthorization("Bearer "+ SandalphonUtils.getEncodedAccessToken());
//            httpRequest.setQuery("userJid=" + userJid);
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            HTTPResponse httpResponse = httpRequest.send();
//            if (httpResponse.getStatusCode() == HTTPResponse.SC_OK) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
