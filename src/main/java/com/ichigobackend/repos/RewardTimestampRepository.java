package com.ichigobackend.repos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ichigobackend.models.RewardTimestamp;

/**
 * 
 * @author huylq
 *
 */

public interface RewardTimestampRepository extends JpaRepository<RewardTimestamp, LocalDateTime> {

	@Query(value = "SELECT * FROM reward_timestamp "
			+ "WHERE user_id = :userId AND available_at >= :startDate AND available_at <= :endDate "
			+ "ORDER BY available_at", nativeQuery = true)
	List<RewardTimestamp> getWeeklyRewardsByUserIdAndStartEnd(@Param("userId") Integer userId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query(value = "SELECT * FROM reward_timestamp "
			+ "WHERE user_id = :userId AND available_at = :availableAt", nativeQuery = true)
	RewardTimestamp getRewardByUserIdAt(@Param("userId") Integer userId, @Param("availableAt") LocalDateTime availableAt);

}
