/*
*  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.apimgt.rest.api.store.v1.mappings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.io.IOUtils;
import org.wso2.carbon.apimgt.api.APIDefinition;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.Scope;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.definitions.OASParserUtil;
import org.wso2.carbon.apimgt.impl.dto.Environment;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.SettingsDTO;
import org.wso2.carbon.apimgt.rest.api.util.RestApiConstants;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettingsMappingUtil {

    private static final Log log = LogFactory.getLog(SettingsMappingUtil.class);

    public SettingsDTO fromSettingstoDTO(Boolean isUserAvailable) throws APIManagementException {
        SettingsDTO settingsDTO = new SettingsDTO();
        if (isUserAvailable) {
            settingsDTO.setGrantTypes(APIUtil.getGrantTypes());
            settingsDTO.setScopes(GetScopeList());
            settingsDTO.setApplicationSharingEnabled(APIUtil.isMultiGroupAppSharingEnabled());
            settingsDTO.setMapExistingAuthApps(APIUtil.isMapExistingAuthAppsEnabled());
            Map<String, Environment> environments = APIUtil.getEnvironments();
            Environment environment = environments.get(RestApiConstants.DEFAULT_ENVIRONMENT);
            settingsDTO.apiGatewayEndpoint(environment.getApiGatewayEndpoint());
        } else {
            settingsDTO.setScopes(GetScopeList());
            settingsDTO.setApplicationSharingEnabled(APIUtil.isMultiGroupAppSharingEnabled());
            settingsDTO.setMapExistingAuthApps(APIUtil.isMapExistingAuthAppsEnabled());
        }
        return settingsDTO;
    }

    private List<String> GetScopeList() throws APIManagementException {
        String definition = null;
        try {
            definition = IOUtils
                    .toString(RestApiUtil.class.getResourceAsStream("/store-api.yaml"), "UTF-8");
        } catch (IOException e) {
            log.error("Error while reading the swagger definition", e);
        }
        APIDefinition oasParser = OASParserUtil.getOASParser(definition);
        Set<Scope> scopeSet = oasParser.getScopes(definition);
        List<String> scopeList = new ArrayList<>();
        for (Scope entry : scopeSet) {
            scopeList.add(entry.getKey());
        }
        return scopeList;
    }
}