package com.market.repository;

import com.market.domain.Authority;
import liquibase.pro.packaged.S;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
