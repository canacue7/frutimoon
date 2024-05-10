package com.inn.frutimoon.serviceImpl;

import com.inn.frutimoon.JWT.JwtFilter;
import com.inn.frutimoon.POJO.Category;
import com.inn.frutimoon.POJO.Product;
import com.inn.frutimoon.constants.FrutimoonConstants;
import com.inn.frutimoon.dao.ProductDao;
import com.inn.frutimoon.service.ProductService;
import com.inn.frutimoon.utils.FrutimoonUtils;
import com.inn.frutimoon.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap,false)){
                    productDao.save(getProductFromMap(requestMap, false));
                    return FrutimoonUtils.getResponseEntity("Product added succesfully", HttpStatus.OK);
                }
                return FrutimoonUtils.getResponseEntity(FrutimoonConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else{
                return FrutimoonUtils.getResponseEntity(FrutimoonConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return FrutimoonUtils.getResponseEntity(FrutimoonConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if (!validateId){
                return true;
            }
        }
        return false;
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));

        Product product = new Product();
        if(isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }else{
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try{
            return new ResponseEntity<>(productDao.getAllProduct(), HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap,true)){
                    Optional <Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if(optional.isPresent()){
                        Product product = getProductFromMap(requestMap, true);
                        product.setStatus(optional.get().getStatus());
                        productDao.save(product);
                        return FrutimoonUtils.getResponseEntity("Product updated succesfully", HttpStatus.OK);
                    }else {
                        return FrutimoonUtils.getResponseEntity("Product not exist", HttpStatus.OK);
                    }
                }else{
                    return FrutimoonUtils.getResponseEntity(FrutimoonConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            }else {
                return FrutimoonUtils.getResponseEntity(FrutimoonConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return FrutimoonUtils.getResponseEntity(FrutimoonConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                Optional optional = productDao.findById(id);
                if(optional.isPresent()){
                    productDao.deleteById(id);
                    return FrutimoonUtils.getResponseEntity("Product deleted succesfully", HttpStatus.OK);
                }
                return FrutimoonUtils.getResponseEntity("Product id does not exist", HttpStatus.OK);
            }else{
                return FrutimoonUtils.getResponseEntity(FrutimoonConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return FrutimoonUtils.getResponseEntity(FrutimoonConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                if(optional.isPresent()){
                    productDao.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return FrutimoonUtils.getResponseEntity("Product status updated succesfully", HttpStatus.OK);
                }
                return FrutimoonUtils.getResponseEntity("Product id doesn't exist", HttpStatus.OK);
            }else {
                return FrutimoonUtils.getResponseEntity(FrutimoonConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return FrutimoonUtils.getResponseEntity(FrutimoonConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try{
            return new ResponseEntity<>(productDao.getAllProductByCategory(id), HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductById(id), HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
