package com.cymark.estatemanagementsystem.service;


import com.cymark.estatemanagementsystem.model.dto.response.DetailedUserViewResponse;
import com.cymark.estatemanagementsystem.model.dto.response.UserHierarchyResponse;

public interface UserHierarchyService {
    UserHierarchyResponse getUserHierarchy(String userId);
    DetailedUserViewResponse getDetailedUserView(String userId);
}