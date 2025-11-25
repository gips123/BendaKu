package com.example.bendaku.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bendaku.model.User;
import com.example.bendaku.repository.AuthRepository;

public class AuthViewModel extends ViewModel {
    private AuthRepository repository;
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    
    public AuthViewModel() {
        repository = new AuthRepository();
    }
    
    public LiveData<User> getUser() {
        return userLiveData;
    }
    
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    public LiveData<Boolean> isLoading() {
        return isLoadingLiveData;
    }
    
    public void register(String email, String password, String username, String fullName,
                        String phone, String studentId) {
        isLoadingLiveData.setValue(true);
        repository.register(email, password, username, fullName, phone, studentId,
            new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(User user) {
                    isLoadingLiveData.setValue(false);
                    userLiveData.setValue(user);
                }
                
                @Override
                public void onError(String error) {
                    isLoadingLiveData.setValue(false);
                    errorLiveData.setValue(error);
                }
            });
    }
    
    public void login(String identifier, String password) {
        isLoadingLiveData.setValue(true);
        repository.login(identifier, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoadingLiveData.setValue(false);
                userLiveData.setValue(user);
            }
            
            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }
    
    public void getCurrentUser() {
        isLoadingLiveData.setValue(true);
        repository.getCurrentUser(new AuthRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                isLoadingLiveData.setValue(false);
                userLiveData.setValue(user);
            }
            
            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }
    
    public void logout() {
        repository.logout();
        userLiveData.setValue(null);
    }
}

