package com.airtribe.chronos.services.impl;

import com.airtribe.chronos.dtos.retry.RetryPolicyCreateRequest;
import com.airtribe.chronos.dtos.retry.RetryPolicyDTO;
import com.airtribe.chronos.dtos.retry.RetryPolicyUpdateRequest;
import com.airtribe.chronos.entities.RetryPolicy;
import com.airtribe.chronos.repos.RetryPolicyRepository;
import com.airtribe.chronos.services.service.RetryPolicyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetryPolicyServiceImpl implements RetryPolicyService {

    private final RetryPolicyRepository retryPolicyRepository;

    @Override
    @Transactional
    @CacheEvict(value = "Cache_RetryPolicy_All", allEntries = true)
    public RetryPolicyDTO createPolicy(RetryPolicyCreateRequest request) {
        RetryPolicy policy = RetryPolicy.builder()
                .strategy(request.strategy())
                .backoffInitialMs(request.backoffInitialMs())
                .backoffFactor(request.backoffFactor())
                .maxAttempts(request.maxAttempts())
                .jitterMs(request.jitterMs())
                .build();

        return retryPolicyRepository.save(policy).toDTO();
    }

    @Override
    @Transactional
    @CachePut(value = "Cache_RetryPolicy", key = "#id")
    @CacheEvict(value = "Cache_RetryPolicy_All", allEntries = true)
    public RetryPolicyDTO updatePolicy(String id, RetryPolicyUpdateRequest request) {
        RetryPolicy policy = retryPolicyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RetryPolicy not found: " + id));

        if (request.strategy() != null) policy.setStrategy(request.strategy());
        if (request.backoffInitialMs() != null) policy.setBackoffInitialMs(request.backoffInitialMs());
        if (request.backoffFactor() != null) policy.setBackoffFactor(request.backoffFactor());
        if (request.maxAttempts() != null) policy.setMaxAttempts(request.maxAttempts());
        if (request.jitterMs() != null) policy.setJitterMs(request.jitterMs());

        return retryPolicyRepository.save(policy).toDTO();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_RetryPolicy", key = "#id")
    public RetryPolicyDTO getPolicyById(String id) {
        return retryPolicyRepository.findById(id)
                .map(RetryPolicy::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("RetryPolicy not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "Cache_RetryPolicy_All", key = "'allPolicies'")
    public List<RetryPolicyDTO> getAllPolicies() {
        return retryPolicyRepository.findAll()
                .stream()
                .map(RetryPolicy::toDTO)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"Cache_RetryPolicy", "Cache_RetryPolicy_All"}, key = "#id", allEntries = true)
    public void deletePolicy(String id) {
        if (!retryPolicyRepository.existsById(id)) {
            throw new EntityNotFoundException("RetryPolicy not found: " + id);
        }
        retryPolicyRepository.deleteById(id);
    }
}
