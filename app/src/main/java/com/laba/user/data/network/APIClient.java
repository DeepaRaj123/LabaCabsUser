package com.laba.user.data.network;

import androidx.annotation.NonNull;

import android.util.Log;

import com.laba.user.BuildConfig;
import com.laba.user.MvpApplication;
import com.laba.user.data.SharedHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {


    private static Retrofit retrofit = null;

    public static ApiInterface getAPIClient() {
        if (retrofit == null) {
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(getHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(ApiInterface.class);
    }

    /*public static KeyStore createEmptyKeyStore(char[] keyStorePassword)
            throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, keyStorePassword);
        return keyStore;
    }*/

    /*private static OkHttpClient getHttpClient() throws Exception{
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

//        InputStream trustedCertificateAsInputStream = Files.newInputStream(Paths.get("/path/to/server-certificate.pem"), StandardOpenOption.READ);
        String pemFile = "-----BEGIN CERTIFICATE-----" +
                "MIIGZTCCBU2gAwIBAgIIcdm6zdrng+QwDQYJKoZIhvcNAQELBQAwgcYxCzAJBgNV" +
                "BAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQHEwpTY290dHNkYWxlMSUw" +
                "IwYDVQQKExxTdGFyZmllbGQgVGVjaG5vbG9naWVzLCBJbmMuMTMwMQYDVQQLEypo" +
                "dHRwOi8vY2VydHMuc3RhcmZpZWxkdGVjaC5jb20vcmVwb3NpdG9yeS8xNDAyBgNV" +
                "BAMTK1N0YXJmaWVsZCBTZWN1cmUgQ2VydGlmaWNhdGUgQXV0aG9yaXR5IC0gRzIw" +
                "HhcNMjEwNDA0MTM0MjE0WhcNMjIwNDA0MTM0MjE0WjA+MSEwHwYDVQQLExhEb21h" +
                "aW4gQ29udHJvbCBWYWxpZGF0ZWQxGTAXBgNVBAMTEHd3dy5sYWJhY2Ficy5jb20w" +
                "ggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQChVcM4H34I44KuB2Oib/3S" +
                "Ue0w5jxn2CKKvAoZsRifDSaaH4sM0rsKzEz14GuRAJ/EWbwZ8zraNfmN99BvM04U" +
                "kkyW5KCnDTSeOpsH0c/LObGWZNNFUSH4PFPI6ltjQtsO9IXISIsy1f7jc2Mpmld/" +
                "xD5U5mxB/CvzQaPvGFEKyxD9O5CIBb8l+Nq6MYbBNx/PqeCJhYnWo4XXWiCBNKOm" +
                "eYTdmr/YjaKjqMRI0xRqUuJIefU3SJ22YtOrwagghM16uYx7xcJ4LZ5XR/9fYUwO" +
                "C1GTxFD6TyQfzxjpJ0euG2Rj2rttBqY7LTfJUo6Nrp16aUCu9Sif9suAUEn3Ll5d" +
                "AgMBAAGjggLcMIIC2DAMBgNVHRMBAf8EAjAAMB0GA1UdJQQWMBQGCCsGAQUFBwMB" +
                "BggrBgEFBQcDAjAOBgNVHQ8BAf8EBAMCBaAwPQYDVR0fBDYwNDAyoDCgLoYsaHR0" +
                "cDovL2NybC5zdGFyZmllbGR0ZWNoLmNvbS9zZmlnMnMxLTI5Mi5jcmwwYwYDVR0g" +
                "BFwwWjBOBgtghkgBhv1uAQcXATA/MD0GCCsGAQUFBwIBFjFodHRwOi8vY2VydGlm" +
                "aWNhdGVzLnN0YXJmaWVsZHRlY2guY29tL3JlcG9zaXRvcnkvMAgGBmeBDAECATCB" +
                "ggYIKwYBBQUHAQEEdjB0MCoGCCsGAQUFBzABhh5odHRwOi8vb2NzcC5zdGFyZmll" +
                "bGR0ZWNoLmNvbS8wRgYIKwYBBQUHMAKGOmh0dHA6Ly9jZXJ0aWZpY2F0ZXMuc3Rh" +
                "cmZpZWxkdGVjaC5jb20vcmVwb3NpdG9yeS9zZmlnMi5jcnQwHwYDVR0jBBgwFoAU" +
                "JUWBaFAmOD07LSy+zWrZtj2zZmMwKQYDVR0RBCIwIIIQd3d3LmxhYmFjYWJzLmNv" +
                "bYIMbGFiYWNhYnMuY29tMB0GA1UdDgQWBBSFV4YkvLTp7obxvS0FqaKX9ZAQ5DCC" +
                "AQMGCisGAQQB1nkCBAIEgfQEgfEA7wB2ACl5vvCeOTkh8FZzn2Old+W+V32cYAr4" +
                "+U1dJlwlXceEAAABeJ0erqcAAAQDAEcwRQIgL0bvN7k0VWbFWsOm8+lidT6Tgsch" +
                "dmC01m1u5vhnMU0CIQCL5YZoJoss/9IfvF30I3dj7+1McsHKefVYO2FYP0TplwB1" +
                "ACJFRQdZVSRWlj+hL/H3bYbgIyZjrcBLf13Gg1xu4g8CAAABeJ0er9oAAAQDAEYw" +
                "RAIgAoyI+BYDuF5U6rFG4t1yYA3b6ta9qCUxSqhRo/3O0UICIG8ESyulgf3PSn5A" +
                "AyfAwB2Plr/Xr77w9TQ4XQKsk2PJMA0GCSqGSIb3DQEBCwUAA4IBAQCaevdLSYEO" +
                "MtAhOb3Zdh8xuCyALf9kJNJ1uJD2hQ/ePqSXjng4+/e4c+CaNTddXo3yZ7YkT5Tq" +
                "oAPCLfIAKkR+vRPhOt/FOmkJiZAmDiAsJya/DM95YF+Ctzs8jnwj1SwOCDLHi9PQ" +
                "tQqzDfBBZpe7RW3YnsLsj94E/omRiHbQaq+5aSkph3bUDpmWz7DYgU9pt+ae2aWc" +
                "i0v/YhHGRghpULoDrsCgwGsnEBXWHqLWwFnqEO1eSzbZWsXortzKH5Lgad1cq6Yl" +
                "5+HYhPsQxL1fvkZbRsKrt21pWbLiZj4mSISl86bWej1vDkr5BZh1xPYMWXb4hR8E" +
                "o6RFdTOCdEpp" +
                "-----END CERTIFICATE-----";
        InputStream trustedCertificateAsInputStream = new ByteArrayInputStream(
                pemFile.getBytes(StandardCharsets.UTF_8));;
        Certificate trustedCertificate = certificateFactory.generateCertificate(trustedCertificateAsInputStream);
        KeyStore trustStore = createEmptyKeyStore("secret".toCharArray());
        trustStore.setCertificateEntry("server-certificate", trustedCertificate);

        *//*String privateKeyContent = new String(Files.readAllBytes(Paths.get("/path/to/mykey.key")), Charset.defaultCharset())
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");*//*
        String privateKeyContent = "MIIGZTCCBU2gAwIBAgIIcdm6zdrng+QwDQYJKoZIhvcNAQELBQAwgcYxCzAJBgNV" +
                "BAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQHEwpTY290dHNkYWxlMSUw" +
                "IwYDVQQKExxTdGFyZmllbGQgVGVjaG5vbG9naWVzLCBJbmMuMTMwMQYDVQQLEypo" +
                "dHRwOi8vY2VydHMuc3RhcmZpZWxkdGVjaC5jb20vcmVwb3NpdG9yeS8xNDAyBgNV" +
                "BAMTK1N0YXJmaWVsZCBTZWN1cmUgQ2VydGlmaWNhdGUgQXV0aG9yaXR5IC0gRzIw" +
                "HhcNMjEwNDA0MTM0MjE0WhcNMjIwNDA0MTM0MjE0WjA+MSEwHwYDVQQLExhEb21h" +
                "aW4gQ29udHJvbCBWYWxpZGF0ZWQxGTAXBgNVBAMTEHd3dy5sYWJhY2Ficy5jb20w" +
                "ggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQChVcM4H34I44KuB2Oib/3S" +
                "Ue0w5jxn2CKKvAoZsRifDSaaH4sM0rsKzEz14GuRAJ/EWbwZ8zraNfmN99BvM04U" +
                "kkyW5KCnDTSeOpsH0c/LObGWZNNFUSH4PFPI6ltjQtsO9IXISIsy1f7jc2Mpmld/" +
                "xD5U5mxB/CvzQaPvGFEKyxD9O5CIBb8l+Nq6MYbBNx/PqeCJhYnWo4XXWiCBNKOm" +
                "eYTdmr/YjaKjqMRI0xRqUuJIefU3SJ22YtOrwagghM16uYx7xcJ4LZ5XR/9fYUwO" +
                "C1GTxFD6TyQfzxjpJ0euG2Rj2rttBqY7LTfJUo6Nrp16aUCu9Sif9suAUEn3Ll5d" +
                "AgMBAAGjggLcMIIC2DAMBgNVHRMBAf8EAjAAMB0GA1UdJQQWMBQGCCsGAQUFBwMB" +
                "BggrBgEFBQcDAjAOBgNVHQ8BAf8EBAMCBaAwPQYDVR0fBDYwNDAyoDCgLoYsaHR0" +
                "cDovL2NybC5zdGFyZmllbGR0ZWNoLmNvbS9zZmlnMnMxLTI5Mi5jcmwwYwYDVR0g" +
                "BFwwWjBOBgtghkgBhv1uAQcXATA/MD0GCCsGAQUFBwIBFjFodHRwOi8vY2VydGlm" +
                "aWNhdGVzLnN0YXJmaWVsZHRlY2guY29tL3JlcG9zaXRvcnkvMAgGBmeBDAECATCB" +
                "ggYIKwYBBQUHAQEEdjB0MCoGCCsGAQUFBzABhh5odHRwOi8vb2NzcC5zdGFyZmll" +
                "bGR0ZWNoLmNvbS8wRgYIKwYBBQUHMAKGOmh0dHA6Ly9jZXJ0aWZpY2F0ZXMuc3Rh" +
                "cmZpZWxkdGVjaC5jb20vcmVwb3NpdG9yeS9zZmlnMi5jcnQwHwYDVR0jBBgwFoAU" +
                "JUWBaFAmOD07LSy+zWrZtj2zZmMwKQYDVR0RBCIwIIIQd3d3LmxhYmFjYWJzLmNv" +
                "bYIMbGFiYWNhYnMuY29tMB0GA1UdDgQWBBSFV4YkvLTp7obxvS0FqaKX9ZAQ5DCC" +
                "AQMGCisGAQQB1nkCBAIEgfQEgfEA7wB2ACl5vvCeOTkh8FZzn2Old+W+V32cYAr4" +
                "+U1dJlwlXceEAAABeJ0erqcAAAQDAEcwRQIgL0bvN7k0VWbFWsOm8+lidT6Tgsch" +
                "dmC01m1u5vhnMU0CIQCL5YZoJoss/9IfvF30I3dj7+1McsHKefVYO2FYP0TplwB1" +
                "ACJFRQdZVSRWlj+hL/H3bYbgIyZjrcBLf13Gg1xu4g8CAAABeJ0er9oAAAQDAEYw" +
                "RAIgAoyI+BYDuF5U6rFG4t1yYA3b6ta9qCUxSqhRo/3O0UICIG8ESyulgf3PSn5A" +
                "AyfAwB2Plr/Xr77w9TQ4XQKsk2PJMA0GCSqGSIb3DQEBCwUAA4IBAQCaevdLSYEO" +
                "MtAhOb3Zdh8xuCyALf9kJNJ1uJD2hQ/ePqSXjng4+/e4c+CaNTddXo3yZ7YkT5Tq" +
                "oAPCLfIAKkR+vRPhOt/FOmkJiZAmDiAsJya/DM95YF+Ctzs8jnwj1SwOCDLHi9PQ" +
                "tQqzDfBBZpe7RW3YnsLsj94E/omRiHbQaq+5aSkph3bUDpmWz7DYgU9pt+ae2aWc" +
                "i0v/YhHGRghpULoDrsCgwGsnEBXWHqLWwFnqEO1eSzbZWsXortzKH5Lgad1cq6Yl" +
                "5+HYhPsQxL1fvkZbRsKrt21pWbLiZj4mSISl86bWej1vDkr5BZh1xPYMWXb4hR8E" +
                "o6RFdTOCdEpp" +
                "-----END RSA PRIVATE KEY-----";

        privateKeyContent
                 .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] privateKeyAsBytes = Base64.getDecoder().decode(privateKeyContent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyAsBytes);

        InputStream certificateChainAsInputStream = Files.newInputStream(Paths.get("/path/to/mycert.pem"), StandardOpenOption.READ);
        Certificate certificateChain = certificateFactory.generateCertificate(certificateChainAsInputStream);

        KeyStore identityStore = createEmptyKeyStore("secret".toCharArray());
        identityStore.setKeyEntry("client", keyFactory.generatePrivate(keySpec), "secret".toCharArray(), new Certificate[]{certificateChain});

        trustedCertificateAsInputStream.close();
        certificateChainAsInputStream.close();

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(identityStore, "secret".toCharArray());
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        *//*OkHttpClient okHttpClient = OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManagers[0])
                .build();*//*
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient().newBuilder()
                //.cache(new Cache(MvpApplication.getInstance().getCacheDir(), 10 * 1024 * 1024)) // 10 MB
                .connectTimeout(10, TimeUnit.SECONDS)
                .addNetworkInterceptor(new AddHeaderInterceptor())
                .sslSocketFactory(sslSocketFactory)
//                .addNetworkInterceptor(new StethoInterceptor())
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(interceptor)
                .build();
    }*/

    private static OkHttpClient getHttpClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient().newBuilder()
                //.cache(new Cache(MvpApplication.getInstance().getCacheDir(), 10 * 1024 * 1024)) // 10 MB
                .connectTimeout(10, TimeUnit.SECONDS)
                .addNetworkInterceptor(new AddHeaderInterceptor())
//                .hostnameVerifier((hostname, sslSession) -> hostname.equals("labacabs.com"))
//                .addNetworkInterceptor(new StethoInterceptor())
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(interceptor)
                .build();
    }

    private static class AddHeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("X-Requested-With", "XMLHttpRequest");
            builder.addHeader(
                    "Authorization",
                    SharedHelper.getKey(MvpApplication.getInstance(), "access_token", ""));
            Log.d(" TOKEN", SharedHelper.getKey(MvpApplication.getInstance(), "access_token", ""));
//            String value = SharedHelper.getKey(MvpApplication.getInstance(),"access_token");
//            Log.e("data",value);
            return chain.proceed(builder.build());
        }
    }
}
