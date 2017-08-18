package org.leme.hibernate.base.dao.jpa.config;

import org.leme.hibernate.base.dao.TestDbEntity;
import org.leme.hibernate.base.dao.DbJpaCommonEntityDao;
import org.springframework.stereotype.Component;

/**
 * Created by mendzl on 8/17/2017.
 */
@Component("testDbEntityJpaDao")
public class TestDbEntityJpaDao extends DbJpaCommonEntityDao<TestDbEntity, Integer> {}
