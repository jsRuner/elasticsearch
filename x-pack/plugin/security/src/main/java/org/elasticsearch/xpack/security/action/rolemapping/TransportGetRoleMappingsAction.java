/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.security.action.rolemapping;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.xpack.core.security.action.rolemapping.GetRoleMappingsAction;
import org.elasticsearch.xpack.core.security.action.rolemapping.GetRoleMappingsRequest;
import org.elasticsearch.xpack.core.security.action.rolemapping.GetRoleMappingsResponse;
import org.elasticsearch.xpack.core.security.authc.support.mapper.ExpressionRoleMapping;
import org.elasticsearch.xpack.security.authc.support.mapper.NativeRoleMappingStore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TransportGetRoleMappingsAction
        extends HandledTransportAction<GetRoleMappingsRequest, GetRoleMappingsResponse> {

    private final NativeRoleMappingStore roleMappingStore;

    @Inject
    public TransportGetRoleMappingsAction(Settings settings, ActionFilters actionFilters,
                                          TransportService transportService, NativeRoleMappingStore nativeRoleMappingStore) {
        super(settings, GetRoleMappingsAction.NAME, transportService, actionFilters,
            GetRoleMappingsRequest::new);
        this.roleMappingStore = nativeRoleMappingStore;
    }

    @Override
    protected void doExecute(final GetRoleMappingsRequest request,
                             final ActionListener<GetRoleMappingsResponse> listener) {
        final Set<String> names;
        if (request.getNames() == null || request.getNames().length == 0) {
            names = null;
        } else {
            names = new HashSet<>(Arrays.asList(request.getNames()));
        }
        this.roleMappingStore.getRoleMappings(names, ActionListener.wrap(
                mappings -> {
                    ExpressionRoleMapping[] array = mappings.toArray(
                            new ExpressionRoleMapping[mappings.size()]
                    );
                    listener.onResponse(new GetRoleMappingsResponse(array));
                },
                listener::onFailure
        ));
    }
}
