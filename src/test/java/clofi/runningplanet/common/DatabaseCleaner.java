package clofi.runningplanet.common;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class DatabaseCleaner {

	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Transactional
	public void truncateAllTables() {
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
		entityManager.createNativeQuery("SHOW TABLES").getResultList().forEach(row -> {
			String tableName = (String)row;
			entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
		});
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
	}
}
