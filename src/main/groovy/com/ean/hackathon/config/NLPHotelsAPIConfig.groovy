package com.ean.hackathon.config

import com.ean.dropwizardio.EanServiceConfiguration
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.bundles.assets.AssetsBundleConfiguration
import io.dropwizard.bundles.assets.AssetsConfiguration
import io.dropwizard.db.DataSourceFactory

import javax.validation.Valid
import javax.validation.constraints.NotNull

class NLPHotelsAPIConfig extends EanServiceConfiguration implements AssetsBundleConfiguration {

    @Valid
    @NotNull
    @JsonProperty
    private final AssetsConfiguration assets = new AssetsConfiguration();

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }

    @Valid
    @NotNull
    @JsonProperty("database")
    DataSourceFactory database = new DataSourceFactory()

}