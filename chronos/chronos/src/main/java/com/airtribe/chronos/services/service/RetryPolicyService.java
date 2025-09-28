package com.airtribe.chronos.services.service;

import com.airtribe.chronos.dtos.retry.RetryPolicyCreateRequest;
import com.airtribe.chronos.dtos.retry.RetryPolicyDTO;
import com.airtribe.chronos.dtos.retry.RetryPolicyUpdateRequest;

import java.util.List;

public interface RetryPolicyService {

    RetryPolicyDTO createPolicy(RetryPolicyCreateRequest request);

    RetryPolicyDTO updatePolicy(String id, RetryPolicyUpdateRequest request);

    RetryPolicyDTO getPolicyById(String id);

    List<RetryPolicyDTO> getAllPolicies();

    void deletePolicy(String id);
}
