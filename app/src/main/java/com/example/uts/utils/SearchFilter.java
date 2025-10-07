package com.example.uts.utils;

import java.util.ArrayList;
import java.util.List;

public class SearchFilter {

    public static class FilterOptions {
        private String searchQuery = "";
        private String category = "";
        private String itemType = ""; // "lost" or "found"
        private String location = "";
        private String dateRange = "";

        // Getters and Setters
        public String getSearchQuery() { return searchQuery; }
        public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getItemType() { return itemType; }
        public void setItemType(String itemType) { this.itemType = itemType; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getDateRange() { return dateRange; }
        public void setDateRange(String dateRange) { this.dateRange = dateRange; }
    }

    public static List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("Elektronik");
        categories.add("Pakaian");
        categories.add("Aksesoris");
        categories.add("Dokumen");
        categories.add("Tas");
        categories.add("Buku");
        categories.add("Lainnya");
        return categories;
    }

    public static List<String> getCommonLocations() {
        List<String> locations = new ArrayList<>();
        locations.add("Gedung A");
        locations.add("Gedung B");
        locations.add("Gedung C");
        locations.add("Perpustakaan");
        locations.add("Kantin");
        locations.add("Parkiran");
        locations.add("Laboratorium");
        locations.add("Auditorium");
        locations.add("Masjid");
        locations.add("Lapangan");
        return locations;
    }

    public static boolean matchesFilter(ItemModel item, FilterOptions filter) {
        // Search query filter
        if (!filter.getSearchQuery().isEmpty()) {
            String query = filter.getSearchQuery().toLowerCase();
            if (!item.getName().toLowerCase().contains(query) &&
                !item.getDescription().toLowerCase().contains(query) &&
                !item.getLocation().toLowerCase().contains(query)) {
                return false;
            }
        }

        // Category filter
        if (!filter.getCategory().isEmpty() && !filter.getCategory().equals("Semua")) {
            if (!item.getCategory().equals(filter.getCategory())) {
                return false;
            }
        }

        // Item type filter
        if (!filter.getItemType().isEmpty()) {
            if (!item.getType().equals(filter.getItemType())) {
                return false;
            }
        }

        // Location filter
        if (!filter.getLocation().isEmpty()) {
            if (!item.getLocation().toLowerCase().contains(filter.getLocation().toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    // Placeholder ItemModel class - this should match your actual item model
    public static class ItemModel {
        private String name, description, location, category, type;

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getLocation() { return location; }
        public String getCategory() { return category; }
        public String getType() { return type; }
    }
}
