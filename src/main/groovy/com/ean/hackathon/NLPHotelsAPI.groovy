package com.ean.hackathon

import com.ean.dropwizardio.EanBundle
import com.ean.hackathon.config.NLPHotelsAPIConfig

import com.ean.hackathon.rapid.HttpClientFactory
import com.ean.hackathon.rapid.RapidAPIRESTClient
import com.ean.hackathon.resource.HotelsListResource
import groovy.util.logging.Slf4j
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.views.ViewBundle
import org.apache.http.impl.client.CloseableHttpClient

@Slf4j
class NLPHotelsAPI extends Application<NLPHotelsAPIConfig> {

    static void main(String[] args) {
        new NLPHotelsAPI().run(args)
    }

    @Override
    void initialize(Bootstrap<NLPHotelsAPIConfig> bootstrap) {
        bootstrap.addBundle(new EanBundle<NLPHotelsAPIConfig>())
        bootstrap.addBundle(new ViewBundle<NLPHotelsAPIConfig>());
    }


    @Override
    void run(NLPHotelsAPIConfig configuration, Environment environment) throws Exception {
        CloseableHttpClient client = new HttpClientFactory().getObject()
        RapidAPIRESTClient rapidAPIRESTClient = new RapidAPIRESTClient(client)
        environment.jersey().register(new HotelsListResource(rapidAPIRESTClient))
    }
}
