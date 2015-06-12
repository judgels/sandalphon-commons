package org.iatoki.judgels.sandalphon.commons;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.iatoki.judgels.sandalphon.commons.programming.LanguageRestriction;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public final class Sandalphon implements BundleProblemGrader {

    private static final String ENCODING= "UTF-8";
    private static final String TOTP_ENCRYPTION_ALGORITHM =  "HmacSHA1";

    private final String baseUrl;
    private final String clientJid;
    private final String clientSecret;

    public Sandalphon(String baseUrl, String clientJid, String clientSecret) {
        this.baseUrl = baseUrl;
        this.clientJid = clientJid;
        this.clientSecret = clientSecret;
    }

    public String verifyLessonJid(String lessonJid) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();

        List<NameValuePair> params = ImmutableList.of(
              new BasicNameValuePair("clientJid", clientJid),
              new BasicNameValuePair("clientSecret", clientSecret),
              new BasicNameValuePair("lessonJid", lessonJid)
        );

        HttpGet request = new HttpGet(getEndpoint("/verifyLesson", params));

        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return IOUtils.toString(response.getEntity().getContent());
        } else {
            return null;
        }
    }

    public String verifyProblemJid(String problemJid) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();

        List<NameValuePair> params = ImmutableList.of(
              new BasicNameValuePair("clientJid", clientJid),
              new BasicNameValuePair("clientSecret", clientSecret),
              new BasicNameValuePair("problemJid", problemJid)
        );

        HttpGet request = new HttpGet(getEndpoint("/verifyProblem", params));

        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return IOUtils.toString(response.getEntity().getContent());
        } else {
            return null;
        }
    }

    @Override
    public BundleGradingResult gradeBundleProblem(String problemJid, BundleAnswer bundleAnswer) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();

        List<NameValuePair> params = ImmutableList.of(
                new BasicNameValuePair("clientJid", clientJid),
                new BasicNameValuePair("clientSecret", clientSecret),
                new BasicNameValuePair("problemJid", problemJid),
                new BasicNameValuePair("answer", new Gson().toJson(bundleAnswer))
        );

        HttpGet request = new HttpGet(getEndpoint("/problem/bundle/grade", params));

        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return new Gson().fromJson(IOUtils.toString(response.getEntity().getContent()), BundleGradingResult.class);
        } else {
            return null;
        }
    }

    public URI getLessonStatementRenderUri() {
        return getEndpoint("/lesson/totp/statement");
    }

    public String getLessonStatementRenderRequestBody(String lessonJid, String lessonSecret, long currentMillis, String lang, String switchLanguageUri) {
        int tOTP = calculateTOTPCode(lessonSecret, currentMillis);
        List<NameValuePair> params = ImmutableList.of(
              new BasicNameValuePair("clientJid", clientJid),
              new BasicNameValuePair("lessonJid", lessonJid),
              new BasicNameValuePair("TOTP", tOTP + ""),
              new BasicNameValuePair("lang", lang),
              new BasicNameValuePair("switchLanguageUri", switchLanguageUri)
        );
        return URLEncodedUtils.format(params, ENCODING);
    }

    public URI getLessonMediaRenderUri(String lessonJid, String mediaName) {
        return getEndpoint("/lessons/" + lessonJid + "/render/" + mediaName);
    }

    public URI getProblemStatementRenderUri() {
        return getEndpoint("/problem/totp/statement");
    }

    public String getProblemStatementRenderRequestBody(String problemJid, String problemSecret, long currentMillis, String lang, String postSubmitUri, String switchLanguageUri, String reasonNotAllowedToSubmit, LanguageRestriction languageRestriction) {
        int tOTP = calculateTOTPCode(problemSecret, currentMillis);
        List<NameValuePair> params = ImmutableList.of(
              new BasicNameValuePair("clientJid", clientJid),
              new BasicNameValuePair("problemJid", problemJid),
              new BasicNameValuePair("TOTP", tOTP + ""),
              new BasicNameValuePair("lang", lang),
              new BasicNameValuePair("postSubmitUri", postSubmitUri),
              new BasicNameValuePair("switchLanguageUri", switchLanguageUri),
              new BasicNameValuePair("reasonNotAllowedToSubmit", reasonNotAllowedToSubmit),
              new BasicNameValuePair("languageRestriction", new Gson().toJson(languageRestriction))
        );
        return URLEncodedUtils.format(params, ENCODING);
    }

    public URI getProblemMediaRenderUri(String problemJid, String mediaName) {
        return getEndpoint("/problems/" + problemJid + "/render/" + mediaName);
    }

    private URI getEndpoint(String path) {
        return getEndpoint(path, ImmutableList.of());
    }

    private URI getEndpoint(String path, List<NameValuePair> params) {
        try {
            URIBuilder uriBuilder = new URIBuilder(baseUrl);
            uriBuilder.setPath(path);
            uriBuilder.setParameters(params);

            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("sandalphon.baseUrl malformed in configuration");
        }
    }

    private int calculateTOTPCode(String keyString, long timeMillis) {
        long totpMod = 1000000;
        long timeStep = 30000;

        byte[] key = keyString.getBytes();
        byte[] data = new byte[8];
        long value = timeMillis / timeStep;

        for(int signKey = 8; signKey-- > 0; value >>>= 8) {
            data[signKey] = (byte)((int)value);
        }

        SecretKeySpec var15 = new SecretKeySpec(key, TOTP_ENCRYPTION_ALGORITHM);

        try {
            Mac ex = Mac.getInstance(TOTP_ENCRYPTION_ALGORITHM);
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
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("The operation cannot be performed now.", e);
        }
    }
}
