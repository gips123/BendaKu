package com.example.bendaku.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bendaku.model.Item;
import com.example.bendaku.repository.ItemRepository;

import java.io.File;
import java.util.List;

public class ItemViewModel extends ViewModel {
    private ItemRepository repository;
    private MutableLiveData<List<Item>> itemsLiveData = new MutableLiveData<>();
    private MutableLiveData<Item> itemLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    
    public ItemViewModel() {
        repository = new ItemRepository();
    }
    
    public LiveData<List<Item>> getItems() {
        return itemsLiveData;
    }
    
    public LiveData<Item> getItem() {
        return itemLiveData;
    }
    
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    public LiveData<Boolean> isLoading() {
        return isLoadingLiveData;
    }
    
    public void loadItems(String type) {
        isLoadingLiveData.setValue(true);
        repository.getItems(type, new ItemRepository.ItemsCallback() {
            @Override
            public void onSuccess(List<Item> items) {
                isLoadingLiveData.setValue(false);
                itemsLiveData.setValue(items);
            }
            
            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }
    
    public void loadItem(int id) {
        isLoadingLiveData.setValue(true);
        repository.getItem(id, new ItemRepository.ItemCallback() {
            @Override
            public void onSuccess(Item item) {
                isLoadingLiveData.setValue(false);
                itemLiveData.setValue(item);
            }
            
            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }
    
    public void createItem(String name, String description, String location, String dateTime,
                          String type, Integer reporterId, String reporterName, String reporterPhone,
                          File imageFile) {
        isLoadingLiveData.setValue(true);
        repository.createItem(name, description, location, dateTime, type, reporterId,
            reporterName, reporterPhone, imageFile, new ItemRepository.ItemCallback() {
                @Override
                public void onSuccess(Item item) {
                    isLoadingLiveData.setValue(false);
                    itemLiveData.setValue(item);
                }
                
                @Override
                public void onError(String error) {
                    isLoadingLiveData.setValue(false);
                    errorLiveData.setValue(error);
                }
            });
    }
    
    public void updateItem(int id, String name, String description, String location,
                          String dateTime, String type, String status) {
        isLoadingLiveData.setValue(true);
        repository.updateItem(id, name, description, location, dateTime, type, status,
            new ItemRepository.ItemCallback() {
                @Override
                public void onSuccess(Item item) {
                    isLoadingLiveData.setValue(false);
                    itemLiveData.setValue(item);
                }
                
                @Override
                public void onError(String error) {
                    isLoadingLiveData.setValue(false);
                    errorLiveData.setValue(error);
                }
            });
    }
    
    public void deleteItem(int id) {
        isLoadingLiveData.setValue(true);
        repository.deleteItem(id, new ItemRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                isLoadingLiveData.setValue(false);
                // Reload items after deletion
                loadItems(null);
            }
            
            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }
}

