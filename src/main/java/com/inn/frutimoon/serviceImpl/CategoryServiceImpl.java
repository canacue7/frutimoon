package com.inn.frutimoon.serviceImpl;

import com.inn.frutimoon.JWT.JwtFilter;
import com.inn.frutimoon.POJO.Category;
import com.inn.frutimoon.constants.FrutimoonConstants;
import com.inn.frutimoon.dao.CategoryDao;
import com.inn.frutimoon.service.CategoryService;
import com.inn.frutimoon.utils.FrutimoonUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//this is to printo some logs in our console
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, false)){
                    categoryDao.save(getCategoryFromMap(requestMap, false));
                    return FrutimoonUtils.getResponseEntity("Category Added Succesfully", HttpStatus.OK);
                }
            }else{
                return FrutimoonUtils.getResponseEntity(FrutimoonConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return FrutimoonUtils.getResponseEntity(FrutimoonConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if (!validateId){
                return true;
            }
        }
        return false;
    }

    private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd){
        Category category = new Category();
        if(isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try{
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                log.info("Inside If");
                return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, true)){
                    Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
//                    I used optional.isPresent() instead of !optional.isEmpty()
                    if(!optional.isEmpty()){
                        categoryDao.save(getCategoryFromMap(requestMap, true));
                        return FrutimoonUtils.getResponseEntity("Category Updated Succesfully", HttpStatus.OK);
                    }
                    else {
                        return FrutimoonUtils.getResponseEntity("Category id doesn't exist", HttpStatus.OK);
                    }
                }
                return FrutimoonUtils.getResponseEntity(FrutimoonConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
            }else{
                return FrutimoonUtils.getResponseEntity(FrutimoonConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return FrutimoonUtils.getResponseEntity(FrutimoonConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
