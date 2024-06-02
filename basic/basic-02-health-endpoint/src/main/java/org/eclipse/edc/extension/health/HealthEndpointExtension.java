package org.eclipse.edc.extension.health;

package com.example.health;

import org.eclipse.edc.connector.api.management.ManagementApiExtension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.configuration.Config;

public class HealthEndpointExtension implements ManagementApiExtension {

    @Inject
    private Config config;

    @Override
    public void initialize() {
         webService.registerResource(new HealthApiController(context.getMonitor()));
    }
}

