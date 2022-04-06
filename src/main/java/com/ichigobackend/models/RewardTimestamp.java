package com.ichigobackend.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ichigobackend.utils.TimeUtils;

/**
 * 
 * @author huylq
 *
 */

@Entity
@Table(name = "reward_timestamp")
public class RewardTimestamp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "available_at")
	private LocalDateTime availableAt;

	@Column(name = "redeemed_at")
	private LocalDateTime redeemedAt;

	@Column(name = "expires_at")
	private LocalDateTime expiresAt;

	public RewardTimestamp() {
	};

	public RewardTimestamp(Integer userId, LocalDateTime availableAt) {
		this.userId = userId;
		this.availableAt = TimeUtils.toMidnight(availableAt);
		this.redeemedAt = null;
		this.expiresAt = this.availableAt.plusDays(1);
	}

	public RewardTimestamp(Integer id, Integer userId, LocalDateTime availableAt, LocalDateTime redeemedAt,
			LocalDateTime expiresAt) {
		this.id = id;
		this.userId = userId;
		this.availableAt = TimeUtils.toMidnight(availableAt);
		this.redeemedAt = redeemedAt;
		this.expiresAt = TimeUtils.toMidnight(expiresAt);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public LocalDateTime getAvailableAt() {
		return availableAt;
	}

	public void setAvailableAt(LocalDateTime availableAt) {
		this.availableAt = availableAt;
	}

	public LocalDateTime getRedeemedAt() {
		return redeemedAt;
	}

	public void setRedeemedAt(LocalDateTime redeemedAt) {
		this.redeemedAt = redeemedAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

}
