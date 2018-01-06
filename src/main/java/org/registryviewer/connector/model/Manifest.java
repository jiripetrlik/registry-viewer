package org.registryviewer.connector.model;

import java.util.List;

public class Manifest {
    private int schemaVersion;
    private String mediaType;
    private ManifestConfig config;
    private List<Layer> layers;

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public ManifestConfig getConfig() {
        return config;
    }

    public void setConfig(ManifestConfig config) {
        this.config = config;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }
}
