package com.ean.hackathon.rapid

import com.ean.messagecorrelation.httpclient.MessageCorrelationInterceptor
import groovy.util.logging.Slf4j
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.SSLContextBuilder
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.DefaultConnectionReuseStrategy
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException

/**
 * Factory class to create HttpClient with SSL.
 */
@Slf4j
class HttpClientFactory {
    private static final String SCHEME_HTTP = "http"
    private static final String SCHEME_SECURE_HTTP = "https"

    /**
     * HTTP client which provides secure HTTP over SSL with the specified keystore.
     *
     * @return HTTP client with HTTP over SSL
     * @throws Exception
     */
    public HttpClient getObject() throws Exception {

        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsocketFactory = new SSLConnectionSocketFactory(
                    builder.build());

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register(SCHEME_HTTP, PlainConnectionSocketFactory.getSocketFactory())
                    .register(SCHEME_SECURE_HTTP, sslsocketFactory)
                    .build()

            PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry)

            HttpClient httpClient = HttpClients.custom()
                    .addInterceptorFirst(new MessageCorrelationInterceptor())
                    .setConnectionManager(manager)
                    .setSSLSocketFactory(sslsocketFactory)
                    .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy())
                    .setDefaultRequestConfig(
                    RequestConfig.custom()
                            .setConnectTimeout(2000)
                            .setSocketTimeout(2000)
                            .build())
                    .build()

            return httpClient

        } catch (NoSuchAlgorithmException ex) {
            log.error("Error while initializing HttpClient ", ex);
        } catch (KeyStoreException ex) {
            log.error("Key Store Exception while initializing HttpClient ", ex);
        } catch (KeyManagementException ex) {
            log.error("Key Management Exception while initializing HttpClient ", ex);
    }

    }
}