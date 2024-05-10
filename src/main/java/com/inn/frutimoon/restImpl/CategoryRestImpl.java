package com.inn.frutimoon.restImpl;

import com.inn.frutimoon.POJO.Category;
import com.inn.frutimoon.constants.FrutimoonConstants;
import com.inn.frutimoon.rest.CategoryRest;
import com.inn.frutimoon.service.CategoryService;
import com.inn.frutimoon.utils.FrutimoonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryRestImpl implements CategoryRest {

    @Autowired
    CategoryService categoryService;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            return categoryService.addNewCategory(requestMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return FrutimoonUtils.getResponseEntity(FrutimoonConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategories(String filterValue) {
        try{
            return categoryService.getAllCategory(filterValue);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            return categoryService.updateCategory(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return FrutimoonUtils.getResponseEntity(FrutimoonConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
