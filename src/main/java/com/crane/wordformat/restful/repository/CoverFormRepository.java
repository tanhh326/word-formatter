package com.crane.wordformat.restful.repository;

import com.crane.wordformat.restful.entity.CoverFormPO;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverFormRepository extends ListPagingAndSortingRepository<CoverFormPO, String> {

}
