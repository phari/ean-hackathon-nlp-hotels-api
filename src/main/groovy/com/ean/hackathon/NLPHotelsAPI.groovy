package com.ean.hackathon

import com.ean.dropwizardio.EanBundle
import com.ean.hackathon.config.NLPHotelsAPIConfig
import com.ean.hackathon.content.PropertyCache
import com.ean.hackathon.dao.FileBasedPropertyCatalogDAO
import com.ean.hackathon.dao.PropertyCatalogDAO
import com.ean.hackathon.nlp.NLPService
import com.ean.hackathon.nlp.Parser
import com.ean.hackathon.rapid.HttpClientFactory
import com.ean.hackathon.rapid.RapidAPIRESTClient
import com.ean.hackathon.resource.HotelsListResource
import groovy.util.logging.Slf4j
import io.dropwizard.Application
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.views.ViewBundle
import org.apache.http.impl.client.CloseableHttpClient
import org.skife.jdbi.v2.DBI

@Slf4j
class NLPHotelsAPI extends Application<NLPHotelsAPIConfig> {

    static void main(String[] args) {
        new NLPHotelsAPI().run(args)
    }

    @Override
    void initialize(Bootstrap<NLPHotelsAPIConfig> bootstrap) {
        bootstrap.addBundle(new EanBundle<NLPHotelsAPIConfig>())
        bootstrap.addBundle(new ViewBundle<NLPHotelsAPIConfig>());
        bootstrap.addBundle(new ConfiguredAssetsBundle());
    }


    @Override
    void run(NLPHotelsAPIConfig configuration, Environment environment) throws Exception {
        final DBIFactory factory = new DBIFactory()
        //final DBI jdbi = factory.build(environment, configuration.getDatabase(), "eanrapidcontent")
        //final PropertyCatalogDAO propertyCatalogDAO = jdbi.onDemand(PropertyCatalogDAO)
        final FileBasedPropertyCatalogDAO propertyCatalogDAO = new FileBasedPropertyCatalogDAO();

        CloseableHttpClient client = new HttpClientFactory().getObject()
        RapidAPIRESTClient rapidAPIRESTClient = new RapidAPIRESTClient(client)

        //PropertyCache propertyCache = PropertyCache.instance()
        Parser parser = new Parser(propertyCatalogDAO)
        environment.jersey().register(new HotelsListResource(rapidAPIRESTClient, propertyCatalogDAO, parser, new NLPService()))
    }
}
