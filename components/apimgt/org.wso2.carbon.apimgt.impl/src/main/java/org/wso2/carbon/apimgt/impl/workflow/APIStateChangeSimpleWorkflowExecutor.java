/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.apimgt.impl.workflow;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.WorkflowResponse;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dto.WorkflowDTO;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

public class APIStateChangeSimpleWorkflowExecutor extends WorkflowExecutor {
    private static final Log log = LogFactory.getLog(APIStateChangeSimpleWorkflowExecutor.class);
    @Override
    public String getWorkflowType() {
        return WorkflowConstants.WF_TYPE_AM_API_STATE;
    }

    @Override
    public List<WorkflowDTO> getWorkflowDetails(String workflowStatus) throws WorkflowException {
        return Collections.emptyList();
    }

    @Override
    public WorkflowResponse execute(WorkflowDTO workflowDTO) throws WorkflowException {
        workflowDTO.setStatus(WorkflowStatus.APPROVED);
        WorkflowResponse workflowResponse = complete(workflowDTO);
        super.publishEvents(workflowDTO);
        return workflowResponse;
    }

    @Override
    public WorkflowResponse complete(WorkflowDTO workflowDTO) throws WorkflowException {
        APIStateWorkflowDTO apiStateWorkFlowDTO = (APIStateWorkflowDTO) workflowDTO;
        boolean transactionCommitted = false;
        Registry registry = null;
        try {
            // starting of tenant flow is not needed. tenant flow is already started before calling this
            registry = ServiceReferenceHolder.getInstance().getRegistryService()
                    .getGovernanceUserRegistry(apiStateWorkFlowDTO.getInvoker(), apiStateWorkFlowDTO.getTenantId());
            GenericArtifactManager artifactManager = APIUtil.getArtifactManager(registry, APIConstants.API_KEY);
            APIIdentifier apiIdentifier = new APIIdentifier(apiStateWorkFlowDTO.getApiProvider(),
                    apiStateWorkFlowDTO.getApiName(), apiStateWorkFlowDTO.getApiVersion());
            GenericArtifact apiArtifact = APIUtil.getAPIArtifact(apiIdentifier, registry);
            registry.beginTransaction();
            apiArtifact.setAttribute(APIConstants.API_WORKFLOW_STATE_ATTR, WorkflowStatus.APPROVED.toString());
            artifactManager.updateGenericArtifact(apiArtifact);
            registry.commitTransaction();
            transactionCommitted = true;
        } catch (RegistryException e) {
            try {
                registry.rollbackTransaction();
            } catch (RegistryException re) {
                // Throwing an error here would mask the original exception
                log.error("Error while rolling back the transaction ", re);
            }
        } catch (APIManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (!transactionCommitted) {
                    registry.rollbackTransaction();
                }
            } catch (RegistryException ex) {
                //////////////////////////////////////////////////////////////////////////
            }
        }

        return new GeneralWorkflowResponse();
    }
}
