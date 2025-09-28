package com.airtribe.chronos.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.airtribe.chronos.dtos.retry.RetryPolicyCreateRequest;
import com.airtribe.chronos.dtos.retry.RetryPolicyDTO;
import com.airtribe.chronos.dtos.retry.RetryPolicyUpdateRequest;
import com.airtribe.chronos.entities.RetryPolicy;
import com.airtribe.chronos.repos.RetryPolicyRepository;

import jakarta.persistence.EntityNotFoundException;

class RetryPolicyServiceImplTest {

    @Mock
    private RetryPolicyRepository retryPolicyRepository;

    @InjectMocks
    private RetryPolicyServiceImpl retryPolicyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPolicy_shouldReturnDTO() {
        RetryPolicyCreateRequest request = mock(RetryPolicyCreateRequest.class);
        RetryPolicy policy = mock(RetryPolicy.class);
        RetryPolicyDTO dto = mock(RetryPolicyDTO.class);

        when(retryPolicyRepository.save(any(RetryPolicy.class))).thenReturn(policy);
        when(policy.toDTO()).thenReturn(dto);

        RetryPolicyDTO result = retryPolicyService.createPolicy(request);

        assertEquals(dto, result);
        verify(retryPolicyRepository).save(any(RetryPolicy.class));
    }

    @Test
    void updatePolicy_shouldReturnDTO() {
        String id = "policy1";
        RetryPolicyUpdateRequest request = mock(RetryPolicyUpdateRequest.class);
        RetryPolicy policy = mock(RetryPolicy.class);
        RetryPolicyDTO dto = mock(RetryPolicyDTO.class);

        when(retryPolicyRepository.findById(id)).thenReturn(Optional.of(policy));
        when(retryPolicyRepository.save(policy)).thenReturn(policy);
        when(policy.toDTO()).thenReturn(dto);

        RetryPolicyDTO result = retryPolicyService.updatePolicy(id, request);

        assertEquals(dto, result);
        verify(retryPolicyRepository).findById(id);
        verify(retryPolicyRepository).save(policy);
    }

    @Test
    void updatePolicy_shouldThrowEntityNotFoundException() {
        when(retryPolicyRepository.findById(anyString())).thenReturn(Optional.empty());
        RetryPolicyUpdateRequest request = mock(RetryPolicyUpdateRequest.class);
        assertThrows(EntityNotFoundException.class, () ->
                retryPolicyService.updatePolicy("invalid", request));
    }

    @Test
    void getPolicyById_shouldReturnDTO() {
        String id = "policy1";
        RetryPolicy policy = mock(RetryPolicy.class);
        RetryPolicyDTO dto = mock(RetryPolicyDTO.class);

        when(retryPolicyRepository.findById(id)).thenReturn(Optional.of(policy));
        when(policy.toDTO()).thenReturn(dto);

        RetryPolicyDTO result = retryPolicyService.getPolicyById(id);

        assertEquals(dto, result);
    }

    @Test
    void getPolicyById_shouldThrowEntityNotFoundException() {
        when(retryPolicyRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                retryPolicyService.getPolicyById("invalid"));
    }

    @Test
    void getAllPolicies_shouldReturnList() {
        RetryPolicy policy = mock(RetryPolicy.class);
        RetryPolicyDTO dto = mock(RetryPolicyDTO.class);

        when(retryPolicyRepository.findAll()).thenReturn(List.of(policy));
        when(policy.toDTO()).thenReturn(dto);

        List<RetryPolicyDTO> result = retryPolicyService.getAllPolicies();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void deletePolicy_shouldDelete() {
        String id = "policy1";
        when(retryPolicyRepository.existsById(id)).thenReturn(true);

        retryPolicyService.deletePolicy(id);

        verify(retryPolicyRepository).deleteById(id);
    }

    @Test
    void deletePolicy_shouldThrowEntityNotFoundException() {
        when(retryPolicyRepository.existsById(anyString())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () ->
                retryPolicyService.deletePolicy("invalid"));
    }
}
