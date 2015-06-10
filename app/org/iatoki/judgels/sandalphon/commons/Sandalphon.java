package org.iatoki.judgels.sandalphon.commons;

import com.google.gson.Gson;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.iatoki.judgels.sandalphon.commons.programming.LanguageRestriction;
import play.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public final class Sandalphon implements BundleProblemGrader {
    private final String baseUrl;
    private final String clientJid;
    private final String clientSecret;

    public Sandalphon(String baseUrl, String clientJid, String clientSecret) {
        this.baseUrl = baseUrl;
        this.clientJid = clientJid;
        this.clientSecret = clientSecret;
    }

    public String verifyLessonJid(String lessonJid) {
        HTTPRequest httpRequest;
        try {
            httpRequest = new HTTPRequest(HTTPRequest.Method.GET, getEndpoint("verifyLesson").toURL());
            httpRequest.setQuery("clientJid=" + clientJid + "&clientSecret=" + clientSecret + "&lessonJid=" + lessonJid);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            HTTPResponse httpResponse = httpRequest.send();
            if (httpResponse.getStatusCode() == HTTPResponse.SC_OK) {
                return httpResponse.getContent();
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public URI getLessonRenderUri(String lessonJid, String imageName) {
        try {
            return getEndpoint("lessons/" + lessonJid + "/render/" + URLEncoder.encode(imageName, "UTF-8").replaceAll("\\+", "%20")).toURL().toURI();
        } catch (UnsupportedEncodingException | MalformedURLException | URISyntaxException e) {
            return null;
        }
    }

    public String verifyProblemJid(String problemJid) {
        HTTPRequest httpRequest;
        try {
            httpRequest = new HTTPRequest(HTTPRequest.Method.GET, getEndpoint("verifyProblem").toURL());
            httpRequest.setQuery("clientJid=" + clientJid + "&clientSecret=" + clientSecret + "&problemJid=" + problemJid);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            HTTPResponse httpResponse = httpRequest.send();
            if (httpResponse.getStatusCode() == HTTPResponse.SC_OK) {
                return httpResponse.getContent();
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public URI getProblemRenderUri(String problemJid, String imageName) {
        try {
            return getEndpoint("problems/" + problemJid + "/render/" + URLEncoder.encode(imageName, "UTF-8").replaceAll("\\+", "%20")).toURL().toURI();
        } catch (UnsupportedEncodingException | MalformedURLException | URISyntaxException e) {
            return null;
        }
    }

    @Override
    public BundleGradingResult gradeBundleProblem(String problemJid, BundleAnswer bundleAnswer) throws IOException {
        HTTPRequest httpRequest;
        try {
            httpRequest = new HTTPRequest(HTTPRequest.Method.POST, getEndpoint("problem/bundle/grade").toURL());
            httpRequest.setQuery("clientJid=" + clientJid + "&clientSecret=" + clientSecret + "&problemJid=" + problemJid + "&answer=" + URLEncoder.encode(new Gson().toJson(bundleAnswer), "UTF-8"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            HTTPResponse httpResponse = httpRequest.send();
            if (httpResponse.getStatusCode() == HTTPResponse.SC_OK) {
                return new Gson().fromJson(httpResponse.getContent(), BundleGradingResult.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int calculateTOTPCode(String keyString, long tm) {
        long totpMod = 1000000;
        long timeStep = 30000;

        byte[] key = keyString.getBytes();
        byte[] data = new byte[8];
        long value = tm / timeStep;

        for(int signKey = 8; signKey-- > 0; value >>>= 8) {
            data[signKey] = (byte)((int)value);
        }

        SecretKeySpec var15 = new SecretKeySpec(key, "HmacSHA1");

        try {
            Mac ex = Mac.getInstance("HmacSHA1");
            ex.init(var15);
            byte[] hash = ex.doFinal(data);
            int offset = hash[hash.length - 1] & 15;
            long truncatedHash = 0L;

            for(int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (long)(hash[offset + i] & 255);
            }

            truncatedHash &= 2147483647L;
            truncatedHash %= totpMod;
            return (int)truncatedHash;
        } catch (InvalidKeyException | NoSuchAlgorithmException var14) {
            Logger.warn(var14.getMessage(), var14);
            throw new RuntimeException("The operation cannot be performed now.");
        }
    }

    public URI getLessonTOTPEndpoint() {
        return getEndpoint("lesson/totp/statement");
    }

    public String getLessonTOTPRequestBody(String lessonJid, int tOTP, String lang, String switchLanguageUri) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("clientJid", clientJid));
        params.add(new BasicNameValuePair("lessonJid", lessonJid));
        params.add(new BasicNameValuePair("TOTP", tOTP + ""));
        params.add(new BasicNameValuePair("lang", lang));
        params.add(new BasicNameValuePair("switchLanguageUri", switchLanguageUri));
        return URLEncodedUtils.format(params, "UTF-8");
    }

    public URI getProblemTOTPEndpoint() {
        return getEndpoint("problem/totp/statement");
    }

    public String getProblemTOTPRequestBody(String problemJid, int tOTP, String lang, String postSubmitUri, String switchLanguageUri, String reasonNotAllowedToSubmit, LanguageRestriction languageRestriction) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("clientJid", clientJid));
        params.add(new BasicNameValuePair("problemJid", problemJid));
        params.add(new BasicNameValuePair("TOTP", tOTP + ""));
        params.add(new BasicNameValuePair("lang", lang));
        params.add(new BasicNameValuePair("postSubmitUri", postSubmitUri));
        params.add(new BasicNameValuePair("switchLanguageUri", switchLanguageUri));
        params.add(new BasicNameValuePair("reasonNotAllowedToSubmit", reasonNotAllowedToSubmit));
        params.add(new BasicNameValuePair("languageRestriction", new Gson().toJson(languageRestriction)));
        return URLEncodedUtils.format(params, "UTF-8");
    }

    public URI getEndpoint(String service) {
        try {
            return new URI(baseUrl + "/" + service);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("sandalphon.baseUrl malformed in configuration");
        }
    }
}
