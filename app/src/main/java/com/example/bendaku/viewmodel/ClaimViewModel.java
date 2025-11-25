package com.example.bendaku.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bendaku.model.Claim;
import com.example.bendaku.repository.ClaimRepository;

import java.io.File;
import java.util.List;

public class ClaimViewModel extends ViewModel {
    private ClaimRepository repository;
    private MutableLiveData<List<Claim>> claimsLiveData = new MutableLiveData<>();
    private MutableLiveData<Claim> claimLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    
    public ClaimViewModel() {
        repository = new ClaimRepository();
    }
    
    public LiveData<List<Claim>> getClaims() {
        return claimsLiveData;
    }
    
    public LiveData<Claim> getClaim() {
        return claimLiveData;
    }
    
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    public LiveData<Boolean> isLoading() {
        return isLoadingLiveData;
    }
    
    public void loadClaims(String status) {
        isLoadingLiveData.setValue(true);
        repository.getClaims(status, new ClaimRepository.ClaimsCallback() {
            @Override
            public void onSuccess(List<Claim> claims) {
                isLoadingLiveData.setValue(false);
                claimsLiveData.setValue(claims);
            }
            
            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }
    
    public void loadClaim(int id) {
        isLoadingLiveData.setValue(true);
        repository.getClaim(id, new ClaimRepository.ClaimCallback() {
            @Override
            public void onSuccess(Claim claim) {
                isLoadingLiveData.setValue(false);
                claimLiveData.setValue(claim);
            }
            
            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }
    
    public void createClaim(Integer itemId, Integer claimerId, String claimerName,
                           String claimerPhone, String description, File proofImageFile) {
        isLoadingLiveData.setValue(true);
        repository.createClaim(itemId, claimerId, claimerName, claimerPhone, description,
            proofImageFile, new ClaimRepository.ClaimCallback() {
                @Override
                public void onSuccess(Claim claim) {
                    isLoadingLiveData.setValue(false);
                    claimLiveData.setValue(claim);
                }
                
                @Override
                public void onError(String error) {
                    isLoadingLiveData.setValue(false);
                    errorLiveData.setValue(error);
                }
            });
    }
    
    public void approveClaim(int id) {
        isLoadingLiveData.setValue(true);
        repository.approveClaim(id, new ClaimRepository.ClaimCallback() {
            @Override
            public void onSuccess(Claim claim) {
                isLoadingLiveData.setValue(false);
                claimLiveData.setValue(claim);
            }
            
            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }
    
    public void rejectClaim(int id, String adminNotes) {
        isLoadingLiveData.setValue(true);
        repository.rejectClaim(id, adminNotes, new ClaimRepository.ClaimCallback() {
            @Override
            public void onSuccess(Claim claim) {
                isLoadingLiveData.setValue(false);
                claimLiveData.setValue(claim);
            }
            
            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }
}

