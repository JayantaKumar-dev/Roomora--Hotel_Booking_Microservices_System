package com.propertyservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.propertyservice.entity.PropertyPhotos;

public interface PropertyPhotosRepository extends JpaRepository<PropertyPhotos, Long> {

}
